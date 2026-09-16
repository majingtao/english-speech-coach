import { endpoints } from "./endpoints"
import { pyFetch, handleAuthRejection, handleQuotaRejection } from "@/lib/api/py"

const TARGET_SAMPLE_RATE = 16000

function downsample(buffer: Float32Array, inputRate: number) {
  if (inputRate === TARGET_SAMPLE_RATE) return buffer.slice()
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
  private _lastError = ""
  private pendingAudio: Float32Array | null = null
  private request: AbortController | null = null
  private generation = 0

  get recording() { return this._recording }
  get active() { return this._recording && this.audioCtx?.state === "running" && !!this.mediaStream?.getTracks().some((track) => track.readyState === "live") }
  get lastError() { return this._lastError }
  get hasPendingAudio() { return !!this.pendingAudio || this.chunks.length > 0 }

  async ensureMic(): Promise<boolean> {
    const generation = this.generation
    this._lastError = ""
    if (!window.isSecureContext) {
      this._lastError = "当前页面不是可信 HTTPS 连接，请安装并完全信任 EnglishAI 根证书"
      return false
    }
    if (!navigator.mediaDevices?.getUserMedia) {
      this._lastError = "Safari 未提供麦克风接口，请检查网站权限和证书信任"
      return false
    }
    if (this.mediaStream && this.mediaStream.getTracks().some((t) => t.readyState === "live")) {
      if (!this.audioCtx || this.audioCtx.state === "closed")
        this.audioCtx = new AudioContext()
      else if (this.audioCtx.state === "suspended")
        await this.audioCtx.resume()
      return true
    }
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: { channelCount: 1 } })
      if (generation !== this.generation) {
        stream.getTracks().forEach((track) => track.stop())
        return false
      }
      this.mediaStream = stream
      this.audioCtx = new AudioContext()
      return true
    } catch (error: unknown) {
      const name = error instanceof DOMException ? error.name : ""
      this._lastError = name === "NotAllowedError"
        ? "麦克风访问被拒绝，请在 Safari 的网站设置中允许麦克风"
        : name === "NotFoundError"
          ? "没有检测到可用的麦克风"
          : error instanceof Error
            ? `麦克风启动失败：${error.message}`
            : "麦克风启动失败"
      return false
    }
  }

  pause() {
    this.processorNode?.disconnect()
    this.processorNode = null
    this.sourceNode?.disconnect()
    this.sourceNode = null
    void this.audioCtx?.close().catch(() => undefined)
    this.audioCtx = null
    this.mediaStream?.getTracks().forEach((t) => t.stop())
    this.mediaStream = null
    if (this.timer) { clearInterval(this.timer); this.timer = null }
    this._recording = false
  }

  release() {
    this.generation++
    this.request?.abort()
    this.request = null
    this.pause()
    this.chunks = []
    this.pendingAudio = null
  }

  async startRecording(onTick: (sec: number) => void, onLevel?: (level: number) => void): Promise<boolean> {
    const generation = this.generation
    const ok = await this.ensureMic()
    if (!ok || generation !== this.generation) return false
    if (this.audioCtx!.state === "suspended") await this.audioCtx!.resume()
    if (generation !== this.generation) return false
    this.sourceNode = this.audioCtx!.createMediaStreamSource(this.mediaStream!)
    this.processorNode = this.audioCtx!.createScriptProcessor(4096, 1, 1)
    this.chunks = []
    this.pendingAudio = null
    this.processorNode.onaudioprocess = (e) => {
      const raw = e.inputBuffer.getChannelData(0)
      this.chunks.push(downsample(raw, this.audioCtx!.sampleRate))
      if (onLevel) {
        const rms = Math.sqrt(raw.reduce((sum, sample) => sum + sample * sample, 0) / raw.length)
        onLevel(Math.min(1, rms * 8))
      }
    }
    this.sourceNode.connect(this.processorNode)
    this.processorNode.connect(this.audioCtx!.destination)
    this._recording = true
    let sec = 0
    if (this.timer) clearInterval(this.timer)
    this.timer = setInterval(() => { sec++; onTick(sec) }, 1000)
    return true
  }

  async stopAndRecognize(asrModelId: string, retainAudio = false): Promise<{ text: string; audio?: Blob } | { error: string }> {
    if (this.request) return { error: "正在识别，请稍候" }
    this.pause()
    if (!this.hasPendingAudio) return { error: "无音频" }
    if (!this.pendingAudio) {
      const totalLen = this.chunks.reduce((sum, c) => sum + c.length, 0)
      const allSamples = new Float32Array(totalLen)
      let offset = 0
      this.chunks.forEach((c) => { allSamples.set(c, offset); offset += c.length })
      this.chunks = []
      this.pendingAudio = allSamples
    }
    const audio = this.pendingAudio
    const request = new AbortController()
    this.request = request
    const timeout = setTimeout(() => request.abort(), 45000)
    try {
      const res = await pyFetch(`${endpoints.asrOffline()}?model=${asrModelId}`, {
        method: "POST",
        headers: { "Content-Type": "application/octet-stream" },
        body: audio.buffer as ArrayBuffer,
        signal: request.signal,
      })
      if (await handleAuthRejection(res)) return { error: "登录已失效" }
      if (await handleQuotaRejection(res)) return { error: "已达额度限制" }
      const raw = await res.text()
      let data: { error?: string; text?: string } = {}
      try {
        data = raw ? JSON.parse(raw) as { error?: string; text?: string } : {}
      } catch {
        if (res.status === 413 || raw.startsWith("Maximum request body size")) {
          return { error: "录音过长，音频上传超过服务限制" }
        }
        return { error: raw.trim() || `ASR ${res.status}` }
      }
      if (data.error) return { error: data.error }
      if (!res.ok) return { error: `ASR ${res.status}` }
      const text = (data.text || "").trim()
      if (text && this.pendingAudio === audio) this.pendingAudio = null
      if (!text) return { error: "未识别到内容" }
      if (!retainAudio) return { text }
      const wav = new ArrayBuffer(44 + audio.length * 2)
      const view = new DataView(wav)
      const label = (offset: number, value: string) => { for (let i = 0; i < value.length; i++) view.setUint8(offset + i, value.charCodeAt(i)) }
      label(0, "RIFF"); view.setUint32(4, 36 + audio.length * 2, true); label(8, "WAVE")
      label(12, "fmt "); view.setUint32(16, 16, true); view.setUint16(20, 1, true); view.setUint16(22, 1, true)
      view.setUint32(24, TARGET_SAMPLE_RATE, true); view.setUint32(28, TARGET_SAMPLE_RATE * 2, true)
      view.setUint16(32, 2, true); view.setUint16(34, 16, true); label(36, "data"); view.setUint32(40, audio.length * 2, true)
      for (let i = 0; i < audio.length; i++) { const value = Math.max(-1, Math.min(1, audio[i])); view.setInt16(44 + i * 2, value * (value < 0 ? 32768 : 32767), true) }
      return { text, audio: new Blob([wav], { type: "audio/wav" }) }
    } catch (e: unknown) {
      return { error: request.signal.aborted ? "识别超时或已取消，可重试这段录音" : e instanceof Error ? e.message : "识别失败" }
    } finally {
      clearTimeout(timeout)
      if (this.request === request) this.request = null
    }
  }
}
