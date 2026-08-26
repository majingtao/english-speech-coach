"use client"

import { useEffect, useMemo, useRef, useState } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  ArrowRight,
  BookOpenText,
  CheckCircle2,
  ChevronLeft,
  Eye,
  EyeOff,
  Loader2,
  Mic,
  PenLine,
  RotateCcw,
  Sparkles,
  Square,
  Volume2,
} from "lucide-react"
import {
  fetchPersonalPractices,
  gradePersonalPractice,
  savePersonalPracticeAttempt,
  type PersonalPractice,
  type PersonalPracticeGrade,
  type PersonalPracticeType,
} from "@/lib/api/personal-practice"
import { AsrRecorder } from "@/lib/exam/asr"
import { useAiConfig } from "@/lib/exam/use-ai-config"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"

type Stage = "learn" | "answer" | "result"

function makeResponseLines(practice: PersonalPractice) {
  return Array.from({ length: Math.max(1, practice.minSentences, practice.referenceLines.length) }, () => "")
}

export function PersonalPracticePage({ type }: { type: PersonalPracticeType }) {
  const router = useRouter()
  const config = useAiConfig()
  const recorderRef = useRef<AsrRecorder | null>(null)
  const [items, setItems] = useState<PersonalPractice[]>([])
  const [index, setIndex] = useState(0)
  const [stage, setStage] = useState<Stage>("learn")
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [showChinese, setShowChinese] = useState(true)
  const [responseLines, setResponseLines] = useState<string[]>([""])
  const [grade, setGrade] = useState<PersonalPracticeGrade | null>(null)
  const [grading, setGrading] = useState(false)
  const [recording, setRecording] = useState(false)
  const [recordSeconds, setRecordSeconds] = useState(0)
  const [ttsLoading, setTtsLoading] = useState(false)
  const [ttsSpeaking, setTtsSpeaking] = useState(false)
  const item = items[index]
  const responseText = useMemo(() => responseLines.map((line) => line.trim()).filter(Boolean).join("\n"), [responseLines])
  const wordCount = useMemo(() => responseText.split(/\s+/).filter(Boolean).length, [responseText])
  const isSpeaking = type === "speaking"

  useEffect(() => {
    let active = true
    fetchPersonalPractices(type)
      .then((list) => {
        if (!active) return
        setItems(list)
        if (list[0]) setResponseLines(makeResponseLines(list[0]))
      })
      .catch((e: unknown) => active && setError(e instanceof Error ? e.message : "练习加载失败"))
      .finally(() => active && setLoading(false))
    return () => {
      active = false
      recorderRef.current?.release()
      stopTts()
    }
  }, [type])

  function resetFor(next: PersonalPractice) {
    setStage("learn")
    setResponseLines(makeResponseLines(next))
    setGrade(null)
    setError("")
    setRecordSeconds(0)
    setRecording(false)
    stopTts()
  }

  async function speak(text: string) {
    if (!text.trim()) return
    setError("")
    unlockAudio()
    try {
      if (!config.ttsEnabled || config.ttsEngine === "system") {
        setTtsSpeaking(true)
        await speakWithSystem(text, config.selectedVoice)
      } else {
        await speakWithServer(text, config.ttsEngine, config.selectedVoice, {
          onLoadingChange: setTtsLoading,
          onSpeakingChange: setTtsSpeaking,
        })
      }
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "播放失败")
    } finally {
      setTtsLoading(false)
      setTtsSpeaking(false)
    }
  }

  async function playAll() {
    if (!item) return
    await speak([item.promptEn, ...item.referenceLines.map((line) => line.en)].join("\n"))
  }

  async function toggleRecording() {
    setError("")
    if (!recorderRef.current) recorderRef.current = new AsrRecorder()
    if (!recording) {
      setRecordSeconds(0)
      const started = await recorderRef.current.startRecording(setRecordSeconds)
      if (!started) {
        setError(recorderRef.current.lastError || "无法使用麦克风")
        return
      }
      setRecording(true)
      return
    }
    setRecording(false)
    const result = await recorderRef.current.stopAndRecognize(config.currentAsr?.id || config.selectedAsrId)
    if ("error" in result) setError(result.error)
    else setResponseLines([result.text])
  }

  async function submit() {
    if (!item || !responseText) return
    setGrading(true)
    setError("")
    try {
      const result = await gradePersonalPractice({
        practice: item,
        responseText,
        llm: config.currentLlm,
        useProxy: config.llmProxy,
      })
      setGrade(result)
      setStage("result")
      try {
        await savePersonalPracticeAttempt({
          practiceId: item.id,
          responseText,
          grade: result,
          durationSeconds: isSpeaking ? recordSeconds : undefined,
        })
        setItems((current) => current.map((practice) => practice.id === item.id ? {
          ...practice,
          attemptCount: (practice.attemptCount || 0) + 1,
          bestScore: Math.max(practice.bestScore || 0, result.totalScore),
          lastScore: result.totalScore,
          progressStatus: result.totalScore >= 80 ? 2 : 1,
        } : practice))
      } catch {
        setError("评分已完成，但学习记录保存失败")
      }
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "AI 评分失败")
    } finally {
      setGrading(false)
    }
  }

  function move(delta: number) {
    const nextIndex = index + delta
    if (nextIndex < 0 || nextIndex >= items.length) return
    setIndex(nextIndex)
    resetFor(items[nextIndex])
  }

  if (loading) return <main className="personal-shell"><div className="personal-state"><Loader2 className="size-6 animate-spin" />正在加载专属练习</div></main>
  if (error && !item) return <main className="personal-shell"><div className="personal-state personal-error">{error}</div></main>
  if (!item) return (
    <main className="personal-shell">
      <header className="personal-header"><button type="button" onClick={() => router.push("/")}><ArrowLeft /></button><h1>{isSpeaking ? "专属口语" : "专属写作"}</h1></header>
      <div className="personal-state">管理员还没有发布{isSpeaking ? "口语" : "写作"}练习</div>
    </main>
  )

  return (
    <main className="personal-shell">
      <header className="personal-header">
        <button type="button" onClick={() => router.push("/")} title="返回首页"><ArrowLeft /></button>
        <div><h1>{isSpeaking ? "专属口语" : "专属写作"}</h1><p>{item.title}</p></div>
        <span>{index + 1} / {items.length}</span>
      </header>

      <div className="personal-progress"><span style={{ width: `${((index + 1) / items.length) * 100}%` }} /></div>

      <section className="personal-question">
        <div className="personal-kicker">{isSpeaking ? <Mic /> : <PenLine />} Question</div>
        <div className="personal-question-row">
          <div><h2>{item.promptEn}</h2>{showChinese && item.promptCn && <p>{item.promptCn}</p>}</div>
          <button type="button" onClick={() => speak(item.promptEn)} disabled={ttsLoading} title="播放题目">
            {ttsLoading ? <Loader2 className="animate-spin" /> : <Volume2 />}
          </button>
        </div>
        <div className="personal-meta">
          <span>至少 {item.minSentences} 句</span>
          {!isSpeaking && item.minWords > 0 && <span>至少 {item.minWords} 词</span>}
          {item.bestScore !== undefined && <span>最高 {item.bestScore} 分</span>}
          <button type="button" onClick={() => setShowChinese((value) => !value)}>{showChinese ? <EyeOff /> : <Eye />}{showChinese ? "隐藏中文" : "显示中文"}</button>
        </div>
      </section>

      {stage === "learn" && (
        <section className="personal-panel">
          <div className="personal-panel-head"><div><BookOpenText /><span>先学习参考表达</span></div><button type="button" onClick={playAll}><Volume2 />连续播放</button></div>
          <div className="personal-reference-list">
            {item.referenceLines.map((line, lineIndex) => (
              <article key={`${line.en}-${lineIndex}`}>
                <span>{lineIndex + 1}</span>
                <div><strong>{line.en}</strong>{showChinese && line.cn && <p>{line.cn}</p>}</div>
                <button type="button" onClick={() => speak(line.en)} title={`播放第 ${lineIndex + 1} 句`}><Volume2 /></button>
              </article>
            ))}
          </div>
          {item.contentPoints.length > 0 && <div className="personal-targets"><strong>KET 基础要求</strong>{item.contentPoints.map((point) => <span key={point}><CheckCircle2 />{point}</span>)}</div>}
          <button type="button" className="personal-primary" onClick={() => { setStage("answer"); stopTts() }}>开始作答<ArrowRight /></button>
        </section>
      )}

      {stage === "answer" && (
        <section className="personal-panel">
          <div className="personal-panel-head"><div>{isSpeaking ? <Mic /> : <PenLine />}<span>{isSpeaking ? "录下你的回答" : "一句一句写下来"}</span></div></div>
          {isSpeaking ? (
            <>
              <button type="button" className={`personal-record ${recording ? "recording" : ""}`} onClick={toggleRecording} disabled={grading}>
                {recording ? <Square /> : <Mic />}<strong>{recording ? `${recordSeconds}s 点击停止` : "点击开始回答"}</strong>
              </button>
              <label className="personal-input"><span>识别结果</span><textarea rows={5} value={responseLines[0] || ""} onChange={(event) => setResponseLines([event.target.value])} placeholder="录音后也可以在这里修改识别结果" /></label>
            </>
          ) : (
            <div className="personal-writing-lines">
              {responseLines.map((line, lineIndex) => (
                <label key={lineIndex}><span>{lineIndex + 1}</span><textarea rows={2} value={line} onChange={(event) => setResponseLines((current) => current.map((value, i) => i === lineIndex ? event.target.value : value))} placeholder={`Write sentence ${lineIndex + 1}...`} /></label>
              ))}
              <div className="personal-word-count">{wordCount} 词</div>
            </div>
          )}
          {error && <p className="personal-error-text">{error}</p>}
          <div className="personal-actions">
            <button type="button" className="personal-secondary" onClick={() => setStage("learn")}><ChevronLeft />查看参考</button>
            <button type="button" className="personal-primary" disabled={!responseText || grading || recording} onClick={submit}>
              {grading ? <Loader2 className="animate-spin" /> : <Sparkles />}AI 评分
            </button>
          </div>
        </section>
      )}

      {stage === "result" && grade && (
        <section className="personal-panel personal-result">
          <div className="personal-score"><strong>{grade.totalScore}</strong><span>总分</span></div>
          <div className="personal-score-breakdown">
            <div><span>语法</span><strong>{grade.grammarScore} / 60</strong></div>
            <div><span>KET 内容</span><strong>{grade.contentScore} / 40</strong></div>
          </div>
          <div className={`personal-verdict ${grade.grammarCorrect ? "correct" : "needs-work"}`}>
            <CheckCircle2 /><strong>{grade.grammarCorrect ? "语法正确" : "语法需要修改"}</strong>
          </div>
          {!grade.grammarCorrect && <AnswerBlock title="语法修正" text={grade.correctedAnswer} onPlay={() => speak(grade.correctedAnswer)} />}
          <AnswerBlock title="KET 更完整答案" text={grade.enrichedAnswer} onPlay={() => speak(grade.enrichedAnswer)} />
          {(grade.contentFeedbackCn || grade.missingPoints.length > 0) && (
            <div className="personal-feedback">
              {grade.contentFeedbackCn && <p>{grade.contentFeedbackCn}</p>}
              {grade.missingPoints.map((point) => <span key={point}>{point}</span>)}
            </div>
          )}
          {error && <p className="personal-error-text">{error}</p>}
          <div className="personal-actions">
            <button type="button" className="personal-secondary" onClick={() => { setStage("answer"); setGrade(null) }}><RotateCcw />再练一次</button>
            <button type="button" className="personal-primary" disabled={index >= items.length - 1} onClick={() => move(1)}>下一题<ArrowRight /></button>
          </div>
        </section>
      )}

      <nav className="personal-pager">
        <button type="button" disabled={index === 0} onClick={() => move(-1)}><ChevronLeft />上一题</button>
        <button type="button" disabled={index === items.length - 1} onClick={() => move(1)}>下一题<ArrowRight /></button>
      </nav>
      {(ttsLoading || ttsSpeaking) && <button type="button" className="personal-stop-audio" onClick={stopTts}>停止播放</button>}
    </main>
  )
}

function AnswerBlock({ title, text, onPlay }: { title: string; text: string; onPlay: () => void }) {
  return <div className="personal-answer-block"><span>{title}</span><p>{text}</p><button type="button" onClick={onPlay} title={`播放${title}`}><Volume2 /></button></div>
}
