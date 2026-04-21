import { endpoints } from "./endpoints"
import { pyFetch, handleAuthRejection, handleQuotaRejection } from "@/lib/api/py"

const TARGET_SAMPLE_RATE = 16000

function downsample(buffer: Float32Array, inputRate: number) {
  if (inputRate === TARGET_SAMPLE_RATE) return buffer
  const ratio = inputRate / TARGET_SAMPLE_RATE
  const length = Math.round(buffer.length / ratio)
  const result = new Float32Array(length)
  let offsetResult = 0
  let offsetBuffer = 0
  while (offsetResult < result.length) {
    const nextOffsetBuffer = Math.round((offsetResult + 1) * ratio)
    let accum = 0
    let count = 0
    for (let i = offsetBuffer; i < nextOffsetBuffer && i < buffer.length; i++) {
      accum += buffer[i]
      count++
    }
    result[offsetResult] = accum / count
    offsetResult++
    offsetBuffer = nextOffsetBuffer
  }
  return result
}

export class AsrRecorder {
  private audioCtx: AudioContext | null = null
  private mediaStream: MediaStream | null = null
  private sourceNode: MediaStreamAudioSourceNode | null = null
  private processorNode: ScriptProcessorNode | null = null
  private chunks: Float32Array[] = []
  private timer: ReturnType<typeof setInterval> | null = null
  private _recording = false

  get recording() { return this._recording }

  async ensureMic(): Promise<boolean> {
    if (this.mediaStream && this.mediaStream.getTracks().some((t) => t.readyState === "live")) {
      if (!this.audioCtx || this.audioCtx.state === "closed")
        this.audioCtx = new AudioContext()
      else if (this.audioCtx.state === "suspended")
        await this.audioCtx.resume()
      return true
    }
    try {
      this.mediaStream = await navigator.mediaDevices.getUserMedia({ audio: { channelCount: 1 } })
      this.audioCtx = new AudioContext()
      return true
    } catch {
      return false
    }
  }

  release() {
    this.processorNode?.disconnect()
    this.processorNode = null
    this.sourceNode?.disconnect()
    this.sourceNode = null
    this.audioCtx?.close()
    this.audioCtx = null
    this.mediaStream?.getTracks().forEach((t) => t.stop())
    this.mediaStream = null
    if (this.timer) { clearInterval(this.timer); this.timer = null }
    this._recording = false
  }

  async startRecording(onTick: (sec: number) => void): Promise<boolean> {
    const ok = await this.ensureMic()
    if (!ok) return false
    if (this.audioCtx!.state === "suspended") await this.audioCtx!.resume()
    this.sourceNode = this.audioCtx!.createMediaStreamSource(this.mediaStream!)
    this.processorNode = this.audioCtx!.createScriptProcessor(4096, 1, 1)
    this.chunks = []
    this.processorNode.onaudioprocess = (e) => {
      const raw = e.inputBuffer.getChannelData(0)
      this.chunks.push(downsample(raw, this.audioCtx!.sampleRate))
    }
    this.sourceNode.connect(this.processorNode)
    this.processorNode.connect(this.audioCtx!.destination)
    this._recording = true
    let sec = 0
    if (this.timer) clearInterval(this.timer)
    this.timer = setInterval(() => { sec++; onTick(sec) }, 1000)
    return true
  }

  async stopAndRecognize(asrModelId: string): Promise<{ text: string } | { error: string }> {
    this._recording = false
    if (this.timer) { clearInterval(this.timer); this.timer = null }
    this.processorNode?.disconnect()
    this.sourceNode?.disconnect()
    if (!this.chunks.length) return { error: "无音频" }
    const totalLen = this.chunks.reduce((sum, c) => sum + c.length, 0)
    const allSamples = new Float32Array(totalLen)
    let offset = 0
    this.chunks.forEach((c) => { allSamples.set(c, offset); offset += c.length })
    this.chunks = []
    try {
      const res = await pyFetch(`${endpoints.asrOffline()}?model=${asrModelId}`, {
        method: "POST",
        headers: { "Content-Type": "application/octet-stream" },
        body: allSamples.buffer,
      })
      if (await handleAuthRejection(res)) return { error: "登录已失效" }
      if (await handleQuotaRejection(res)) return { error: "已达额度限制" }
      const data = await res.json()
      if (data.error) return { error: data.error }
      if (!res.ok) return { error: `ASR ${res.status}` }
      const text = (data.text || "").trim()
      return text ? { text } : { error: "未识别到内容" }
    } catch (e: unknown) {
      return { error: e instanceof Error ? e.message : "识别失败" }
    }
  }
}
