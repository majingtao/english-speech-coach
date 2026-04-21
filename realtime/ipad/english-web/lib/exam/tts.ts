import { endpoints } from "./endpoints"
import { pyFetch, handleAuthRejection, handleQuotaRejection } from "@/lib/api/py"
import type { TtsVoiceInfo } from "@/lib/types/speech"

let ttsUnlocked = false
let ttsAudioUnlocked = false
let _ttsAudioCtx: AudioContext | null = null
let _ttsSourceNode: AudioBufferSourceNode | null = null
let _ttsResolve: (() => void) | null = null

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

export function unlockAudio() {
  if (typeof window === "undefined") return
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
  if (typeof window !== "undefined" && "speechSynthesis" in window)
    window.speechSynthesis.cancel()
  if (ttsAudio) {
    ttsAudio.pause()
    ttsAudio.currentTime = 0
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

export async function speakWithSystem(text: string, voiceName: string) {
  if (typeof window === "undefined" || !("speechSynthesis" in window)) return
  window.speechSynthesis.cancel()
  if (_ttsResolve) { _ttsResolve(); _ttsResolve = null }
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
  const timer = setTimeout(() => ctrl.abort(), 45000)
  try {
    const res = await pyFetch(endpoints.tts(), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
      signal: ctrl.signal,
    })
    clearTimeout(timer)
    if (await handleAuthRejection(res)) { callbacks.onLoadingChange(false); return }
    if (!res.ok) throw new Error(`TTS 请求失败 ${res.status}`)
    if (await handleQuotaRejection(res)) { callbacks.onLoadingChange(false); return }
    const arrayBuf = await res.arrayBuffer()
    if (arrayBuf.byteLength < 256) return
    const ctx = _getTtsAudioCtx()
    const audioBuf = await ctx.decodeAudioData(arrayBuf.slice(0))
    callbacks.onLoadingChange(false)
    await new Promise<void>((resolve) => {
      _ttsResolve = resolve
      const done = () => {
        _ttsSourceNode = null
        callbacks.onSpeakingChange(false)
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
  } finally {
    clearTimeout(timer)
    callbacks.onLoadingChange(false)
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
