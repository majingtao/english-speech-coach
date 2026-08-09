"use client"

import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, BookOpenText, ChevronRight, Loader2, RefreshCw } from "lucide-react"
import { fetchExpressionThemes, type ExpressionTheme } from "@/lib/api/expression"

export function ExpressionThemes() {
  const router = useRouter()
  const [themes, setThemes] = useState<ExpressionTheme[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  const load = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      setThemes(await fetchExpressionThemes("ket"))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "主题加载失败")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(load, 0)
    return () => window.clearTimeout(timer)
  }, [load])

  return (
    <main className="expression-shell">
      <header className="expression-header">
        <button type="button" className="expression-icon-btn" onClick={() => router.push("/")} title="返回首页">
          <ArrowLeft className="size-5" />
        </button>
        <div>
          <h1>表达练习</h1>
          <p>KET speaking and writing</p>
        </div>
        <button type="button" className="expression-icon-btn" onClick={load} disabled={loading} title="刷新">
          <RefreshCw className={`size-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </header>

      <section className="expression-intro">
        <BookOpenText className="size-6" />
        <div>
          <h2>从句型到自由表达</h2>
          <p>先听分级范文，再替换关键词，最后完成自己的口语或写作回答。</p>
        </div>
      </section>

      {loading ? (
        <div className="expression-state"><Loader2 className="size-6 animate-spin" />正在加载</div>
      ) : error ? (
        <div className="expression-state expression-error"><span>{error}</span><button type="button" onClick={load}>重试</button></div>
      ) : themes.length === 0 ? (
        <div className="expression-state">当前没有已发布的表达主题</div>
      ) : (
        <section className="expression-theme-list">
          {themes.map((theme) => (
            <button
              key={theme.code}
              type="button"
              className="expression-theme-row"
              onClick={() => router.push(`/expression/practice?theme=${encodeURIComponent(theme.code)}`)}
            >
              <span className="expression-theme-mark">{theme.nameEn.slice(0, 1).toUpperCase()}</span>
              <span className="expression-theme-copy">
                <strong>{theme.nameCn}</strong>
                <span>{theme.nameEn}</span>
                {theme.description && <small>{theme.description}</small>}
              </span>
              <ChevronRight className="size-5" />
            </button>
          ))}
        </section>
      )}
    </main>
  )
}
