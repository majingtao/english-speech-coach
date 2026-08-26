import { endpoints } from "./endpoints"
import { pyFetch, handleAuthRejection, handleQuotaRejection } from "@/lib/api/py"
import type { TtsVoiceInfo } from "@/lib/types/speech"

let ttsUnlocked = false
let ttsAudioUnlocked = false
let _ttsAudioCtx: AudioContext | null = null
let _ttsSourceNode: AudioBufferSourceNode | null = null
let _ttsResolve: (() => void) | null = null
let _ttsObjectUrl: string | null = null
let _ttsAbortController: AbortController | null = null
let _ttsRunId = 0

const ttsAudio = typeof window !== "undefined" ? new Audio() : null
if (ttsAudio) {
  ttsAudio.autoplay = false
  ttsAudio.addEventListener("ended", () => {})
}

function _getTtsAudioCtx() {
  if (!_ttsAudioCtx || _ttsAudioCtx.state === "closed")
    _ttsAudioCtx = new (window.AudioContext || (window as never as { webkitAudioContext: typeof AudioContext }).webkitAudioContext)()
  if (_ttsAudioCtx.state === "suspended")
    _ttsAudioCtx.resume().catch(() => {})
  return _ttsAudioCtx
}

function isAppleTouchDevice() {
  if (typeof window === "undefined") return false
  const nav = window.navigator
  return /iPad|iPhone|iPod/.test(nav.userAgent) || (nav.platform === "MacIntel" && nav.maxTouchPoints > 1)
}

export function unlockAudio() {
  if (typeof window === "undefined") return
  const ctx = _getTtsAudioCtx()
  try {
    const src = ctx.createBufferSource()
    src.buffer = ctx.createBuffer(1, 1, ctx.sampleRate)
    src.connect(ctx.destination)
    src.start(0)
  } catch {}
  if (!ttsUnlocked && "speechSynthesis" in window) {
    const utter = new SpeechSynthesisUtterance("")
    utter.volume = 0
    utter.lang = "en-US"
    window.speechSynthesis.speak(utter)
    ttsUnlocked = true
  }
  if (ttsAudio && !ttsAudioUnlocked) {
    ttsAudio.src =
      "data:audio/mp3;base64,SUQzBAAAAAAAI1RTU0UAAAAPAAADTGF2ZjU4Ljc2LjEwMAAAAAAAAAAAAAAA//tQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAWGluZwAAAA8AAAACAAABhgC7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7//////////////////////////////////////////////////////////////////8AAAAATGF2YzU4LjEzAAAAAAAAAAAAAAAAJAAAAAAAAAAABIYGAAAAAAAAAAAAAAAAAAAA//tQZAAP8AAAaQAAAAgAAA0gAAABAAABpAAAACAAADSAAAAETEFNRTMuMTAwVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV//tQZB4P8AAAaQAAAAgAAA0gAAABAAABpAAAACAAADSAAAAEVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVQ=="
    ttsAudio.volume = 0
    ttsAudio
      .play()
      .then(() => {
        if (!ttsAudio) return
        ttsAudio.pause()
        ttsAudio.volume = 1
        ttsAudioUnlocked = true
      })
      .catch(() => {})
  }
}

