"use client"

import { useEffect, useMemo, useRef, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import {
  ArrowLeft,
  ArrowRight,
  BookOpenText,
  CheckCircle2,
  Headphones,
  Loader2,
  Mic,
  PenLine,
  RotateCcw,
  Settings,
  Square,
  Volume2,
} from "lucide-react"
import {
  fetchExpressionItems,
  fetchExpressionThemes,
  gradeExpression,
  saveExpressionAttempt,
  type ExpressionAnswerLevel,
  type ExpressionGradeResult,
  type ExpressionItem,
} from "@/lib/api/expression"
import { AsrRecorder } from "@/lib/exam/asr"
import { useAiConfig } from "@/lib/exam/use-ai-config"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"
import { AiSettingsPanel } from "@/components/ai/ai-settings-panel"

type Stage = "model" | "pattern" | "answer"
type PracticeMode = "speaking" | "writing"
type AnswerLevelKey = "basic" | "expanded" | "challenge"

const answerLevelLabels: Record<AnswerLevelKey, string> = {
  basic: "基础回答",
  expanded: "扩展回答",
  challenge: "进阶回答",
}

const MIC_HINT_STORAGE_KEY = "expression-mic-hint-seen"

function buildPattern(pattern: string, values: string[]) {
  let index = 0
  return pattern.replace(/___/g, () => values[index++] || "___")
}

export function ExpressionPractice() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const themeCode = searchParams.get("theme") || "food-drink"
  const recorderRef = useRef<AsrRecorder | null>(null)
  const config = useAiConfig()

  const [items, setItems] = useState<ExpressionItem[]>([])
  const [themeTitle, setThemeTitle] = useState("")
  const [index, setIndex] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [stage, setStage] = useState<Stage>("model")
  const [answerLevel, setAnswerLevel] = useState<AnswerLevelKey>("expanded")
  const [mode, setMode] = useState<PracticeMode>("speaking")
  const [responseText, setResponseText] = useState("")
  const [grade, setGrade] = useState<ExpressionGradeResult | null>(null)
  const [grading, setGrading] = useState(false)
  const [recording, setRecording] = useState(false)
  const [recordSeconds, setRecordSeconds] = useState(0)
  const [showMicHint, setShowMicHint] = useState(false)
  const micHintTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const [slotIndexes, setSlotIndexes] = useState<number[]>([])
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [ttsLoading, setTtsLoading] = useState(false)
  const [ttsSpeaking, setTtsSpeaking] = useState(false)

  const item = items[index]
  const answers = item?.answerJson.answers
  const selectedAnswer = answers?.[answerLevel] || answers?.basic
  const pattern = item?.answerJson.patterns?.[0]
  const slotEntries = useMemo(() => Object.entries(pattern?.slots || {}), [pattern])
  const patternText = useMemo(() => buildPattern(
    pattern?.en || "",
    slotEntries.map(([, options], i) => options[slotIndexes[i] || 0] || "___"),
  ), [pattern, slotEntries, slotIndexes])

  useEffect(() => {
    let active = true
    Promise.all([fetchExpressionItems(themeCode), fetchExpressionThemes("ket")])
      .then(([list, themes]) => {
        if (!active) return
        setItems(list)
        const selectedTheme = themes.find((theme) => theme.code === themeCode)
        setThemeTitle(selectedTheme?.nameEn || selectedTheme?.nameCn || themeCode)
        if (list[0]?.practiceMode === "writing") setMode("writing")
      })
      .catch((e: unknown) => active && setError(e instanceof Error ? e.message : "练习加载失败"))
      .finally(() => active && setLoading(false))
    return () => {
      active = false
      recorderRef.current?.release()
      stopTts()
    }
  }, [themeCode])

  useEffect(() => {
    return () => {
      if (micHintTimerRef.current) clearTimeout(micHintTimerRef.current)
    }
  }, [])

  async function speak(text: string) {
    if (!config.ttsEnabled || !text.trim()) return
    setError("")
    unlockAudio()
    try {
      if (config.ttsEngine === "system") {
        setTtsSpeaking(true)
        await speakWithSystem(text, config.selectedVoice)
      } else {
        await speakWithServer(text, config.ttsEngine, config.selectedVoice, {
          onLoadingChange: setTtsLoading,
          onSpeakingChange: setTtsSpeaking,
        })
      }
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "语音播放失败")
    } finally {
      setTtsLoading(false)
      setTtsSpeaking(false)
    }
  }

  function resetForItem(nextItem: ExpressionItem) {
    setStage("model")
    setAnswerLevel("expanded")
    setMode(nextItem.practiceMode === "writing" ? "writing" : "speaking")
    setResponseText("")
    setGrade(null)
    setSlotIndexes([])
    setError("")
  }

  function dismissMicHint() {
    setShowMicHint(false)
    if (micHintTimerRef.current) {
      clearTimeout(micHintTimerRef.current)
      micHintTimerRef.current = null
    }
  }

  async function toggleRecording() {
    setError("")
    if (!recorderRef.current) recorderRef.current = new AsrRecorder()
    if (!recording) {
      setRecordSeconds(0)
      const started = await recorderRef.current.startRecording(setRecordSeconds)
      if (!started) {
        setError(recorderRef.current.lastError || "无法使用麦克风，请检查浏览器权限")
        return
      }
      setRecording(true)
      if (typeof window !== "undefined" && !window.localStorage.getItem(MIC_HINT_STORAGE_KEY)) {
        window.localStorage.setItem(MIC_HINT_STORAGE_KEY, "1")
        setShowMicHint(true)
        micHintTimerRef.current = setTimeout(() => setShowMicHint(false), 5000)
      }
      return
    }
    dismissMicHint()
    setRecording(false)
    const result = await recorderRef.current.stopAndRecognize(config.currentAsr?.id || config.selectedAsrId)
    if ("error" in result) setError(result.error)
    else setResponseText(result.text)
  }

  async function submit() {
    if (!item || !responseText.trim()) return
    setGrading(true)
    setError("")
    try {
      const result = await gradeExpression({
        prompt: item.promptEn,
        responseText: responseText.trim(),
        practiceMode: mode,
        answerJson: item.answerJson,
        llm: config.currentLlm,
        useProxy: config.llmProxy,
      })
      setGrade(result)
      try {
        await saveExpressionAttempt(item.id, {
          practiceMode: mode,
          responseText: responseText.trim(),
          score: result.score,
          feedbackJson: result,
          durationSeconds: mode === "speaking" ? recordSeconds : undefined,
        })
      } catch {
        setError("反馈已生成，但本次学习记录保存失败")
      }
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "批改失败")
    } finally {
      setGrading(false)
    }
  }

  function nextItem() {
    if (index < items.length - 1) {
      resetForItem(items[index + 1])
      setIndex((value) => value + 1)
      return
    }
    router.push("/expression")
  }

  if (loading) return <main className="expression-shell"><div className="expression-state"><Loader2 className="size-6 animate-spin" />正在加载练习</div></main>
  if (!item) return <main className="expression-shell"><div className="expression-state expression-error"><span>{error || "这个主题还没有练习内容"}</span><button type="button" onClick={() => router.push("/expression")}>返回主题</button></div></main>

  return (
    <main className="expression-shell expression-practice-shell">
      <header className="expression-header">
        <button type="button" className="expression-icon-btn" onClick={() => router.push("/expression")} title="返回主题">
          <ArrowLeft className="size-5" />
        </button>
        <div className="expression-progress-copy">
          <h1>{themeTitle || "KET Expression"}</h1>
          <p>{index + 1} / {items.length}</p>
        </div>
        <div className="expression-header-actions">
          <span className="expression-score-badge">{item.bestSpeakingScore ?? item.bestWritingScore ?? "新题"}</span>
          <button type="button" className="expression-icon-btn" onClick={() => setSettingsOpen((open) => !open)} title="AI 设置" aria-expanded={settingsOpen}>
            <Settings className="size-5" />
          </button>
        </div>
      </header>
      {settingsOpen && (
        <section className="expression-ai-settings" aria-label="AI 设置">
          <div className="expression-ai-settings-head">
            <strong>AI 设置</strong>
            {(ttsLoading || ttsSpeaking) && <button type="button" onClick={stopTts}>停止播放</button>}
          </div>
          <AiSettingsPanel config={config} />
        </section>
      )}
      <div className="expression-progress-track"><span style={{ width: `${((index + 1) / items.length) * 100}%` }} /></div>

      {(item.imageUrlsJson || []).length > 0 && (
        <section className="expression-image-strip">
          {item.imageUrlsJson!.map((url, imageIndex) => (
            // eslint-disable-next-line @next/next/no-img-element
            <img key={url} src={url} alt={`Question picture ${imageIndex + 1}`} />
          ))}
        </section>
      )}

      <section className="expression-question">
        <div className="expression-question-label">Question</div>
        <div className="expression-question-row">
          <div>
            <h2>{item.promptEn}</h2>
            {item.promptCn && <p>{item.promptCn}</p>}
          </div>
          <button type="button" className="expression-round-action" onClick={() => speak(item.promptEn)} title="播放问题">
            <Volume2 className="size-5" />
          </button>
        </div>
      </section>

      <nav className="expression-stage-tabs" aria-label="练习阶段">
        <button type="button" className={stage === "model" ? "active" : ""} onClick={() => setStage("model")}><Headphones className="size-4" />听范文</button>
        <button type="button" className={stage === "pattern" ? "active" : ""} onClick={() => setStage("pattern")}><BookOpenText className="size-4" />换一换</button>
        <button type="button" className={stage === "answer" ? "active" : ""} onClick={() => setStage("answer")}><Mic className="size-4" />我来答</button>
      </nav>

      {stage === "model" && (
        <section className="expression-workspace">
          <div className="expression-level-switch">
            {(Object.keys(answerLevelLabels) as AnswerLevelKey[]).map((key) => answers?.[key] && (
              <button key={key} type="button" className={answerLevel === key ? "active" : ""} onClick={() => setAnswerLevel(key)}>{answerLevelLabels[key]}</button>
            ))}
          </div>
          {selectedAnswer && <AnswerCard answer={selectedAnswer} onPlay={() => speak(selectedAnswer.en)} />}
          <button type="button" className="expression-primary" onClick={() => setStage("pattern")}>练习句型<ArrowRight className="size-4" /></button>
        </section>
      )}

      {stage === "pattern" && (
        <section className="expression-workspace">
          {pattern ? (
            <>
              <div className="expression-pattern-card">
                <span>Sentence pattern</span>
                <strong>{patternText}</strong>
                {pattern.cn && <p>{pattern.cn}</p>}
                <button type="button" className="expression-round-action" onClick={() => speak(patternText)} title="播放句子"><Volume2 className="size-4" /></button>
              </div>
              <div className="expression-slot-list">
                {slotEntries.map(([name, options], slotIndex) => (
                  <label key={name}>
                    <span>{name}</span>
                    <select value={slotIndexes[slotIndex] || 0} onChange={(event) => setSlotIndexes((current) => {
                      const next = [...current]
                      next[slotIndex] = Number(event.target.value)
                      return next
                    })}>
                      {options.map((option, optionIndex) => <option key={option} value={optionIndex}>{option}</option>)}
                    </select>
                  </label>
                ))}
              </div>
            </>
          ) : <p className="expression-muted">这道题暂时没有句型替换配置。</p>}
          <button
            type="button"
            className="expression-primary"
            onClick={() => {
              if (pattern) setResponseText(patternText)
              setStage("answer")
            }}
          >
            开始练习<ArrowRight className="size-4" />
          </button>
        </section>
      )}

      {stage === "answer" && (
        <section className="expression-workspace">
          {item.practiceMode === "both" && (
            <div className="expression-mode-switch">
              <button type="button" className={mode === "speaking" ? "active" : ""} onClick={() => { setMode("speaking"); setGrade(null) }}><Mic className="size-4" />口语</button>
              <button type="button" className={mode === "writing" ? "active" : ""} onClick={() => { setMode("writing"); setGrade(null) }}><PenLine className="size-4" />写作</button>
            </div>
          )}

          {mode === "speaking" && (
            <div className="expression-record-wrap">
              <button type="button" className={`expression-record ${recording ? "recording" : ""}`} onClick={toggleRecording} disabled={grading}>
                {recording ? <Square className="size-6" /> : <Mic className="size-7" />}
                <strong>{recording ? `${recordSeconds}s 点击停止` : "点击开始回答"}</strong>
                <span>录音完成后会自动转成文字</span>
              </button>
              {showMicHint && (
                <p className="expression-mic-hint" role="status">
                  说完后再次点击麦克风结束录音
                </p>
              )}
            </div>
          )}

          <label className="expression-answer-input">
            <span>{mode === "speaking" ? "识别结果" : "写下你的回答"}</span>
            <textarea value={responseText} onChange={(event) => { setResponseText(event.target.value); setGrade(null) }} placeholder="Type your answer in English..." rows={4} />
          </label>

          {error && <p className="expression-inline-error">{error}</p>}
          {!grade ? (
            <button type="button" className="expression-primary" disabled={grading || !responseText.trim() || recording} onClick={submit}>
              {grading ? <Loader2 className="size-4 animate-spin" /> : <CheckCircle2 className="size-4" />}
              {grading ? "AI 正在分析" : "提交并查看反馈"}
            </button>
          ) : (
            <Feedback result={grade} onReplay={() => speak(grade.revised_answer)} />
          )}
          {grade && <div className="expression-footer-actions">
            <button type="button" className="expression-secondary" onClick={() => { setGrade(null); setResponseText("") }}><RotateCcw className="size-4" />再答一次</button>
          </div>}
        </section>
      )}

      <div className="expression-skip-actions">
        <button type="button" className="expression-primary" disabled={grading || recording} onClick={nextItem}>
          {index < items.length - 1 ? "下一题" : "完成练习"}<ArrowRight className="size-4" />
        </button>
      </div>
    </main>
  )
}

