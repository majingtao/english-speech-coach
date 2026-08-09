"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  BookOpen,
  ClipboardList,
  Ear,
  Layers,
  Loader2,
  PenLine,
  Plus,
  RefreshCw,
  Repeat,
} from "lucide-react"
import {
  enrollFromWordbook,
  fetchTodayReview,
  type VocabDetail,
} from "@/lib/api/vocab"

interface QuickEntry {
  key: string
  label: string
  subtitle: string
  icon: React.ReactNode
  href?: string
  enabled: boolean
}

const DAILY_NEW_TARGET = 50

export function VocabDashboard() {
  const router = useRouter()
  const [queue, setQueue] = useState<VocabDetail[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")

  const load = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      // 每天自动从生词本待学池补新词（后端按每日 50 上限限流，可重复安全调用）
      try {
        await enrollFromWordbook(DAILY_NEW_TARGET)
      } catch {
        // best-effort：补词失败不影响复习队列展示
      }
      const list = await fetchTodayReview(50)
      setQueue(list || [])
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "加载失败"
      setError(msg)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const newCount = useMemo(
    () => queue.filter((v) => !v.progressStatus).length,
    [queue],
  )
  const reviewCount = queue.length - newCount

  const entries: QuickEntry[] = [
    {
      key: "review",
      label: "SRS 抽认卡",
      subtitle:
        queue.length > 0
          ? `${queue.length} 个词待复习`
          : "暂无待复习词，去生词本添加单词",
      icon: <Repeat className="size-5" />,
      href: "/vocab/review",
      enabled: queue.length > 0,
    },
    {
      key: "themes",
      label: "按主题练习",
      subtitle: "运动 / 食物 / 学校 ...",
      icon: <Layers className="size-5" />,
      href: "/vocab/themes",
      enabled: true,
    },
    {
      key: "test",
      label: "主题测验",
      subtitle: "选义 / 拼写 / 填空",
      icon: <ClipboardList className="size-5" />,
      enabled: false,
    },
    {
      key: "dictation",
      label: "听音拼写",
      subtitle: "从 SRS 队列抽词",
      icon: <Ear className="size-5" />,
      enabled: false,
    },
    {
      key: "sentence",
      label: "造句练习",
      subtitle: "AI 批改反馈",
      icon: <PenLine className="size-5" />,
      enabled: false,
    },
    {
      key: "wordbook",
      label: "我的生词本",
      subtitle: "添加孩子不会的单词",
      icon: <BookOpen className="size-5" />,
      href: "/vocab/wordbook",
      enabled: true,
    },
  ]

  return (
    <main className="vocab-shell">
      <header className="vocab-header">
        <button type="button" className="vocab-back" onClick={() => router.push("/")}>
          <ArrowLeft className="size-[18px]" />
          <span>首页</span>
        </button>
        <h1 className="vocab-header-title">词汇练习</h1>
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

      <section className="vocab-today-card">
        <div className="vocab-today-head">
          <div>
            <p className="vocab-today-label">今日复习</p>
            <h2 className="vocab-today-count">
              {loading ? (
                <Loader2 className="size-5 animate-spin text-white" />
              ) : (
                queue.length
              )}
              <span className="vocab-today-unit">词</span>
            </h2>
            {!loading && queue.length > 0 && (
              <p className="vocab-today-breakdown">
                复习 {reviewCount} · 新词 {newCount}
              </p>
            )}
          </div>
          <div className="vocab-today-actions">
            <button
              type="button"
              className="vocab-today-start"
              disabled={queue.length === 0 || loading}
              onClick={() => router.push("/vocab/review")}
            >
              开始复习
            </button>
            <button
              type="button"
              className="vocab-today-enroll"
              disabled={loading}
              onClick={() => router.push("/vocab/wordbook")}
            >
              <Plus className="size-4" />
              <span>添加生词</span>
            </button>
          </div>
        </div>
        {error && <p className="vocab-today-error">{error}</p>}
      </section>

      <section className="vocab-entries">
        <h3 className="vocab-section-title">学习模式</h3>
        <div className="vocab-entry-grid">
          {entries.map((entry) => {
            const Tag = entry.enabled ? "button" : "div"
            return (
              <Tag
                key={entry.key}
                className={`vocab-entry-card ${entry.enabled ? "vocab-entry-enabled" : "vocab-entry-disabled"}`}
                type={entry.enabled ? "button" : undefined}
                onClick={
                  entry.enabled && entry.href
                    ? () => router.push(entry.href!)
                    : undefined
                }
              >
                <div className={`vocab-entry-icon ${entry.enabled ? "vocab-entry-icon-active" : ""}`}>
                  {entry.icon}
                </div>
                <div className="vocab-entry-text">
                  <span className="vocab-entry-label">{entry.label}</span>
                  <span className="vocab-entry-sub">{entry.subtitle}</span>
                </div>
                {!entry.enabled && <span className="vocab-entry-badge">即将开放</span>}
              </Tag>
            )
          })}
        </div>
      </section>
    </main>
  )
}
