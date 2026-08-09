"use client"

import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, Loader2, RefreshCw } from "lucide-react"
import {
  enrollNewVocabs,
  fetchVocabThemes,
  type VocabTheme,
} from "@/lib/api/vocab"

const ENROLL_BATCH = 25
const LEVEL = "ket"

export function VocabThemeGrid() {
  const router = useRouter()
  const [themes, setThemes] = useState<VocabTheme[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [enrollingCode, setEnrollingCode] = useState<string | null>(null)
  const [hint, setHint] = useState("")

  const load = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      const list = await fetchVocabThemes(LEVEL)
      setThemes(list || [])
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "加载失败")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  async function handlePickTheme(theme: VocabTheme) {
    if (enrollingCode) return
    setEnrollingCode(theme.code)
    setHint("")
    try {
      const { count, enrolledToday, dailyCap } = await enrollNewVocabs(
        LEVEL,
        ENROLL_BATCH,
        theme.code,
      )
      if (count === 0) {
        if (enrolledToday >= dailyCap) {
          setHint(`今天已经学满 ${dailyCap} 个新词啦，明天再来 🌙`)
        } else {
          setHint(`「${theme.nameCn}」里你能学的词都学过啦 🎉`)
        }
        return
      }
      router.push("/vocab/review")
    } catch (e: unknown) {
      setHint(e instanceof Error ? e.message : "学新词失败")
    } finally {
      setEnrollingCode(null)
    }
  }

  return (
    <main className="vocab-shell">
      <header className="vocab-header">
        <button
          type="button"
          className="vocab-back"
          onClick={() => router.push("/vocab")}
        >
          <ArrowLeft className="size-[18px]" />
          <span>词汇</span>
        </button>
        <h1 className="vocab-header-title">按主题练习</h1>
        <button
          type="button"
          className="vocab-refresh"
          disabled={loading}
          onClick={load}
          title="刷新"
        >
          <RefreshCw className={`size-[16px] ${loading ? "animate-spin" : ""}`} />
        </button>
      </header>

      <p className="vocab-theme-intro">
        选一个你感兴趣的主题，会随机挑 {ENROLL_BATCH} 个未学的词加入今日复习队列。
      </p>

      {hint && <p className="vocab-theme-hint">{hint}</p>}

      {loading ? (
        <div className="vocab-review-empty">
          <Loader2 className="size-6 animate-spin text-blue-400" />
          <span>加载主题中…</span>
        </div>
      ) : error ? (
        <div className="vocab-review-empty vocab-review-error">
          <span>{error}</span>
          <button type="button" className="vocab-review-retry" onClick={load}>
            重试
          </button>
        </div>
      ) : themes.length === 0 ? (
        <div className="vocab-review-empty">
          <span>没有可用主题，请联系管理员添加</span>
        </div>
      ) : (
        <section className="vocab-theme-grid">
          {themes.map((t, i) => (
            <button
              key={t.code}
              type="button"
              className={`vocab-theme-tile vocab-theme-tile-${(i % 6) + 1}`}
              disabled={enrollingCode != null}
              onClick={() => handlePickTheme(t)}
            >
              {enrollingCode === t.code && (
                <span className="vocab-theme-overlay">
                  <Loader2 className="size-5 animate-spin" />
                </span>
              )}
              <span className="vocab-theme-cn">{t.nameCn}</span>
              <span className="vocab-theme-en">{t.nameEn}</span>
            </button>
          ))}
        </section>
      )}
    </main>
  )
}
