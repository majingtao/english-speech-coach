"use client"

import { useEffect, useMemo, useRef, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import {
  ArrowLeft,
  CheckCircle2,
  ImageOff,
  Loader2,
  Mic,
  RefreshCw,
  Send,
  Settings,
  Square,
  Users,
  Volume2,
} from "lucide-react"
import {
  fetchExpressionDialogueTask,
  gradeExpressionDialogue,
  requestDialoguePartnerTurn,
  saveExpressionDialogueAttempt,
  type DialogueRole,
  type DialogueTurn,
  type ExpressionDialogueGradeResult,
  type ExpressionDialogueTask,
} from "@/lib/api/expression"
import { AiSettingsPanel } from "@/components/ai/ai-settings-panel"
import { useSpeechRecorder } from "@/lib/exam/use-speech-recorder"
import { RecordingFeedback } from "@/components/speech/recording-feedback"
import { useAiConfig } from "@/lib/exam/use-ai-config"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"

function roleLabel(role: DialogueRole) {
  return role === "student_a" ? "Student A" : "Student B"
}

function oppositeRole(role: DialogueRole): DialogueRole {
  return role === "student_a" ? "student_b" : "student_a"
}

export function ExpressionDialoguePractice() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const taskId = Number(searchParams.get("task"))
  const validTaskId = Number.isFinite(taskId) && taskId > 0
  const config = useAiConfig()
  const chatEndRef = useRef<HTMLDivElement | null>(null)

  const [task, setTask] = useState<ExpressionDialogueTask | null>(null)
  const [learnerRole, setLearnerRole] = useState<DialogueRole | null>(null)
  const [turns, setTurns] = useState<DialogueTurn[]>([])
  const [input, setInput] = useState("")
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const speech = useSpeechRecorder({
    modelId: config.currentAsr?.id || config.selectedAsrId,
    onText: (text) => setInput(text),
    onError: setError,
  })
  const { recording, seconds: recordSeconds } = speech
  const [aiThinking, setAiThinking] = useState(false)
  const [totalSeconds, setTotalSeconds] = useState(0)
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [grading, setGrading] = useState(false)
  const [grade, setGrade] = useState<ExpressionDialogueGradeResult | null>(null)

  const learnerTurns = useMemo(
    () => learnerRole ? turns.filter((turn) => turn.role === learnerRole).length : 0,
    [learnerRole, turns],
  )
  const canFinish = Boolean(task && learnerRole && learnerTurns >= task.configJson.minLearnerTurns && !aiThinking)

  useEffect(() => {
    if (!validTaskId) return
    let active = true
    fetchExpressionDialogueTask(taskId)
      .then((value) => active && setTask(value))
      .catch((e: unknown) => active && setError(e instanceof Error ? e.message : "互动题加载失败"))
      .finally(() => active && setLoading(false))
    return () => {
      active = false
      stopTts()
    }
  }, [taskId, validTaskId])

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth", block: "nearest" })
  }, [turns, aiThinking])

  useEffect(() => {
    if (!learnerRole || grade) return
    const timer = window.setInterval(() => setTotalSeconds((value) => value + 1), 1000)
    return () => window.clearInterval(timer)
  }, [grade, learnerRole])

  async function speak(text: string) {
    if (!config.ttsEnabled || !text.trim()) return
    unlockAudio()
    try {
      if (config.ttsEngine === "system") await speakWithSystem(text, config.selectedVoice)
      else await speakWithServer(text, config.ttsEngine, config.selectedVoice, {
        onLoadingChange: () => undefined,
        onSpeakingChange: () => undefined,
      })
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "语音播放失败")
    }
  }

  async function askPartner(role: DialogueRole, transcript: DialogueTurn[]) {
    if (!task) return
    setAiThinking(true)
    setError("")
    try {
      const text = await requestDialoguePartnerTurn({
        task,
        learnerRole: role,
        transcript,
        llm: config.currentLlm,
        useProxy: config.llmProxy,
      })
      const nextTurn: DialogueTurn = { role: oppositeRole(role), text }
      setTurns((current) => [...current, nextTurn])
      await speak(text)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "AI 回复失败")
    } finally {
      setAiThinking(false)
    }
  }

  async function chooseRole(role: DialogueRole) {
    speech.reset()
    setLearnerRole(role)
    setTurns([])
    setInput("")
    setGrade(null)
    setError("")
    setTotalSeconds(0)
    if (task?.configJson.starterRole !== role) await askPartner(role, [])
  }

  async function switchRole() {
    if (!learnerRole) return
    if (turns.length > 0 && !window.confirm("切换角色会重新开始当前对话，继续吗？")) return
    await chooseRole(oppositeRole(learnerRole))
  }

  async function sendTurn() {
    if (!task || !learnerRole || !input.trim() || aiThinking || grade) return
    speech.reset()
    const learnerTurn: DialogueTurn = { role: learnerRole, text: input.trim() }
    const next = [...turns, learnerTurn]
    setTurns(next)
    setInput("")
    if (next.length < task.configJson.maxTotalTurns) await askPartner(learnerRole, next)
  }

  const toggleRecording = speech.toggle

  async function finish() {
    if (!task || !learnerRole || !canFinish) return
    setGrading(true)
    setError("")
    try {
      const result = await gradeExpressionDialogue({
        task,
        learnerRole,
        transcript: turns,
        llm: config.currentLlm,
        useProxy: config.llmProxy,
      })
      setGrade(result)
      try {
        await saveExpressionDialogueAttempt(task.id, {
          selectedRole: learnerRole,
          transcript: turns,
          score: result.score,
          feedback: result,
          durationSeconds: totalSeconds,
        })
      } catch {
        setError("反馈已生成，但本次互动记录保存失败")
      }
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "互动评分失败")
    } finally {
      setGrading(false)
    }
  }

  if (!validTaskId) return <main className="expression-shell"><div className="expression-state expression-error"><span>互动题编号无效</span><button type="button" onClick={() => router.push("/expression/dialogue")}>返回题库</button></div></main>
  if (loading) return <main className="expression-shell"><div className="expression-state"><Loader2 className="size-6 animate-spin" />正在加载互动题</div></main>
  if (!task) return <main className="expression-shell"><div className="expression-state expression-error"><span>{error || "互动题不存在"}</span><button type="button" onClick={() => router.push("/expression/dialogue")}>返回题库</button></div></main>

  return (
    <main className="expression-shell expression-dialogue-practice">
      <header className="expression-header">
        <button type="button" className="expression-icon-btn" onClick={() => router.push("/expression/dialogue")} title="返回互动题库">
          <ArrowLeft className="size-5" />
        </button>
        <div className="expression-progress-copy">
          <h1>{task.titleCn}</h1>
          <p>{learnerRole ? `你是 ${roleLabel(learnerRole)}` : task.titleEn}</p>
        </div>
        <div className="expression-header-actions">
          {learnerRole && <button type="button" className="expression-icon-btn" onClick={switchRole} title="切换 A/B 角色"><RefreshCw className="size-4" /></button>}
          <button type="button" className="expression-icon-btn" onClick={() => setSettingsOpen((open) => !open)} title="练习设置"><Settings className="size-5" /></button>
        </div>
      </header>

      {settingsOpen && <section className="expression-ai-settings"><AiSettingsPanel config={config} /></section>}

      <section className="expression-question expression-dialogue-task">
        <div className="expression-question-label">Part 2 Task</div>
        <h2>{task.promptEn}</h2>
        {task.promptCn && <p>{task.promptCn}</p>}
      </section>

      <section className="expression-dialogue-images" aria-label="题目图片位置">
        {task.configJson.imageSlots.map((slot) => slot.imageUrl ? (
          // eslint-disable-next-line @next/next/no-img-element
          <figure key={slot.key}><img src={slot.imageUrl} alt={slot.label} /><figcaption>{slot.label}</figcaption></figure>
        ) : (
          <figure key={slot.key} className="empty"><ImageOff className="size-5" /><figcaption>{slot.label}</figcaption></figure>
        ))}
      </section>

      {!learnerRole ? (
        <section className="expression-role-picker">
          <div className="expression-role-heading"><Users className="size-5" /><div><strong>选择你的角色</strong><span>AI 自动扮演另一位学生</span></div></div>
          <div className="expression-role-options">
            {(["student_a", "student_b"] as DialogueRole[]).map((role) => (
              <button key={role} type="button" onClick={() => chooseRole(role)}>
                <strong>{roleLabel(role)}</strong>
                <span>{task.configJson.starterRole === role ? "你先开始讨论" : "AI 先开始讨论"}</span>
              </button>
            ))}
          </div>
          <div className="expression-language-bank">
            <span>Useful language</span>
            <div>{task.configJson.targetLanguage.map((text) => <button type="button" key={text} onClick={() => speak(text)}>{text}<Volume2 className="size-3" /></button>)}</div>
          </div>
        </section>
      ) : (
        <>
          <section className="expression-dialogue-chat" aria-live="polite">
            {turns.length === 0 && !aiThinking && <p className="expression-chat-empty">你是 Student A，请选择一个图片选项开始讨论。</p>}
            {turns.map((turn, index) => (
              <div key={`${turn.role}-${index}`} className={`expression-chat-turn ${turn.role === learnerRole ? "mine" : "partner"}`}>
                <span>{roleLabel(turn.role)}</span>
                <div><p>{turn.text}</p><button type="button" onClick={() => speak(turn.text)} title="播放这句话"><Volume2 className="size-3.5" /></button></div>
              </div>
            ))}
            {aiThinking && <div className="expression-chat-thinking"><Loader2 className="size-4 animate-spin" />{roleLabel(oppositeRole(learnerRole))} 正在回应</div>}
            <div ref={chatEndRef} />
          </section>

          {!grade && (
            <section className="expression-dialogue-composer">
              <div className="expression-language-bank compact">
                <div>{task.configJson.targetLanguage.map((text) => <button type="button" key={text} onClick={() => setInput(text)}>{text}</button>)}</div>
              </div>
              <label><span>你的下一句话</span><textarea rows={3} value={input} onChange={(event) => setInput(event.target.value)} placeholder="Respond and ask your partner..." /></label>
              <div className="expression-composer-actions">
                <button type="button" className={`expression-record-small ${recording ? "recording" : ""}`} onClick={toggleRecording} disabled={aiThinking || speech.busy} title={recording ? "停止录音" : "语音输入"}>
                  {recording ? <Square className="size-4" /> : <Mic className="size-5" />}<span>{recording ? `${recordSeconds}s 停止录音` : "开始录音"}</span>
                </button>
                <button type="button" className="expression-primary" onClick={sendTurn} disabled={!input.trim() || aiThinking || recording || speech.busy}><Send className="size-4" />发送</button>
              </div>
              <div className="expression-dialogue-finish">
                <span>已完成 {learnerTurns} / {task.configJson.minLearnerTurns} 次发言</span>
                <button type="button" className="expression-secondary" disabled={!canFinish || grading} onClick={finish}>
                  {grading ? <Loader2 className="size-4 animate-spin" /> : <CheckCircle2 className="size-4" />}{grading ? "正在评分" : "完成并评分"}
                </button>
              </div>
            </section>
          )}

          {grade && <DialogueFeedback result={grade} onRestart={() => chooseRole(learnerRole)} />}
          <RecordingFeedback recorder={speech} />
          {error && <p className="expression-inline-error">{error}</p>}
        </>
      )}
    </main>
  )
}

function DialogueFeedback({ result, onRestart }: { result: ExpressionDialogueGradeResult; onRestart: () => void }) {
  const dimensions = [
    ["互动", result.dimensions.interaction],
    ["任务", result.dimensions.task_achievement],
    ["语言", result.dimensions.language],
    ["流畅", result.dimensions.fluency],
  ] as const
  return <section className="expression-feedback expression-dialogue-feedback">
    <div className="expression-feedback-head"><div className={result.passed ? "passed" : "retry"}>{result.score}</div><div><strong>{result.summary_en}</strong><p>{result.summary_cn}</p></div></div>
    <div className="expression-dimensions">{dimensions.map(([label, value]) => <div key={label}><span>{label}</span><strong>{value}</strong><i><b style={{ width: `${value}%` }} /></i></div>)}</div>
    {result.improvements.length > 0 && <ul>{result.improvements.map((text) => <li key={text}>{text}</li>)}</ul>}
    {result.useful_phrases.length > 0 && <div className="expression-feedback-phrases"><span>下次可以用</span>{result.useful_phrases.map((text) => <b key={text}>{text}</b>)}</div>}
    <button type="button" className="expression-primary" onClick={onRestart}><RefreshCw className="size-4" />使用当前角色再练一次</button>
  </section>
}
