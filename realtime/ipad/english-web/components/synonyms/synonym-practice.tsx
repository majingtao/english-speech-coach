"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  CheckCircle2,
  Loader2,
  RefreshCw,
  Shuffle,
  Volume2,
  XCircle,
} from "lucide-react"
import {
  fetchSynonymQueue,
  submitSynonymAnswer,
  type SynonymPoint,
  type SynonymPracticeMode,
} from "@/lib/api/synonym"
import { playWord, stopWord } from "@/lib/vocab/audio"

type PracticeTab = "all" | SynonymPracticeMode | "wrong"
type AnswerState = "idle" | "correct" | "wrong"

function SynAudioButtons({
  text,
  audioBusy,
  onPlay,
}: {
  text: string
  audioBusy: string
  onPlay: (text: string, accent: "uk" | "us") => void
}) {
  return (
    <span className="syn-audio-actions">
      {(["uk", "us"] as const).map((accent) => {
        const key = `${accent}:${text}`
        return (
          <button
            key={accent}
            type="button"
            className="syn-audio-btn"
            disabled={!!audioBusy}
            onClick={(event) => {
              event.stopPropagation()
              onPlay(text, accent)
            }}
            title={accent === "uk" ? "播放英式发音" : "播放美式发音"}
          >
            {audioBusy === key ? <Loader2 className="size-3 animate-spin" /> : <Volume2 className="size-3" />}
            {accent === "uk" ? "英" : "美"}
          </button>
        )
      })}
    </span>
  )
}

function hashText(value: string) {
  let hash = 0
  for (let i = 0; i < value.length; i += 1) {
    hash = (hash * 31 + value.charCodeAt(i)) >>> 0
  }
  return hash
}

function orderedOptions(point: SynonymPoint, pool: SynonymPoint[]) {
  const sameMode = pool.filter(
    (item) => item.mode === point.mode && item.id !== point.id && item.rightText !== point.rightText,
  )
  const fallback = pool.filter((item) => item.id !== point.id && item.rightText !== point.rightText)
  const candidates = [...sameMode, ...fallback]
    .map((item) => ({
      label: item.rightText,
      cn: item.rightCn,
      sort: hashText(`${point.code}:${point.seenCount}:${item.code}`),
    }))
    .sort((a, b) => a.sort - b.sort)

  const options = [{ label: point.rightText, cn: point.rightCn, correct: true }]
  for (const item of candidates) {
    if (options.some((option) => option.label === item.label)) continue
    options.push({ label: item.label, cn: item.cn, correct: false })
    if (options.length >= 4) break
  }

  return options
    .map((option) => ({ ...option, sort: hashText(`${point.code}:option:${option.label}`) }))
    .sort((a, b) => a.sort - b.sort)
}

