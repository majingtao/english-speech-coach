"use client"

import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, BookOpenText, ChevronRight, Loader2, RefreshCw, Users } from "lucide-react"
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
          <h1>KET 表达</h1>
          <p>单题表达与双人互动</p>
        </div>
        <button type="button" className="expression-icon-btn" onClick={load} disabled={loading} title="刷新">
          <RefreshCw className={`size-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </header>

      <section className="expression-intro">
        <BookOpenText className="size-6" />
        <div>
          <h2>选择训练方式</h2>
          <p>单题表达练习 Part 1 回答；双人互动练习 Part 2 提问、回应和说明理由。</p>
        </div>
      </section>

      <nav className="expression-mode-entry" aria-label="KET 表达训练方式">
        <div className="active">
          <BookOpenText className="size-5" />
          <span><strong>单题表达</strong><small>主题练习 · Part 1</small></span>
        </div>
        <button type="button" onClick={() => router.push("/expression/dialogue")}>
          <Users className="size-5" />
          <span><strong>双人互动</strong><small>选择 A/B 角色 · Part 2</small></span>
          <ChevronRight className="size-4" />
        </button>
      </nav>

      <h2 className="expression-list-heading">单题表达主题</h2>

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
