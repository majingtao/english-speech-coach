"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  Check,
  Loader2,
  RotateCcw,
  Volume2,
  X,
} from "lucide-react"
import {
  fetchTodayReview,
  fetchVocabDetail,
  submitVocabReview,
  type VocabAccent,
  type VocabDetail,
  type VocabExample,
  type VocabForms,
} from "@/lib/api/vocab"
import { playVocab, playWord, stopWord } from "@/lib/vocab/audio"
import { formatDueLabel } from "@/lib/vocab/srs"

interface SessionWord {
  id: number
  word: string
  detail?: VocabDetail
}

const MASTERY_TARGET = 6

/**
 * 把例句里"目标词及其变形"（base/past/ing/plural...）整段标红。
 * 大小写不敏感；优先匹配较长的变形（避免 "decide" 抢在 "decided" 前面）。
 */
function highlightWord(
  text: string,
  word?: string,
  forms?: VocabForms,
): React.ReactNode[] {
  if (!text) return [text]
  const variants = new Set<string>()
  if (word) variants.add(word)
  if (forms) {
    Object.values(forms).forEach((v) => {
      if (typeof v === "string" && v.trim()) variants.add(v.trim())
    })
  }
  if (variants.size === 0) return [text]
  const list = Array.from(variants)
    .map((s) => s.trim())
    .filter(Boolean)
    .sort((a, b) => b.length - a.length)
  const escape = (s: string) => s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")
  const pattern = new RegExp(`\\b(${list.map(escape).join("|")})\\b`, "gi")
  const out: React.ReactNode[] = []
  let last = 0
  let key = 0
  let m: RegExpExecArray | null
  while ((m = pattern.exec(text)) !== null) {
    if (m.index > last) out.push(text.slice(last, m.index))
    out.push(
      <span key={`hl-${key++}`} className="vocab-highlight">
        {m[0]}
      </span>,
    )
    last = m.index + m[0].length
    if (m.index === pattern.lastIndex) pattern.lastIndex++
  }
  if (last < text.length) out.push(text.slice(last))
  return out
}

function MasteryBadge({
  status,
  reps,
}: {
  status?: number | null
  reps?: number | null
}) {
  if (status === 2) {
    return <span className="vocab-badge vocab-badge-mastered">🎓 已掌握</span>
  }
  if (status === 1) {
    const n = Math.max(0, Math.min(MASTERY_TARGET, reps ?? 0))
    return (
      <span className="vocab-badge vocab-badge-learning">
        📚 学习中 {n}/{MASTERY_TARGET}
      </span>
    )
  }
  return <span className="vocab-badge vocab-badge-new">🌱 新词</span>
}

