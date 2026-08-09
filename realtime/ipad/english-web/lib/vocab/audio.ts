import { speakWithServer, stopTts, unlockAudio } from "@/lib/exam/tts"
import { ensureVocabAudio, type VocabAccent } from "@/lib/api/vocab"

const STREAM_ENGINE = "edge"
const STREAM_VOICE_BY_ACCENT: Record<VocabAccent, string> = {
  uk: "en-GB-LibbyNeural",
  us: "en-US-AnaNeural",
}

let _vocabAudio: HTMLAudioElement | null = null

function getAudio(): HTMLAudioElement | null {
  if (typeof window === "undefined") return null
  if (!_vocabAudio) _vocabAudio = new Audio()
  return _vocabAudio
}

type Callbacks = {
  onLoadingChange?: (v: boolean) => void
  onSpeakingChange?: (v: boolean) => void
}

/**
 * 直接按 word 走流式 Edge-TTS（不经过缓存）。
 * 用于例句、临时朗读等无 vocabId 的场景。
 */
export async function playWord(
  word: string,
  callbacks: Callbacks = {},
  accent: VocabAccent = "us",
) {
  if (!word) return
  unlockAudio()
  await speakWithServer(word, STREAM_ENGINE, STREAM_VOICE_BY_ACCENT[accent], {
    onLoadingChange: callbacks.onLoadingChange ?? (() => {}),
    onSpeakingChange: callbacks.onSpeakingChange ?? (() => {}),
  })
}

/**
 * 词条发音：优先播放已缓存 URL；URL 为空时调后端懒生成；失败降级流式 TTS。
 */
export async function playVocab(
  args: { id: number; word: string; url?: string | null; accent?: VocabAccent },
  callbacks: Callbacks = {},
) {
  const audio = getAudio()
  if (!audio) return
  unlockAudio()
  audio.pause()
  audio.currentTime = 0

  const accent: VocabAccent = args.accent ?? "uk"
  callbacks.onLoadingChange?.(true)

  let url = args.url ?? null
  if (!url && args.id) {
    try {
      url = await ensureVocabAudio(args.id, accent)
    } catch {
      callbacks.onLoadingChange?.(false)
      return playWord(args.word, callbacks, accent)
    }
  }
  if (!url) {
    callbacks.onLoadingChange?.(false)
    return playWord(args.word, callbacks, accent)
  }

  audio.src = url
  await new Promise<void>((resolve) => {
    const cleanup = () => {
      audio.removeEventListener("ended", onEnd)
      audio.removeEventListener("error", onErr)
      callbacks.onSpeakingChange?.(false)
    }
    const onEnd = () => {
      cleanup()
      resolve()
    }
    const onErr = () => {
      cleanup()
      resolve()
    }
    audio.addEventListener("ended", onEnd, { once: true })
    audio.addEventListener("error", onErr, { once: true })
    callbacks.onLoadingChange?.(false)
    callbacks.onSpeakingChange?.(true)
    audio.play().catch(onErr)
  })
}

export function stopWord() {
  if (_vocabAudio) {
    _vocabAudio.pause()
    _vocabAudio.currentTime = 0
  }
  stopTts()
}
