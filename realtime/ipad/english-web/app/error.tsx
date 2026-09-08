"use client"

import { useEffect } from "react"

const CHUNK_RELOAD_FLAG = "esc_chunk_reload_at"
const CHUNK_RELOAD_COOLDOWN_MS = 10_000

function isChunkLoadError(error: Error) {
  return (
    error.name === "ChunkLoadError" ||
    /Loading chunk [\d\w]+ failed/i.test(error.message) ||
    /Failed to fetch dynamically imported module/i.test(error.message) ||
    /Importing a module script failed/i.test(error.message)
  )
}

export default function GlobalErrorBoundary({
  error,
  reset,
}: {
  error: Error & { digest?: string }
  reset: () => void
}) {
  const chunkError = isChunkLoadError(error)

  useEffect(() => {
    console.error("[app-error-boundary]", error)
    if (!chunkError || typeof window === "undefined") return
    // A stale JS chunk reference (common right after a deploy, or when a
    // mobile connection drops mid-fetch) can't be fixed by re-rendering the
    // React tree — the browser needs a fresh HTML/document. Auto-reload once,
    // then fall back to the manual retry UI so we never loop forever.
    const lastReload = Number(window.sessionStorage.getItem(CHUNK_RELOAD_FLAG) || 0)
    if (Date.now() - lastReload > CHUNK_RELOAD_COOLDOWN_MS) {
      window.sessionStorage.setItem(CHUNK_RELOAD_FLAG, String(Date.now()))
      window.location.reload()
    }
  }, [error, chunkError])

  return (
    <main className="mx-auto flex min-h-dvh w-full max-w-md flex-col items-center justify-center gap-4 p-6 text-center">
      <p className="text-base font-semibold text-slate-800">
        {chunkError ? "页面资源加载失败，正在重新打开" : "页面出了点问题"}
      </p>
      <p className="text-sm text-slate-500">
        {chunkError
          ? "网络不稳定时偶尔会这样，通常刷新一次就好。"
          : error.message || "请重试，如果一直失败，可以刷新页面。"}
      </p>
      <div className="flex gap-3">
        <button
          type="button"
          className="rounded-full bg-blue-600 px-5 py-2 text-sm font-semibold text-white"
          onClick={() => reset()}
        >
          重试
        </button>
        <button
          type="button"
          className="rounded-full bg-slate-100 px-5 py-2 text-sm font-semibold text-slate-700"
          onClick={() => window.location.reload()}
        >
          刷新页面
        </button>
      </div>
    </main>
  )
}