export function extractEnglishText(text: string) {
  return text
    .split("\n")
    .filter((line) => {
      const t = line.trim()
      if (!t) return false
      return ((t.match(/[\u4E00-\u9FFF]/g) || []).length / t.length) < 0.3
    })
    .join(". ")
    .replace(/#{1,6}\s*/g, "")
    .replace(/\*{1,3}([^*]+)\*{1,3}/g, "$1")
    .replace(/`([^`]+)`/g, "$1")
    .replace(/[_~>|]/g, "")
    .replace(/^\d+\.\s*/gm, "")
    .replace(/^[-*]\s*/gm, "")
    .replace(/\s{2,}/g, " ")
    .trim()
}

export function stopTts() {
  _ttsRunId += 1
  _ttsAbortController?.abort()
  _ttsAbortController = null
  if (typeof window !== "undefined" && "speechSynthesis" in window)
    window.speechSynthesis.cancel()
  if (ttsAudio) {
    ttsAudio.pause()
    ttsAudio.currentTime = 0
  }
  if (_ttsObjectUrl) {
    URL.revokeObjectURL(_ttsObjectUrl)
    _ttsObjectUrl = null
  }
  if (_ttsSourceNode) {
    try { _ttsSourceNode.stop() } catch {}
    try { _ttsSourceNode.disconnect() } catch {}
    _ttsSourceNode = null
  }
  if (_ttsResolve) {
    _ttsResolve()
    _ttsResolve = null
  }
}

async function playWithHtmlAudio(arrayBuf: ArrayBuffer, contentType: string) {
  if (!ttsAudio) throw new Error("HTMLAudio is not available")
  if (_ttsObjectUrl) URL.revokeObjectURL(_ttsObjectUrl)
  _ttsObjectUrl = URL.createObjectURL(new Blob([arrayBuf], { type: contentType || "audio/mpeg" }))
  await new Promise<void>((resolve, reject) => {
    _ttsResolve = resolve
    const done = () => {
      ttsAudio.removeEventListener("ended", done)
      ttsAudio.removeEventListener("error", fail)
      if (_ttsObjectUrl) {
        URL.revokeObjectURL(_ttsObjectUrl)
        _ttsObjectUrl = null
      }
      if (_ttsResolve === resolve) _ttsResolve = null
      resolve()
    }
    const fail = () => {
      ttsAudio.removeEventListener("ended", done)
      ttsAudio.removeEventListener("error", fail)
      if (_ttsObjectUrl) {
        URL.revokeObjectURL(_ttsObjectUrl)
        _ttsObjectUrl = null
      }
      if (_ttsResolve === resolve) _ttsResolve = null
      reject(new Error("HTMLAudio playback failed"))
    }
    ttsAudio.addEventListener("ended", done, { once: true })
    ttsAudio.addEventListener("error", fail, { once: true })
    ttsAudio.src = _ttsObjectUrl!
    ttsAudio.volume = 1
    ttsAudio.currentTime = 0
    ttsAudio.play().catch(fail)
  })
}

export async function speakWithSystem(text: string, voiceName: string) {
  if (typeof window === "undefined" || !("speechSynthesis" in window)) return
  stopTts()
  const toSpeak = extractEnglishText(text)
  if (!toSpeak) return
  await new Promise<void>((resolve) => {
    _ttsResolve = resolve
    const voice = window.speechSynthesis.getVoices().find((v) => v.name === voiceName)
    const parts = toSpeak.match(/[^.!?]+[.!?]+|[^.!?]+$/g) || [toSpeak]
    parts.forEach((content, idx) => {
      const utter = new SpeechSynthesisUtterance(content)
      utter.lang = "en-US"
      utter.rate = 0.9
      if (voice) utter.voice = voice
      utter.onend = () => {
        if (idx === parts.length - 1) {
          if (_ttsResolve === resolve) _ttsResolve = null
          resolve()
        }
      }
      window.speechSynthesis.speak(utter)
    })
  })
}

export async function speakWithServer(
  text: string,
  engine: string,
  voiceName: string,
  callbacks: {
    onLoadingChange: (v: boolean) => void
    onSpeakingChange: (v: boolean) => void
  },
) {
  stopTts()
  const runId = _ttsRunId
  const toSpeak = extractEnglishText(text)
  if (!toSpeak) return
  unlockAudio()
  callbacks.onLoadingChange(true)

  const body: Record<string, unknown> = { engine, text: toSpeak }
  if (engine === "piper") body.speed = 1.0
  else if (engine === "edge") { body.voice = voiceName || "en-US-AnaNeural"; body.rate = "-10%" }
  else if (engine === "vibevoice") { body.voice = voiceName || "en-Carter_man"; body.cfg_scale = 1.5 }
  else if (engine === "qwen-tts") { body.voice = voiceName || "Chelsie" }

  const ctrl = new AbortController()
  _ttsAbortController = ctrl
  let timedOut = false
  const timer = setTimeout(() => {
    timedOut = true
    ctrl.abort()
  }, 45000)
  const isCurrentRun = () => runId === _ttsRunId && !ctrl.signal.aborted
  try {
    const res = await pyFetch(endpoints.tts(), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
      signal: ctrl.signal,
    })
    clearTimeout(timer)
    if (await handleAuthRejection(res)) { if (isCurrentRun()) callbacks.onLoadingChange(false); return }
    if (!res.ok) throw new Error(`TTS 请求失败 ${res.status}`)
    if (await handleQuotaRejection(res)) { if (isCurrentRun()) callbacks.onLoadingChange(false); return }
    const arrayBuf = await res.arrayBuffer()
    if (!isCurrentRun() || arrayBuf.byteLength < 256) return

    // iOS Safari is more reliable with the native MP3 decoder than with
    // decoding the response through Web Audio after an async fetch.
    if (isAppleTouchDevice()) {
      callbacks.onLoadingChange(false)
      callbacks.onSpeakingChange(true)
      try {
        await playWithHtmlAudio(arrayBuf, res.headers.get("content-type") || "audio/mpeg")
        if (isCurrentRun()) callbacks.onSpeakingChange(false)
        return
      } catch (err) {
        if (!isCurrentRun()) return
        callbacks.onSpeakingChange(false)
        console.warn("[tts] native audio playback failed, trying Web Audio", err)
      }
    }

    const ctx = _getTtsAudioCtx()
    if (ctx.state === "suspended") await ctx.resume()
    if (!isCurrentRun()) return
    if (ctx.state === "suspended") {
      callbacks.onLoadingChange(false)
      callbacks.onSpeakingChange(true)
      await playWithHtmlAudio(arrayBuf, res.headers.get("content-type") || "audio/mpeg")
      if (isCurrentRun()) callbacks.onSpeakingChange(false)
      return
    }
    let audioBuf: AudioBuffer
    try {
      audioBuf = await ctx.decodeAudioData(arrayBuf.slice(0))
    } catch (err) {
      if (!isCurrentRun()) return
      callbacks.onLoadingChange(false)
      callbacks.onSpeakingChange(true)
      try {
        await playWithHtmlAudio(arrayBuf, res.headers.get("content-type") || "audio/mpeg")
        if (isCurrentRun()) callbacks.onSpeakingChange(false)
        return
      } catch (fallbackErr) {
        callbacks.onSpeakingChange(false)
        console.warn("[tts] Web Audio decode failed and native fallback failed", err, fallbackErr)
        throw fallbackErr
      }
    }
    if (!isCurrentRun()) return
    callbacks.onLoadingChange(false)
    try {
      await new Promise<void>((resolve) => {
        _ttsResolve = resolve
        const done = () => {
          if (_ttsSourceNode === src) _ttsSourceNode = null
          if (isCurrentRun()) callbacks.onSpeakingChange(false)
          if (_ttsResolve === resolve) _ttsResolve = null
          resolve()
        }
        const src = ctx.createBufferSource()
        src.buffer = audioBuf
        src.connect(ctx.destination)
        src.onended = done
        _ttsSourceNode = src
        callbacks.onSpeakingChange(true)
        src.start()
      })
    } catch {
      if (!isCurrentRun()) return
      callbacks.onSpeakingChange(true)
      await playWithHtmlAudio(arrayBuf, res.headers.get("content-type") || "audio/mpeg")
      if (isCurrentRun()) callbacks.onSpeakingChange(false)
    }
  } catch (err) {
    if (ctrl.signal.aborted) {
      if (timedOut) throw new Error("语音请求超时，请重试")
      return
    }
    console.warn("[tts] playback failed", err)
    throw err
  } finally {
    clearTimeout(timer)
    if (_ttsAbortController === ctrl) _ttsAbortController = null
    if (isCurrentRun()) callbacks.onLoadingChange(false)
  }
}

export function getSystemVoices(): TtsVoiceInfo[] {
  if (typeof window === "undefined" || !("speechSynthesis" in window)) return []
  return window.speechSynthesis
    .getVoices()
    .filter((v) => v.lang?.startsWith("en"))
    .map((v) => ({ name: v.name, label: `${v.name} (${v.lang || "en"})` }))
}

export async function fetchVoices(engine: string): Promise<TtsVoiceInfo[]> {
  try {
    const res = await fetch(`${endpoints.ttsVoices()}?engine=${engine}`)
    if (res.ok) return await res.json()
  } catch {}
  return []
}
