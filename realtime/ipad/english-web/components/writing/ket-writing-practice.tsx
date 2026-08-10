"use client"

import { useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  BookOpenText,
  CheckCircle2,
  ClipboardList,
  ImagePlus,
  Loader2,
  PenLine,
  RotateCcw,
  Settings,
  Sparkles,
  Volume2,
} from "lucide-react"
import tasksData from "@/lib/writing/ket-writing-bank.json"
import {
  fetchKetWritingTasks,
  generateKetWritingDraft,
  gradeKetWriting,
  fetchKetWritingLatest,
  saveKetWritingAttempt,
  type KetWritingAttempt,
  type KetWritingDraftResult,
  type KetWritingGradeResult,
  type KetWritingTask,
} from "@/lib/api/writing"
import { AiSettingsPanel } from "@/components/ai/ai-settings-panel"
import { useAiConfig } from "@/lib/exam/use-ai-config"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"

type PracticeMode = "guided" | "imitate" | "free"

const tasks = tasksData as KetWritingTask[]

const modeLabels: Record<PracticeMode, string> = {
  guided: "跟写",
  imitate: "仿写",
  free: "独立写",
}

const dimensionLabels: Record<keyof KetWritingGradeResult["dimensions"], string> = {
  task: "任务完成",
  grammar: "语法",
  spelling: "拼写",
  organization: "结构",
}

const fallbackDraftFields = [
  { key: "who", label: "Who? 写谁？", placeholder: "my friend Tom / a boy called Jack" },
  { key: "where", label: "Where? 在哪里？", placeholder: "at school / in the park" },
  { key: "what", label: "What happened? 发生了什么？", placeholder: "we played football" },
  { key: "ending", label: "How did it end? 结尾怎样？", placeholder: "we were very happy" },
]

