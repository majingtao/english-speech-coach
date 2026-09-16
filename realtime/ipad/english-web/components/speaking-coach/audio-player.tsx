"use client"

import { useEffect, useRef, useState } from "react"
import WaveSurfer from "wavesurfer.js"
import { coachAudio } from "@/lib/api/speaking-coach"

export function CoachAudioPlayer({ attemptId, turnIndex, blob, label }: { attemptId?: string; turnIndex?: number; blob?: Blob; label: string }) {
  const container = useRef<HTMLDivElement>(null)
  const player = useRef<WaveSurfer | null>(null)
  const [ready, setReady] = useState(false)
  const [playing, setPlaying] = useState(false)
  const [error, setError] = useState("")
  useEffect(() => {
    let alive = true; let url = ""
    const wave = WaveSurfer.create({ container: container.current!, height: 48, waveColor: "#a5b4fc", progressColor: "#4f46e5", cursorColor: "#4338ca", barWidth: 2 })
    player.current = wave
    wave.on("ready", () => { if (alive) setReady(true) })
    wave.on("play", () => setPlaying(true)); wave.on("pause", () => setPlaying(false))
    wave.on("error", () => { if (alive) setError("录音暂时无法播放") })
    void (async () => {
      try {
        const audio = blob || await coachAudio(attemptId!, turnIndex!)
        if (!alive) return
        url = URL.createObjectURL(audio); await wave.load(url)
      } catch { if (alive) setError("录音暂时无法加载") }
    })()
    return () => { alive = false; wave.destroy(); player.current = null; if (url) URL.revokeObjectURL(url) }
  }, [attemptId, turnIndex, blob])
  return <div className="coach-audio"><span>{label}</span><div ref={container} /><button type="button" disabled={!ready} onClick={() => { void player.current?.playPause().catch(() => setError("请再次点击播放")) }}>{playing ? "暂停" : "播放录音"}</button>{error && <small role="alert">{error}</small>}</div>
}
