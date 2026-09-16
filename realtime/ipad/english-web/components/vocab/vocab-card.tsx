"use client"

import { useEffect, useMemo, useState } from "react"
import type { ReactNode } from "react"
import { Loader2, Volume2 } from "lucide-react"
import type {
  VocabAccent,
  VocabDetail,
  VocabExample,
  VocabForms,
} from "@/lib/api/vocab"
import { playVocab, playWord, stopWord } from "@/lib/vocab/audio"
import { formatDueLabel } from "@/lib/vocab/srs"

const MASTERY_TARGET = 6
const EXAMPLES_PER_DEF = 2
const EXAMPLES_CAP = 6

/**
 * 把例句里"目标词及其变形"（base/past/ing/plural...）整段标红。
 * 大小写不敏感；优先匹配较长的变形（避免 "decide" 抢在 "decided" 前面）。
 */
export function highlightWord(text: string, word?: string, forms?: VocabForms): ReactNode[] {
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
  const out: ReactNode[] = []
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

export function MasteryBadge({ status, reps }: { status?: number | null; reps?: number | null }) {
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

/**
 * 例句选择策略（与 /vocab/review 一致）：
 *   1) 优先 content.entries[]：与词条 pos 匹配的 entry 排前，每个义项最多 2 句，总数不超过 6；
 *   2) 没有 entries 时回退到 content.examples[].slice(0, 6)。
 */
export function pickExamples(detail?: VocabDetail): VocabExample[] {
  const content = detail?.contentJson
  if (!content) return []
  const entries = content.entries
  if (Array.isArray(entries) && entries.length > 0) {
    const primaryPos = (detail?.pos ?? "")
      .toLowerCase()
      .split(",")
      .map((s) => s.trim())
      .filter(Boolean)
    const matches = (p?: string) => (p ? primaryPos.includes(p.toLowerCase()) : false)
    const sorted = [...entries].sort((a, b) => (matches(a.pos) ? 0 : 1) - (matches(b.pos) ? 0 : 1))
    const out: VocabExample[] = []
    for (const entry of sorted) {
      for (const def of entry.definitions ?? []) {
        for (const ex of (def.examples ?? []).slice(0, EXAMPLES_PER_DEF)) {
          if (out.length >= EXAMPLES_CAP) return out
          out.push(ex)
        }
      }
    }
    if (out.length > 0) return out
  }
  return Array.isArray(content.examples) ? content.examples.slice(0, EXAMPLES_CAP) : []
}

type VocabCardProps = {
  id: number
  word: string
  detail?: VocabDetail
  flipped: boolean
  onToggle: () => void
  /** 翻面时自动播放一次发音（与 /vocab/review 一致），默认开启 */
  autoPlayOnFlip?: boolean
}

/**
 * 翻面抽认卡：正面 单词 / 音标 / UK·US 发音；背面 释义 + 例句（例句可朗读）。
 * 外观与 /vocab/review 相同（复用 vocab-flashcard-* 样式）。
 */
export function VocabCard({ id, word, detail, flipped, onToggle, autoPlayOnFlip = true }: VocabCardProps) {
  const [ukLoading, setUkLoading] = useState(false)
  const [usLoading, setUsLoading] = useState(false)
  const [exampleLoading, setExampleLoading] = useState<number | null>(null)
  const [lastAccent, setLastAccent] = useState<VocabAccent>("uk")

  const content = detail?.contentJson
  const examples = useMemo(() => pickExamples(detail), [detail])

  function play(accent: VocabAccent) {
    const url = accent === "uk" ? detail?.audioUkUrl : detail?.audioUsUrl
    const setLoading = accent === "uk" ? setUkLoading : setUsLoading
    return playVocab(
      { id, word, url, accent },
      { onLoadingChange: setLoading, onSpeakingChange: () => {} },
    ).catch((err) => {
      // 发音失败不打断浏览
      setLoading(false)
      console.warn("[vocab-card] play failed", err)
    })
  }

  // 翻到背面时自动响一次（用上一次手动选过的口音）
  useEffect(() => {
    if (!flipped || !autoPlayOnFlip) return
    void play(lastAccent)
    // 只在翻面时触发，detail 加载/口音变化不重播
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [flipped])

  async function handlePlay(accent: VocabAccent) {
    setLastAccent(accent)
    await play(accent)
  }

  async function handlePlayExample(i: number, text: string) {
    if (!text) return
    stopWord()
    setExampleLoading(i)
    try {
      await playWord(text, {}, lastAccent)
    } catch {
      // 朗读失败不打断浏览
    } finally {
      setExampleLoading(null)
    }
  }

  return (
    <section
      className={`vocab-flashcard ${flipped ? "vocab-flashcard-flipped" : ""}`}
      onClick={onToggle}
    >
      <div className="vocab-flashcard-inner">
        <div className="vocab-flashcard-face vocab-flashcard-front">
          <MasteryBadge status={detail?.progressStatus} reps={detail?.repetitions} />
          <span className="vocab-flashcard-hint">点击卡片翻面</span>
          <h2 className="vocab-flashcard-word">{word}</h2>
          {content?.ipa && <p className="vocab-flashcard-ipa">{content.ipa}</p>}
          <div className="vocab-flashcard-speakers" onClick={(e) => e.stopPropagation()}>
            <button
              type="button"
              className="vocab-flashcard-speaker vocab-flashcard-speaker-uk"
              disabled={ukLoading}
              onClick={() => handlePlay("uk")}
            >
              {ukLoading ? <Loader2 className="size-4 animate-spin" /> : <Volume2 className="size-4" />}
              <span>UK</span>
            </button>
            <button
              type="button"
              className="vocab-flashcard-speaker vocab-flashcard-speaker-us"
              disabled={usLoading}
              onClick={() => handlePlay("us")}
            >
              {usLoading ? <Loader2 className="size-4 animate-spin" /> : <Volume2 className="size-4" />}
              <span>US</span>
            </button>
          </div>
          {detail?.nextReviewAt && (
            <span className="vocab-flashcard-due">{formatDueLabel(detail.nextReviewAt)}</span>
          )}
        </div>
        <div className="vocab-flashcard-face vocab-flashcard-back">
          {content ? (
            <>
              {content.definition_cn && <p className="vocab-flashcard-def-cn">{content.definition_cn}</p>}
              {content.definition_en && <p className="vocab-flashcard-def-en">{content.definition_en}</p>}
              {examples.length > 0 && (
                <ul className="vocab-flashcard-examples">
                  {examples.map((ex, i) => (
                    <li key={i} className="vocab-flashcard-example-row">
                      <div className="vocab-flashcard-example-text">
                        <p className="vocab-flashcard-example-en">
                          {highlightWord(ex.en, word, detail?.formsJson)}
                        </p>
                        {ex.cn && <p className="vocab-flashcard-example-cn">{ex.cn}</p>}
                      </div>
                      <button
                        type="button"
                        className="vocab-flashcard-example-speaker"
                        disabled={exampleLoading === i}
                        onClick={(e) => {
                          e.stopPropagation()
                          void handlePlayExample(i, ex.en)
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
  )
}