function countWords(text: string) {
  const matches = text.trim().match(/[A-Za-z]+(?:'[A-Za-z]+)?/g)
  return matches ? matches.length : 0
}

function starterFromFrame(task: KetWritingTask) {
  return task.writingFrame.join("\n")
}

function formatAttemptTime(value?: string | null) {
  if (!value) return ""
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString("zh-CN", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  })
}

export function KetWritingPractice() {
  const router = useRouter()
  const config = useAiConfig()
  const [taskList, setTaskList] = useState<KetWritingTask[]>(tasks)
  const [taskLoadError, setTaskLoadError] = useState("")
  const [taskIndex, setTaskIndex] = useState(0)
  const [mode, setMode] = useState<PracticeMode>("guided")
  const [answer, setAnswer] = useState("")
  const [grade, setGrade] = useState<KetWritingGradeResult | null>(null)
  const [grading, setGrading] = useState(false)
  const [error, setError] = useState("")
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [ttsLoading, setTtsLoading] = useState(false)
  const [ttsSpeaking, setTtsSpeaking] = useState(false)
  const [draftInfo, setDraftInfo] = useState<Record<string, string>>({})
  const [draftLoading, setDraftLoading] = useState(false)
  const [draftResult, setDraftResult] = useState<KetWritingDraftResult | null>(null)
  const [latestAttempt, setLatestAttempt] = useState<KetWritingAttempt | null>(null)
  const [attemptCount, setAttemptCount] = useState(0)
  const [attemptLoading, setAttemptLoading] = useState(true)

  const task = taskList[taskIndex] || taskList[0]
  const wordCount = useMemo(() => countWords(answer), [answer])
  const filteredTasks = taskList
  const partLabel = task.part === 6 ? "Part 6 短邮件" : "Part 7 看图故事"
  const sourceLabel = task.sourceUnit ? `Unit ${task.sourceUnit}` : `Page ${task.sourcePage || ""}`.trim()
  const draftFields = task.aiDraftFields?.length ? task.aiDraftFields : fallbackDraftFields

  useEffect(() => {
    let active = true
    void fetchKetWritingTasks()
      .then((data) => {
        if (!active || data.length === 0) return
        setTaskList(data)
        setTaskIndex(0)
        setAnswer("")
        setGrade(null)
        setMode(data[0]?.part === 7 ? "imitate" : "guided")
        setDraftInfo({})
        setDraftResult(null)
        setTaskLoadError("")
        setAttemptLoading(true)
      })
      .catch(() => {
        if (active) setTaskLoadError("题库接口暂时不可用，当前使用本地题库。")
      })
    return () => {
      active = false
    }
  }, [])

  useEffect(() => {
    let active = true
    void fetchKetWritingLatest(task.id)
      .then((data) => {
        if (!active) return
        setLatestAttempt(data.latest || null)
        setAttemptCount(data.attemptCount)
      })
      .catch(() => {
        if (!active) return
        setLatestAttempt(null)
        setAttemptCount(0)
      })
      .finally(() => {
        if (active) setAttemptLoading(false)
      })
    return () => {
      active = false
    }
  }, [task.id])

  function selectTask(nextIndex: number) {
    setTaskIndex(nextIndex)
    setAnswer("")
    setGrade(null)
    setError("")
    setMode(taskList[nextIndex]?.part === 7 ? "imitate" : "guided")
    setDraftInfo({})
    setDraftResult(null)
    setAttemptLoading(true)
  }

  function restoreLatestAttempt() {
    if (!latestAttempt) return
    setMode(latestAttempt.practiceMode)
    setAnswer(latestAttempt.responseText)
    setGrade(latestAttempt.feedback || null)
    setError("")
  }

  function applyFrame() {
    setAnswer(starterFromFrame(task))
    setGrade(null)
  }

  async function speakSample() {
    const text = task.sampleAnswer.trim()
    if (!text) return
    if (ttsLoading || ttsSpeaking) {
      stopTts()
      setTtsLoading(false)
      setTtsSpeaking(false)
      return
    }
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
      setError(e instanceof Error ? e.message : "范文播放失败")
    } finally {
      setTtsLoading(false)
      setTtsSpeaking(false)
    }
  }

  async function submit() {
    if (!answer.trim()) return
    setGrading(true)
    setError("")
    try {
      const result = await gradeKetWriting({
        task,
        responseText: answer.trim(),
        mode,
        llm: config.currentLlm,
        useProxy: config.llmProxy,
      })
      setGrade(result)
      try {
        const saved = await saveKetWritingAttempt({
          task,
          mode,
          learnerInfo: draftInfo,
          aiDraftText: draftResult?.draft,
          responseText: answer.trim(),
          wordCount,
          feedback: result,
        })
        if (saved) {
          setLatestAttempt(saved)
          setAttemptCount(saved.attemptCount)
        }
      } catch {
        setError("批改已完成，但这次记录保存失败。")
      }
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "批改失败")
    } finally {
      setGrading(false)
    }
  }

  async function generateDraft() {
    setDraftLoading(true)
    setDraftResult(null)
    setError("")
    try {
      setDraftResult(await generateKetWritingDraft({
        task,
        learnerInfo: draftInfo,
        llm: config.currentLlm,
        useProxy: config.llmProxy,
      }))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "AI仿写失败")
    } finally {
      setDraftLoading(false)
    }
  }

  function useDraft() {
    if (!draftResult?.draft) return
    setAnswer(draftResult.draft)
    setGrade(null)
  }

  return (
    <main className="ket-writing-shell">
      <header className="ket-writing-header">
        <button type="button" className="expression-icon-btn" onClick={() => router.push("/")} title="返回首页">
          <ArrowLeft className="size-5" />
        </button>
        <div>
          <h1>KET写作练习</h1>
          <p>范文模仿 + AI老师纠错</p>
        </div>
        <button type="button" className="expression-icon-btn" onClick={() => setSettingsOpen((open) => !open)} title="AI 设置">
          <Settings className="size-5" />
        </button>
      </header>

      {settingsOpen && (
        <section className="expression-ai-settings" aria-label="AI 设置">
          <div className="expression-ai-settings-head">
            <strong>AI 设置</strong>
          </div>
          <AiSettingsPanel config={config} />
        </section>
      )}

      <section className="ket-writing-layout">
        <aside className="ket-writing-sidebar">
          <div className="ket-writing-filter">
            <strong>题库</strong>
            <span>{filteredTasks.length} 题</span>
          </div>
          {taskLoadError && <p className="ket-writing-bank-warning">{taskLoadError}</p>}
          <div className="ket-writing-task-list">
            {filteredTasks.map((item, index) => (
              <button
                key={item.id}
                type="button"
                className={index === taskIndex ? "active" : ""}
                onClick={() => selectTask(index)}
              >
                <span>{item.part === 6 ? "邮件" : "故事"}</span>
                <strong>{item.title}</strong>
                <small>{item.sourceUnit ? `Unit ${item.sourceUnit}` : `Page ${item.sourcePage || ""}`.trim()} · {item.targetWords} words</small>
              </button>
            ))}
          </div>
        </aside>

        <section className="ket-writing-main">
          <div className="ket-writing-task-card">
            <div className="ket-writing-task-meta">
              <span>{partLabel}</span>
              <span>{sourceLabel}</span>
              <span>{task.targetWords} words</span>
              {task.status === "needs_image" && <span>待补图</span>}
            </div>
            <h2>{task.title}</h2>
            <p>{task.promptEn}</p>
            {task.promptCn && <small>{task.promptCn}</small>}
            <ul className="ket-writing-requirements">
              {task.requirements.map((item) => <li key={item}>{item}</li>)}
            </ul>
          </div>

          {task.part === 7 && (
            <section className="ket-writing-pictures">
              {task.imageUrls.length > 0 ? task.imageUrls.map((url, index) => (
                // eslint-disable-next-line @next/next/no-img-element
                <img key={url} src={url} alt={`Story picture ${index + 1}`} />
              )) : task.imageSlots.map((slot) => (
                <div key={slot.slot}>
                  <ImagePlus className="size-5" />
                  <span>图片 {slot.slot}</span>
                  <small>后续手动补图</small>
                </div>
              ))}
            </section>
          )}

          {task.picturePrompts.length > 0 && (
            <section className="ket-writing-prompts">
              {task.picturePrompts.map((item) => <p key={item}>{item}</p>)}
            </section>
          )}

          <nav className="ket-writing-mode" aria-label="写作模式">
            {(Object.keys(modeLabels) as PracticeMode[]).map((key) => (
              <button key={key} type="button" className={mode === key ? "active" : ""} onClick={() => setMode(key)}>
                {modeLabels[key]}
              </button>
            ))}
          </nav>

          {(attemptLoading || latestAttempt || attemptCount > 0) && (
            <section className="ket-writing-last-attempt">
              {attemptLoading ? (
                <span>正在读取上次提交...</span>
              ) : latestAttempt ? (
                <>
                  <div>
                    <strong>上次提交</strong>
                    <span>
                      {modeLabels[latestAttempt.practiceMode]} · {latestAttempt.wordCount} words · 共 {attemptCount} 次
                      {latestAttempt.createTime ? ` · ${formatAttemptTime(latestAttempt.createTime)}` : ""}
                    </span>
                  </div>
                  <button type="button" className="expression-secondary" onClick={restoreLatestAttempt}>
                    继续修改
                  </button>
                </>
              ) : (
                <span>这道题还没有提交记录</span>
              )}
            </section>
          )}

          <section className="ket-writing-study-grid">
            {mode !== "free" && task.sampleAnswer.trim() && (
              <div className="ket-writing-study-card">
                <div className="ket-writing-card-head">
                  <BookOpenText className="size-4" />
                  <strong>范文</strong>
                  <button type="button" onClick={speakSample} disabled={ttsLoading} title={ttsSpeaking ? "停止播放" : "播放范文"}>
                    {ttsLoading ? <Loader2 className="size-4 animate-spin" /> : <Volume2 className="size-4" />}
                    {ttsSpeaking ? "停止" : "播放"}
                  </button>
                </div>
                <pre>{task.sampleAnswer}</pre>
              </div>
            )}
            {mode === "guided" && (
              <div className="ket-writing-study-card">
                <div className="ket-writing-card-head">
                  <ClipboardList className="size-4" />
                  <strong>跟写框架</strong>
                  <button type="button" onClick={applyFrame}>填入</button>
                </div>
                <pre>{task.writingFrame.join("\n")}</pre>
              </div>
            )}
            {mode === "imitate" && (
              <div className="ket-writing-study-card ket-writing-ai-card">
                <div className="ket-writing-card-head">
                  <Sparkles className="size-4" />
                  <strong>AI仿写助手</strong>
                  <button type="button" onClick={generateDraft} disabled={draftLoading}>
                    {draftLoading ? <Loader2 className="size-4 animate-spin" /> : null}
                    生成
                  </button>
                </div>
                <div className="ket-writing-ai-fields">
                  {draftFields.map((field) => (
                    <label key={field.key}>
                      <span>{field.label}</span>
                      <input
                        value={draftInfo[field.key] || ""}
                        placeholder={field.placeholder}
                        onChange={(event) => {
                          setDraftInfo((current) => ({ ...current, [field.key]: event.target.value }))
                          setDraftResult(null)
                        }}
                      />
                    </label>
                  ))}
                </div>
                {draftResult && (
                  <div className="ket-writing-ai-result">
                    <pre>{draftResult.draft}</pre>
                    {draftResult.tips_cn.length > 0 && (
                      <ul>{draftResult.tips_cn.map((tip) => <li key={tip}>{tip}</li>)}</ul>
                    )}
                    <button type="button" className="expression-secondary" onClick={useDraft}>
                      填入作文框
                    </button>
                  </div>
                )}
              </div>
            )}
          </section>

          <section className="ket-writing-support">
            {task.supportPhrases.map((item) => <span key={item}>{item}</span>)}
          </section>

          <label className="ket-writing-editor">
            <span>
              <PenLine className="size-4" />
              我的作文
              <b>{wordCount} words</b>
            </span>
            <textarea
              value={answer}
              onChange={(event) => { setAnswer(event.target.value); setGrade(null) }}
              rows={9}
              placeholder="Write your answer in English..."
            />
          </label>

          {error && <p className="expression-inline-error">{error}</p>}
          {!grade ? (
            <button type="button" className="expression-primary" disabled={grading || !answer.trim()} onClick={submit}>
              {grading ? <Loader2 className="size-4 animate-spin" /> : <CheckCircle2 className="size-4" />}
              {grading ? "AI老师正在批改" : "提交给AI老师纠错"}
            </button>
          ) : (
            <WritingFeedback result={grade} onReset={() => { setGrade(null); setAnswer("") }} />
          )}
        </section>
      </section>
    </main>
  )
}

