"use client"

import { useEffect, useRef, useState } from "react"
import Lottie from "lottie-react"
import type { LottieRefCurrentProps } from "lottie-react"

export type AvatarState = "idle" | "talking" | "listening"

interface ExamAvatarProps {
  state: AvatarState
  label?: string
}

/**
 * Animated exam avatar that switches between idle / talking / listening states.
 * Uses Lottie JSON files from /lottie/ directory.
 * Falls back to CSS-animated placeholder if Lottie files fail to load.
 */
export function ExamAvatar({ state, label }: ExamAvatarProps) {
  const lottieRef = useRef<LottieRefCurrentProps>(null)
  const [animData, setAnimData] = useState<Record<string, unknown> | null>(null)
  const [loadFailed, setLoadFailed] = useState(false)

  useEffect(() => {
    let cancelled = false
    const file = `/lottie/teacher-${state}.json`
    fetch(file)
      .then((r) => {
        if (!r.ok) throw new Error(`${r.status}`)
        return r.json()
      })
      .then((data) => {
        if (!cancelled) {
          setAnimData(data)
          setLoadFailed(false)
        }
      })
      .catch(() => {
        if (!cancelled) setLoadFailed(true)
      })
    return () => { cancelled = true }
  }, [state])

  const stateLabel = state === "talking" ? "考官正在说话…" : state === "listening" ? "请回答…" : "准备中"

  return (
    <div className="exam-avatar-area">
      <div className={`exam-avatar-circle exam-avatar-${state}`}>
        {!loadFailed && animData ? (
          <Lottie
            lottieRef={lottieRef}
            animationData={animData}
            loop
            autoplay
            className="exam-avatar-lottie"
          />
        ) : (
          <CssFallbackAvatar state={state} />
        )}
      </div>
      <p className="exam-avatar-label">{label || stateLabel}</p>
    </div>
  )
}

/** CSS-only animated fallback when Lottie files aren't available */
function CssFallbackAvatar({ state }: { state: AvatarState }) {
  return (
    <div className={`exam-css-avatar exam-css-avatar-${state}`}>
      {/* Face */}
      <div className="exam-css-face">
        {/* Eyes */}
        <div className="exam-css-eyes">
          <div className="exam-css-eye exam-css-eye-l" />
          <div className="exam-css-eye exam-css-eye-r" />
        </div>
        {/* Mouth */}
        <div className={`exam-css-mouth exam-css-mouth-${state}`} />
      </div>
    </div>
  )
}