function AnswerCard({ answer, onPlay }: { answer: ExpressionAnswerLevel; onPlay: () => void }) {
  return <div className="expression-answer-card">
    <p>{answer.en}</p>
    <span>{answer.cn}</span>
    <button type="button" className="expression-round-action" onClick={onPlay} title="播放范文"><Volume2 className="size-5" /></button>
  </div>
}

function Feedback({ result, onReplay }: { result: ExpressionGradeResult; onReplay: () => void }) {
  const dimensions = [
    ["内容", result.dimensions.content],
    ["语法", result.dimensions.grammar],
    ["词汇", result.dimensions.vocabulary],
    ["表达", result.dimensions.delivery],
  ] as const
  return <section className="expression-feedback">
    <div className="expression-feedback-head">
      <div className={result.passed ? "passed" : "retry"}>{result.score}</div>
      <div><strong>{result.summary_en}</strong><p>{result.summary_cn}</p></div>
    </div>
    <div className="expression-dimensions">
      {dimensions.map(([label, score]) => <div key={label}><span>{label}</span><strong>{score}</strong><i><b style={{ width: `${score}%` }} /></i></div>)}
    </div>
    <div className="expression-revised">
      <span>Better answer</span>
      <p>{result.revised_answer}</p>
      <button type="button" className="expression-round-action" onClick={onReplay} title="播放改进答案"><Volume2 className="size-4" /></button>
    </div>
    {(result.improvements || []).length > 0 && <ul>{result.improvements.map((text) => <li key={text}>{text}</li>)}</ul>}
  </section>
}