function WritingFeedback({ result, onReset }: { result: KetWritingGradeResult; onReset: () => void }) {
  return (
    <section className="ket-writing-feedback">
      <div className={`ket-writing-feedback-level ${result.level}`}>
        <Sparkles className="size-5" />
        <strong>{result.summary_cn}</strong>
      </div>
      <div className="ket-writing-dimensions">
        {Object.entries(result.dimensions).map(([key, value]) => (
          <div key={key}>
            <span>{dimensionLabels[key as keyof KetWritingGradeResult["dimensions"]]}</span>
            <strong>{value}</strong>
          </div>
        ))}
      </div>
      {result.teacher_notes.length > 0 && (
        <div className="ket-writing-feedback-block">
          <h3>老师讲解</h3>
          <ul>{result.teacher_notes.map((item) => <li key={item}>{item}</li>)}</ul>
        </div>
      )}
      {result.corrections.length > 0 && (
        <div className="ket-writing-feedback-block">
          <h3>具体纠错</h3>
          {result.corrections.map((item, index) => (
            <div key={`${item.corrected}-${index}`} className="ket-writing-correction">
              {item.original && <p><span>原句</span>{item.original}</p>}
              {item.corrected && <p><span>改成</span>{item.corrected}</p>}
              {item.reason_cn && <small>{item.reason_cn}</small>}
            </div>
          ))}
        </div>
      )}
      <div className="ket-writing-feedback-block">
        <h3>修改后范文</h3>
        <pre>{result.revised_answer}</pre>
      </div>
      {result.useful_sentences.length > 0 && (
        <div className="ket-writing-feedback-block">
          <h3>可以背的句子</h3>
          <ul>{result.useful_sentences.map((item) => <li key={item}>{item}</li>)}</ul>
        </div>
      )}
      <div className="ket-writing-next-goal">{result.next_goal_cn}</div>
      <button type="button" className="expression-secondary" onClick={onReset}>
        <RotateCcw className="size-4" />
        再写一次
      </button>
    </section>
  )
}