export function VocabFlashcardSession() {
  const router = useRouter()
  const [queue, setQueue] = useState<SessionWord[]>([])
  const [idx, setIdx] = useState(0)
  const [flipped, setFlipped] = useState(false)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState("")
  const [ukLoading, setUkLoading] = useState(false)
  const [usLoading, setUsLoading] = useState(false)
  const [exampleLoading, setExampleLoading] = useState<number | null>(null)
  // 翻面自动播用：记住上一次手动选过的口音，默认 UK
  const [lastAccent, setLastAccent] = useState<VocabAccent>("uk")
  const [stats, setStats] = useState({ remembered: 0, forgot: 0 })
  const [lastResult, setLastResult] = useState<string>("")

  const current = queue[idx]
  const done = !loading && queue.length > 0 && idx >= queue.length
  const empty = !loading && queue.length === 0

  const loadQueue = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      const list = await fetchTodayReview(30)
      setQueue(list.map((v) => ({ id: v.id, word: v.word, detail: v })))
      setIdx(0)
      setFlipped(false)
      setStats({ remembered: 0, forgot: 0 })
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "加载失败")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadQueue()
    return () => {
      stopWord()
    }
  }, [loadQueue])

  useEffect(() => {
    if (!current || current.detail?.contentJson) return
    let aborted = false
    ;(async () => {
      try {
        const full = await fetchVocabDetail(current.id)
        if (aborted) return
        setQueue((prev) => {
          const next = [...prev]
          next[idx] = { ...next[idx], detail: full }
          return next
        })
      } catch (e) {
        console.error("[flashcard] fetch detail failed", e)
      }
    })()
    return () => {
      aborted = true
    }
  }, [current, idx])

  /**
   * 例句选择策略：
   *   1) 优先用 content.entries[]（按词性/义项分层）：先把 pos 与 vocab.pos 匹配的 entry 排到前面，
   *      然后逐 definition 取最多 2 句，全程总数不超过 6。
   *   2) 没有 entries 时回退到拍平的 content.examples[].slice(0, 6)。
   */
  const examples = useMemo<VocabExample[]>(() => {
    const content = current?.detail?.contentJson
    if (!content) return []

    const PER_DEF = 2
    const CAP = 6

    const entries = content.entries
    if (Array.isArray(entries) && entries.length > 0) {
      const primaryPos = (current?.detail?.pos ?? "")
        .toLowerCase()
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean)
      const matches = (p?: string) =>
        p ? primaryPos.includes(p.toLowerCase()) : false
      const sorted = [...entries].sort((a, b) => {
        const am = matches(a.pos) ? 0 : 1
        const bm = matches(b.pos) ? 0 : 1
        return am - bm
      })

      const out: VocabExample[] = []
      for (const entry of sorted) {
        for (const def of entry.definitions ?? []) {
          const picks = (def.examples ?? []).slice(0, PER_DEF)
          for (const ex of picks) {
            if (out.length >= CAP) return out
            out.push(ex)
          }
        }
      }
      if (out.length > 0) return out
    }

    const flat = content.examples
    return Array.isArray(flat) ? flat.slice(0, CAP) : []
  }, [current])

  // 翻面自动响一次（用上一次手动选过的口音；默认 UK）
  useEffect(() => {
    if (!flipped || !current) return
    const accent = lastAccent
    const url =
      accent === "uk" ? current.detail?.audioUkUrl : current.detail?.audioUsUrl
    const setLoading = accent === "uk" ? setUkLoading : setUsLoading
    playVocab(
      { id: current.id, word: current.word, url, accent },
      { onLoadingChange: setLoading, onSpeakingChange: () => {} },
    )
    // 故意只依赖 flipped：detail 加载/lastAccent 变化时不应该重播
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [flipped])

  // 预拉下一张卡的音频文件到浏览器缓存，降低翻页等待
  useEffect(() => {
    if (typeof window === "undefined") return
    const next = queue[idx + 1]
    const url = next?.detail?.audioUkUrl ?? next?.detail?.audioUsUrl
    if (!url) return
    const a = new Audio()
    a.preload = "auto"
    a.src = url
    // 不需 .play()，浏览器会自动开始拉资源；变量超出作用域后 GC
  }, [idx, queue])

  async function handlePlay(accent: VocabAccent) {
    if (!current) return
    setLastAccent(accent)
    const url =
      accent === "uk" ? current.detail?.audioUkUrl : current.detail?.audioUsUrl
    const setLoading = accent === "uk" ? setUkLoading : setUsLoading
    await playVocab(
      { id: current.id, word: current.word, url, accent },
      { onLoadingChange: setLoading, onSpeakingChange: () => {} },
    )
  }

  async function handlePlayExample(i: number, text: string) {
    if (!text) return
    // 例句没有缓存音频，走流式 Edge-TTS；先停掉卡片正在响的发音避免重叠
    stopWord()
    setExampleLoading(i)
    try {
      await playWord(text, {}, lastAccent)
    } finally {
      setExampleLoading(null)
    }
  }

  async function handleAnswer(remembered: boolean) {
    if (!current || submitting) return
    setSubmitting(true)
    try {
      const res = await submitVocabReview(current.id, remembered)
      setStats((s) =>
        remembered ? { ...s, remembered: s.remembered + 1 } : { ...s, forgot: s.forgot + 1 },
      )
      setLastResult(
        remembered
          ? `记住啦，下次复习：${formatDueLabel(res.nextReviewAt)}`
          : "没关系，今天再来一次",
      )
      stopWord()
      setFlipped(false)
      setIdx((i) => i + 1)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "提交失败")
    } finally {
      setSubmitting(false)
    }
  }

  const progressText = queue.length > 0 ? `${Math.min(idx + 1, queue.length)} / ${queue.length}` : "0 / 0"
  const content = current?.detail?.contentJson

  return (
    <main className="vocab-review-shell">
      <header className="vocab-review-header">
        <button
          type="button"
          className="vocab-back"
          onClick={() => {
            stopWord()
            router.push("/vocab")
          }}
        >
          <ArrowLeft className="size-[18px]" />
          <span>返回</span>
        </button>
        <div className="vocab-review-progress">
          <span className="vocab-review-pill">{progressText}</span>
          <span className="vocab-review-stat text-emerald-600">
            <Check className="size-3.5" />
            {stats.remembered}
          </span>
          <span className="vocab-review-stat text-rose-500">
            <X className="size-3.5" />
            {stats.forgot}
          </span>
        </div>
        <button type="button" className="vocab-refresh" onClick={loadQueue} title="重新加载">
          <RotateCcw className="size-[16px]" />
        </button>
      </header>

      {loading ? (
        <div className="vocab-review-empty">
          <Loader2 className="size-6 animate-spin text-blue-400" />
          <span>加载中...</span>
        </div>
      ) : error ? (
        <div className="vocab-review-empty vocab-review-error">
          <span>{error}</span>
          <button type="button" className="vocab-review-retry" onClick={loadQueue}>
            重试
          </button>
        </div>
      ) : empty ? (
        <div className="vocab-review-empty">
          <span>今天没有待复习的词了 🎉</span>
          <button
            type="button"
            className="vocab-review-retry"
            onClick={() => router.push("/vocab")}
          >
            返回仪表盘
          </button>
        </div>
      ) : done ? (
        <div className="vocab-review-empty">
          <span>
            复习完成！记住 {stats.remembered}，忘记 {stats.forgot}
          </span>
          <div className="flex gap-3">
            <button type="button" className="vocab-review-retry" onClick={loadQueue}>
              再来一轮
            </button>
            <button
              type="button"
              className="vocab-review-retry"
              onClick={() => router.push("/vocab")}
            >
              返回仪表盘
            </button>
          </div>
        </div>
      ) : (
        <>
          <section
            className={`vocab-flashcard ${flipped ? "vocab-flashcard-flipped" : ""}`}
            onClick={() => setFlipped((v) => !v)}
          >
            <div className="vocab-flashcard-inner">
              <div className="vocab-flashcard-face vocab-flashcard-front">
                <MasteryBadge
                  status={current?.detail?.progressStatus}
                  reps={current?.detail?.repetitions}
                />
                <span className="vocab-flashcard-hint">点击卡片翻面</span>
                <h2 className="vocab-flashcard-word">{current?.word}</h2>
                {content?.ipa && (
                  <p className="vocab-flashcard-ipa">{content.ipa}</p>
                )}
                <div
                  className="vocab-flashcard-speakers"
                  onClick={(e) => e.stopPropagation()}
                >
                  <button
                    type="button"
                    className="vocab-flashcard-speaker vocab-flashcard-speaker-uk"
                    disabled={ukLoading}
                    onClick={() => handlePlay("uk")}
                  >
                    {ukLoading ? (
                      <Loader2 className="size-4 animate-spin" />
                    ) : (
                      <Volume2 className="size-4" />
                    )}
                    <span>UK</span>
                  </button>
                  <button
                    type="button"
                    className="vocab-flashcard-speaker vocab-flashcard-speaker-us"
                    disabled={usLoading}
                    onClick={() => handlePlay("us")}
                  >
                    {usLoading ? (
                      <Loader2 className="size-4 animate-spin" />
                    ) : (
                      <Volume2 className="size-4" />
                    )}
                    <span>US</span>
                  </button>
                </div>
                {current?.detail?.nextReviewAt && (
                  <span className="vocab-flashcard-due">
                    {formatDueLabel(current.detail.nextReviewAt)}
                  </span>
                )}
              </div>
              <div className="vocab-flashcard-face vocab-flashcard-back">
                {content ? (
                  <>
                    {content.definition_cn && (
                      <p className="vocab-flashcard-def-cn">{content.definition_cn}</p>
                    )}
                    {content.definition_en && (
                      <p className="vocab-flashcard-def-en">{content.definition_en}</p>
                    )}
                    {examples.length > 0 && (
                      <ul className="vocab-flashcard-examples">
                        {examples.map((ex, i) => (
                          <li key={i} className="vocab-flashcard-example-row">
                            <div className="vocab-flashcard-example-text">
                              <p className="vocab-flashcard-example-en">
                                {highlightWord(
                                  ex.en,
                                  current?.word,
                                  current?.detail?.formsJson,
                                )}
                              </p>
                              {ex.cn && (
                                <p className="vocab-flashcard-example-cn">{ex.cn}</p>
                              )}
                            </div>
                            <button
                              type="button"
                              className="vocab-flashcard-example-speaker"
                              disabled={exampleLoading === i}
                              onClick={(e) => {
                                e.stopPropagation()
                                handlePlayExample(i, ex.en)
                              }}
                              aria-label="朗读例句"
                            >
                              {exampleLoading === i ? (
                                <Loader2 className="size-4 animate-spin" />
                              ) : (
                                <Volume2 className="size-4" />
                              )}
                            </button>
                          </li>
                        ))}
                      </ul>
                    )}
                  </>
                ) : (
                  <div className="vocab-flashcard-loading">
                    <Loader2 className="size-5 animate-spin text-blue-400" />
                    <span>正在加载释义...</span>
                  </div>
                )}
              </div>
            </div>
          </section>

          {lastResult && (
            <p className="vocab-review-toast">{lastResult}</p>
          )}

          <section className="vocab-review-actions">
            <button
              type="button"
              className="vocab-review-btn vocab-review-btn-forgot"
              disabled={submitting}
              onClick={() => handleAnswer(false)}
            >
              <X className="size-5" />
              忘了
            </button>
            <button
              type="button"
              className="vocab-review-btn vocab-review-btn-got"
              disabled={submitting}
              onClick={() => handleAnswer(true)}
            >
              <Check className="size-5" />
              记得
            </button>
          </section>
        </>
      )}
    </main>
  )
}
