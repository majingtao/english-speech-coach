"use client"

import { useEffect, useRef, useState } from "react"
import { AsrRecorder } from "./asr"
import { stopTts, unlockAudio } from "./tts"

type Options = {
  modelId: string
  onText: (text: string) => void
  onError: (message: string) => void
  onAudio?: (audio: Blob, originalText: string) => void
  maxSeconds?: number
}

export function useSpeechRecorder(options: Options) {
  const recorder = useRef<AsrRecorder | null>(null)
  const callbacks = useRef(options)
  const operation = useRef(0)
  const locked = useRef(false)
  const [recording, setRecording] = useState(false)
  const [busy, setBusy] = useState(false)
  const [seconds, setSeconds] = useState(0)
  const [level, setLevel] = useState(0)
  const [canRetry, setCanRetry] = useState(false)
  const [notice, setNotice] = useState("")

  useEffect(() => { callbacks.current = options }, [options])

  useEffect(() => {
    const lifecycle = operation
    const pause = () => {
      if (document.visibilityState !== "hidden") return
      if (recorder.current?.recording) {
        recorder.current.pause()
        setRecording(false)
        setLevel(0)
        setCanRetry(recorder.current.hasPendingAudio)
        setNotice("录音已暂停，返回后可识别已录内容或重新录音")
      } else if (locked.current && !recorder.current?.hasPendingAudio) {
        // Cancel a microphone permission request that finishes after hiding.
        operation.current++
        recorder.current?.release()
        locked.current = false
        setBusy(false)
        setNotice("麦克风启动已暂停，返回后请重新开始录音")
      }
      stopTts()
    }
    document.addEventListener("visibilitychange", pause)
    return () => {
      lifecycle.current++
      recorder.current?.release()
      document.removeEventListener("visibilitychange", pause)
    }
  }, [])

  function reset() {
    operation.current++
    locked.current = false
    recorder.current?.release()
    setRecording(false)
    setBusy(false)
    setCanRetry(false)
    setSeconds(0)
    setLevel(0)
    setNotice("")
  }

  async function recognize() {
    if (locked.current || !recorder.current) return
    locked.current = true
    const id = ++operation.current
    setBusy(true)
    setRecording(false)
    setLevel(0)
    setNotice("正在识别…")
    callbacks.current.onError("")
    try {
      const result = await recorder.current.stopAndRecognize(callbacks.current.modelId, !!callbacks.current.onAudio)
      if (operation.current !== id) return
      setCanRetry(recorder.current.hasPendingAudio)
      if ("error" in result) {
        setNotice("识别未完成，录音保留在本页，可重试或重新录音")
        callbacks.current.onError(result.error)
      } else {
        setNotice("识别完成")
        callbacks.current.onText(result.text)
        if (result.audio) callbacks.current.onAudio?.(result.audio, result.text)
      }
    } catch (error) {
      if (operation.current !== id) return
      setCanRetry(recorder.current.hasPendingAudio)
      setNotice("识别未完成，请重试或重新录音")
      callbacks.current.onError(error instanceof Error ? error.message : "识别失败")
    } finally {
      if (operation.current === id) { locked.current = false; setBusy(false) }
    }
  }

  async function toggle() {
    if (locked.current) return
    if (recorder.current?.recording) { await recognize(); return }
    locked.current = true
    const id = ++operation.current
    setBusy(true)
    setNotice("正在开启麦克风…")
    callbacks.current.onError("")
    unlockAudio()
    stopTts()
    if (!recorder.current) recorder.current = new AsrRecorder()
    try {
      const started = await recorder.current.startRecording((seconds) => {
        if (!recorder.current?.active) {
          recorder.current?.pause()
          setRecording(false)
          setLevel(0)
          setCanRetry(!!recorder.current?.hasPendingAudio)
          setNotice("麦克风已中断，可识别已录内容或重新录音")
          return
        }
        setSeconds(seconds)
        if (callbacks.current.maxSeconds && seconds >= callbacks.current.maxSeconds) void recognize()
      }, setLevel)
      if (operation.current !== id) return
      if (!started) {
        setNotice("")
        callbacks.current.onError(recorder.current.lastError || "无法使用麦克风，请检查浏览器权限")
        return
      }
      if (document.visibilityState === "hidden") {
        recorder.current.pause()
        setCanRetry(recorder.current.hasPendingAudio)
        setNotice("录音已暂停，返回后请重新开始录音")
        return
      }
      setSeconds(0)
      setCanRetry(false)
      setRecording(true)
      setNotice("录音中，说完请点击停止录音")
    } catch (error) {
      if (operation.current !== id) return
      recorder.current.release()
      setNotice("")
      callbacks.current.onError(error instanceof Error ? error.message : "录音启动失败")
    } finally {
      if (operation.current === id) { locked.current = false; setBusy(false) }
    }
  }

  return { recording, busy, seconds, level, canRetry, notice, toggle, retry: recognize, reset }
}