export function SynonymPractice() {
  const router = useRouter()
  const [tab, setTab] = useState<PracticeTab>("all")
  const [points, setPoints] = useState<SynonymPoint[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [selected, setSelected] = useState("")
  const [answerState, setAnswerState] = useState<AnswerState>("idle")
  const [showQuestionCn, setShowQuestionCn] = useState(false)
  const [shownOptionCn, setShownOptionCn] = useState<Record<string, boolean>>({})
  const [audioBusy, setAudioBusy] = useState("")

  const load = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      const list = await fetchSynonymQueue()
      setPoints(list || [])
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "题库加载失败")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(load, 0)
    return () => window.clearTimeout(timer)
  }, [load])

  const visiblePoints = useMemo(
    () => {
      if (tab === "wrong") return points.filter((point) => point.status === "learning")
      if (tab === "synonym" || tab === "antonym") return points.filter((point) => point.mode === tab)
      return points
    },
    [points, tab],
  )

  const stats = useMemo(() => {
    const all = points
    const done = visiblePoints.filter((point) => point.status === "done").length
    const learning = visiblePoints.filter((point) => point.status === "learning").length
    return {
      total: visiblePoints.length,
      done,
      learning,
      newCount: visiblePoints.length - done - learning,
      allDone: all.filter((point) => point.status === "done").length,
      allTotal: all.length,
      synonymDone: all.filter((point) => point.mode === "synonym" && point.status === "done").length,
      synonymTotal: all.filter((point) => point.mode === "synonym").length,
      antonymDone: all.filter((point) => point.mode === "antonym" && point.status === "done").length,
      antonymTotal: all.filter((point) => point.mode === "antonym").length,
    }
  }, [points, visiblePoints])

  const current = visiblePoints.find((point) => point.status !== "done") || visiblePoints[0]
  const options = useMemo(
    () => (current ? orderedOptions(current, points) : []),
    [current, points],
  )
  const isComplete = stats.total > 0 && stats.done === stats.total && stats.learning === 0

  async function answer(label: string) {
    if (!current || answerState !== "idle") return
    setSelected(label)
    try {
      const result = await submitSynonymAnswer(current.id, label)
      setAnswerState(result.correct ? "correct" : "wrong")
      setPoints((items) =>
        items.map((item) =>
          item.id === current.id
            ? {
                ...item,
                seenCount: result.seenCount,
                correctCount: result.correctCount,
                wrongCount: result.wrongCount,
                mastered: result.mastered,
                status: result.mastered ? "done" : result.wrongCount > 0 ? "learning" : "new",
              }
            : item,
        ),
      )
    } catch (e: unknown) {
      setSelected("")
      setError(e instanceof Error ? e.message : "提交失败")
    }
  }

  function nextQuestion() {
    setSelected("")
    setAnswerState("idle")
    setShowQuestionCn(false)
    setShownOptionCn({})
  }

  function switchTab(next: PracticeTab) {
    setTab(next)
    nextQuestion()
  }

  // 与考试页同一条 TTS 通道：浏览器 -> Next /py/tts -> Python Edge-TTS（流式，不落文件）。
  // 之前走 Java /english/synonym/audio -> Python /py/vocab/tts，Java 端访问自签名 HTTPS 会失败，
  // 且非词库短语每次点击都会生成新文件，播放失败时也没有任何提示。
  useEffect(() => () => stopWord(), [])

  async function playText(text: string, accent: "uk" | "us") {
    if (!text || audioBusy) return
    const key = `${accent}:${text}`
    setAudioBusy(key)
    setError("")
    try {
      await playWord(text, {}, accent)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "语音播放失败")
    } finally {
      setAudioBusy("")
    }
  }

  return (
    <main className="syn-shell">
      <header className="syn-header">
        <button type="button" className="syn-back" onClick={() => router.push("/")}>
          <ArrowLeft className="size-[18px]" />
          <span>首页</span>
        </button>
        <h1>同义词练习</h1>
        <button type="button" className="syn-reset" onClick={() => load()} disabled={loading} title="刷新">
          <RefreshCw className={`size-[16px] ${loading ? "animate-spin" : ""}`} />
        </button>
      </header>

      <section className="syn-summary">
        <div><span>总进度</span><strong>{stats.allDone} / {stats.allTotal}</strong></div>
        <div><span>同义词</span><strong>{stats.synonymDone} / {stats.synonymTotal}</strong></div>
        <div><span>反义词</span><strong>{stats.antonymDone} / {stats.antonymTotal}</strong></div>
        <div><span>错题</span><strong>{points.filter((point) => point.status === "learning").length}</strong></div>
      </section>

      <nav className="syn-tabs" aria-label="练习模式">
        {[
          ["all", "全部"],
          ["synonym", "同义词"],
          ["antonym", "反义词"],
          ["wrong", "错题"],
        ].map(([key, label]) => (
          <button key={key} type="button" className={tab === key ? "active" : ""} onClick={() => switchTab(key as PracticeTab)}>
            {label}
          </button>
        ))}
      </nav>

      {error && <p className="syn-error">{error}</p>}

      {loading ? (
        <section className="syn-empty">
          <Loader2 className="size-8 animate-spin" />
          <strong>加载题库中...</strong>
        </section>
      ) : !current ? (
        <section className="syn-empty">
          <CheckCircle2 className="size-10" />
          <strong>{tab === "wrong" ? "当前没有错题" : "暂无题目"}</strong>
        </section>
      ) : isComplete && tab !== "wrong" ? (
        <section className="syn-complete">
          <CheckCircle2 className="size-12" />
          <h2>本轮全部知识点已完成</h2>
          <p>已覆盖当前模式下的所有后台知识点。</p>
        </section>
      ) : (
        <section className="syn-card">
          <div className="syn-card-meta">
            <span className={`syn-mode syn-mode-${current.mode}`}>{current.mode === "synonym" ? "同义词" : "反义词"}</span>
            <span>{current.source} · {current.sectionName}</span>
            <span>{current.status === "learning" ? "错题回炉" : current.status === "done" ? "已完成" : "新题"}</span>
          </div>

          <p className="syn-instruction">{current.mode === "synonym" ? "选择意思最接近的一项" : "选择意思相反的一项"}</p>
          <div className="syn-question-line">
            <h2>{current.leftText}</h2>
            <SynAudioButtons text={current.leftText} audioBusy={audioBusy} onPlay={playText} />
          </div>
          {current.leftCn && (
            showQuestionCn ? (
              <p className="syn-cn">{current.leftCn}</p>
            ) : (
              <button type="button" className="syn-reveal-cn" onClick={() => setShowQuestionCn(true)}>
                显示中文意思
              </button>
            )
          )}

          <div className="syn-options">
            {options.map((option) => {
              const chosen = selected === option.label
              const showCorrect = answerState !== "idle" && option.label === current.rightText
              const showWrong = answerState !== "idle" && chosen && option.label !== current.rightText
              return (
                <div
                  key={option.label}
                  className={`syn-option ${showCorrect ? "correct" : ""} ${showWrong ? "wrong" : ""}`}
                >
                  <button
                    type="button"
                    className="syn-option-pick"
                    disabled={answerState !== "idle"}
                    onClick={() => answer(option.label)}
                  >
                    <span>{option.label}</span>
                  </button>
                  <SynAudioButtons text={option.label} audioBusy={audioBusy} onPlay={playText} />
                  {option.cn && (
                    shownOptionCn[option.label] ? (
                      <small>{option.cn}</small>
                    ) : (
                      <em
                        role="button"
                        tabIndex={0}
                        onClick={(event) => {
                          event.stopPropagation()
                          setShownOptionCn((value) => ({ ...value, [option.label]: true }))
                        }}
                        onKeyDown={(event) => {
                          if (event.key === "Enter" || event.key === " ") {
                            event.preventDefault()
                            event.stopPropagation()
                            setShownOptionCn((value) => ({ ...value, [option.label]: true }))
                          }
                        }}
                      >
                        显示中文
                      </em>
                    )
                  )}
                </div>
              )
            })}
          </div>

          {answerState !== "idle" && (
            <div className={`syn-feedback ${answerState}`}>
              <div>
                {answerState === "correct" ? <CheckCircle2 className="size-5" /> : <XCircle className="size-5" />}
                <strong>{answerState === "correct" ? "答对了" : "还需要再练一次"}</strong>
              </div>
              <p>{current.leftText} {current.leftCn && `(${current.leftCn})`} = {current.rightText} {current.rightCn && `(${current.rightCn})`}</p>
              <button type="button" onClick={nextQuestion}>
                <Shuffle className="size-4" />
                下一题
              </button>
            </div>
          )}
        </section>
      )}
    </main>
  )
}
