"use client"

import { useCallback, useEffect, useMemo, useRef, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import {
  ArrowLeft,
  ChevronDown,
  ChevronUp,
  Loader2,
  Mic,
  MicOff,
  Pause,
  Play,
  Send,
  Settings,
  Square,
  Volume2,
} from "lucide-react"
import type { ExamMessage, ExamMessageRole, ExamStep, LlmModel } from "@/lib/types/speech"
import type { AvatarState } from "./exam-avatar"
import { useExamConfig } from "@/lib/exam/use-exam-config"
import { buildSteps } from "@/lib/exam/build-steps"
import { callJudge } from "@/lib/exam/judge"
import { AsrRecorder } from "@/lib/exam/asr"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"
import { ExamAvatar } from "./exam-avatar"

const PART_OPTIONS = [
  { value: "all", label: "全流程" },
  { value: "1", label: "Part 1" },
  { value: "2", label: "Part 2" },
  { value: "3", label: "Part 3" },
  { value: "4", label: "Part 4" },
]

let msgIdCounter = 0

export function ExamPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const levelCode = searchParams.get("levelCode") || ""
  const levelName = searchParams.get("levelName") || "Exam"
  const seriesCode = searchParams.get("seriesCode") || ""
  const seriesName = searchParams.get("seriesName") || ""

  const query = useMemo(() => ({
    levelCode: levelCode || undefined,
    seriesCode: seriesCode || undefined,
  }), [levelCode, seriesCode])

  const config = useExamConfig(query)

  const [selectedTest, setSelectedTest] = useState("")
  const [selectedPart, setSelectedPart] = useState("all")
  const [settingsOpen, setSettingsOpen] = useState(true)

  const [examStatus, setExamStatus] = useState("未开始")
  const [examStatusType, setExamStatusType] = useState<"default" | "success" | "error">("default")
  const [messages, setMessages] = useState<ExamMessage[]>([])
  const [inputText, setInputText] = useState("")
  const [inputVisible, setInputVisible] = useState(false)
  const [sendDisabled, setSendDisabled] = useState(true)
  const [recordDisabled, setRecordDisabled] = useState(true)
  const [startDisabled, setStartDisabled] = useState(false)
  const [recording, setRecording] = useState(false)
  const [ttsLoading, setTtsLoading] = useState(false)
  const [ttsSpeaking, setTtsSpeaking] = useState(false)
  const [avatarState, setAvatarState] = useState<AvatarState>("idle")
  const [avatarLabel, setAvatarLabel] = useState("")
  const [testMode, setTestMode] = useState(false)

  const chatRef = useRef<HTMLDivElement>(null)
  const asrRef = useRef(new AsrRecorder())

  const stepsRef = useRef<ExamStep[]>([])
  const stepIdxRef = useRef(0)
  const retriesRef = useRef(0)
  const waitingRef = useRef(false)
  const requireRepeatRef = useRef<string | null>(null)
  const scoreRef = useRef({ correct: 0, retry: 0, showAnswer: 0 })

  const testOptions = useMemo(() => {
    if (!config.questionBank) return []
    return Object.entries(config.questionBank.tests).map(([id, test]) => ({
      value: id,
      label: test.label || id,
    }))
  }, [config.questionBank])

  useEffect(() => {
    if (testOptions.length > 0 && !selectedTest) {
      setSelectedTest(testOptions[0].value)
    }
  }, [testOptions, selectedTest])

  const examProgress = useMemo(() => {
    const step = stepsRef.current[stepIdxRef.current] as Record<string, unknown> | undefined
    if (!step) return ""
    const isQ = (s: Record<string, unknown>) => s.type === "judge" || s.type === "judge-question"
    const total = stepsRef.current.filter((s) => isQ(s as Record<string, unknown>)).length
    const done = stepsRef.current.slice(0, stepIdxRef.current).filter((s) => isQ(s as Record<string, unknown>)).length
    return `${(step.label as string) || ""}  (${done}/${total})`
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [examStatus])

  function addMsg(role: ExamMessageRole, text: string, replay?: string) {
    setMessages((prev) => [...prev, { id: ++msgIdCounter, role, text, replay }])
    setTimeout(() => {
      chatRef.current?.scrollTo({ top: chatRef.current.scrollHeight, behavior: "smooth" })
    }, 50)
  }

  function setStatus(text: string, type: "default" | "success" | "error" = "default") {
    setExamStatus(text)
    setExamStatusType(type)
  }

  const speakText = useCallback(async (text: string) => {
    if (!config.ttsEnabled) return
    unlockAudio()
    setAvatarState("talking")
    setAvatarLabel("")
    try {
      if (config.ttsEngine === "system") {
        await speakWithSystem(text, config.selectedVoice)
      } else {
        await speakWithServer(text, config.ttsEngine, config.selectedVoice, {
          onLoadingChange: setTtsLoading,
          onSpeakingChange: setTtsSpeaking,
        })
      }
    } catch {}
    setAvatarState("idle")
  }, [config.ttsEnabled, config.ttsEngine, config.selectedVoice])

  const safeTtsSpeak = useCallback(async (text: string) => {
    if (!config.ttsEnabled || !text) return
    try {
      await Promise.race([speakText(text), new Promise((r) => setTimeout(r, 5000))])
    } catch {}
  }, [config.ttsEnabled, speakText])

  const runExamStep = useCallback(async () => {
    if (stepIdxRef.current >= stepsRef.current.length) return
    const step = stepsRef.current[stepIdxRef.current] as Record<string, unknown>

    if (step.type === "show-image") {
      setCurrentImages(step.images as string[])
      stepIdxRef.current++
      await runExamStep()
    } else if (step.type === "speak") {
      addMsg("examiner", step.text as string, step.text as string)
      await speakText(step.text as string)
      stepIdxRef.current++
      await runExamStep()
    } else if (step.type === "show-hint") {
      addMsg("hint", `Your turn to ask: ${step.hint}`, step.hint as string)
      stepIdxRef.current++
      waitingRef.current = true
      retriesRef.current = 0
      setSendDisabled(false)
      setRecordDisabled(false)
      setInputText("")
      setAvatarState("listening")
      setAvatarLabel("用提示组成一个问题")
      setStatus("用提示组成一个问题", "success")
    } else if (step.type === "judge" || step.type === "judge-question") {
      if (step.hint_text) {
        addMsg("examiner", step.hint_text as string, step.hint_text as string)
        await speakText(step.hint_text as string)
      }
      waitingRef.current = true
      retriesRef.current = 0
      setSendDisabled(false)
      setRecordDisabled(false)
      setInputText("")
      setAvatarState("listening")
      setAvatarLabel("轮到你回答了")
      setStatus("轮到你回答了", "success")
    } else if (step.type === "end") {
      const s = scoreRef.current
      addMsg("system", `考试结束 ✅ ${s.correct} 正确 | 🔄 ${s.retry} 重试 | 📖 ${s.showAnswer} 看答案`)
      setAvatarState("idle")
      setAvatarLabel("考试结束")
      setStatus("已完成", "success")
      stepIdxRef.current = stepsRef.current.length
      setStartDisabled(false)
      setSendDisabled(true)
      setRecordDisabled(true)
    }
  }, [speakText])

  const submitAnswer = useCallback(async (text: string) => {
    if (!waitingRef.current) return
    waitingRef.current = false
    setSendDisabled(true)
    setRecordDisabled(true)
    setAvatarState("idle")
    setAvatarLabel("判分中…")
    addMsg("student", text)

    try {
      if (requireRepeatRef.current) {
        setStatus("Checking...")
        const norm = (s: string) => s.toLowerCase().replace(/[^a-z0-9\s]/g, "").replace(/\s+/g, " ").trim()
        const nStudent = norm(text)
        const nExpected = norm(requireRepeatRef.current)
        const expWords = nExpected.split(" ").filter((w) => w.length > 2)
        const matched = expWords.filter((w) => nStudent.includes(w)).length
        const ratio = expWords.length > 0 ? matched / expWords.length : 0
        if (nStudent === nExpected || ratio >= 0.7) {
          addMsg("judge-ok", "✅ Good. Let's continue.")
          requireRepeatRef.current = null
          retriesRef.current = 0
          stepIdxRef.current++
          setTimeout(() => runExamStep(), 800)
        } else {
          addMsg("judge-fail", `❌ Please type: ${requireRepeatRef.current}`)
          waitingRef.current = true
          setSendDisabled(false)
          setRecordDisabled(false)
          setStatus("请输入正确答案以继续", "error")
        }
        return
      }

      setStatus("判分中...")
      const step = stepsRef.current[stepIdxRef.current] as Record<string, unknown>

      // Test mode: skip LLM, auto-correct
      if (testMode) {
        addMsg("judge-ok", "✅ Correct! (test)")
        scoreRef.current.correct++
        retriesRef.current = 0
        stepIdxRef.current++
        setTimeout(() => runExamStep(), 300)
        return
      }

      const isQ = step.type === "judge-question"
      const question = isQ ? `Form a question using hint: ${step.hint}` : (step.question as string)
      const expected = isQ ? (step.expected_question as string) : ((step.expected as string) || "")
      const sample = (step.sample as string) || ""
      const judgeExpected = expected || (sample ? `(open-ended, sample: ${sample})` : "")
      const result = await callJudge(question, judgeExpected, text, config.currentLlm, config.llmProxy)

      if (result.ok) {
        addMsg("judge-ok", "✅ Correct!")
        await safeTtsSpeak("Correct!")
        scoreRef.current.correct++
        retriesRef.current = 0
        stepIdxRef.current++
        setTimeout(() => runExamStep(), 300)
        return
      }

      if (result.timeout) {
        addMsg("system", "⏳ 判题超时，自动跳过")
        if (expected) addMsg("hint", `📖 Expected: ${expected}`, expected)
        else if (sample) addMsg("hint", `💡 Sample: ${sample}`, sample)
        retriesRef.current = 0
        stepIdxRef.current++
        setTimeout(() => runExamStep(), 800)
        return
      }

      retriesRef.current++
      const fbText = result.fb || "Not quite."
      addMsg("judge-fail", `❌ ${fbText}`, fbText)
      await safeTtsSpeak("Not quite.")
      if (result.cn) addMsg("hint", `🇨🇳 ${result.cn}`)
      const correctText = result.ans || expected || sample || ""
      if (correctText) await safeTtsSpeak(correctText)

      const openEnded = !!(step.open_ended)
      if (openEnded) {
        if (retriesRef.current >= 3) {
          if (sample) addMsg("hint", `💡 Sample: ${sample}`, sample)
          addMsg("system", "Let's move on.")
          scoreRef.current.showAnswer++
          retriesRef.current = 0
          stepIdxRef.current++
          setTimeout(() => runExamStep(), 1000)
        } else {
          if (sample) addMsg("hint", `💡 Sample: ${sample}`, sample)
          addMsg("hint", "Try again.")
          scoreRef.current.retry++
          waitingRef.current = true
          setSendDisabled(false)
          setRecordDisabled(false)
          setStatus("请再试一次", "error")
        }
      } else {
        const ans = expected || ""
        if (retriesRef.current >= 3) {
          if (ans) {
            addMsg("hint", `📖 Answer: ${ans}`, ans)
            addMsg("hint", "👉 Please type the correct answer to continue.")
            scoreRef.current.showAnswer++
            requireRepeatRef.current = ans
            setStatus("请输入正确答案以继续", "success")
          } else {
            addMsg("system", "Let's move on.")
            retriesRef.current = 0
            stepIdxRef.current++
            setTimeout(() => runExamStep(), 800)
            return
          }
        } else {
          if (ans) addMsg("hint", `💡 Hint: ${ans}`, ans)
          addMsg("hint", "Try again.")
          scoreRef.current.retry++
          setStatus("请再试一次", "error")
        }
        waitingRef.current = true
        setSendDisabled(false)
        setRecordDisabled(false)
      }
    } catch (err: unknown) {
      addMsg("system", `Error: ${err instanceof Error ? err.message : err} — try again.`)
      waitingRef.current = true
      setSendDisabled(false)
      setRecordDisabled(false)
      setStatus("出错了，请重试", "error")
    }
  }, [config.currentLlm, config.llmProxy, runExamStep, safeTtsSpeak, testMode])

  function startExam() {
    if (!selectedTest || !config.questionBank) return
    setMessages([])
    setCurrentImages([])
    const rand = () => Math.random().toString(36).slice(2, 8)
    setAvatarSeeds({ examiner: `ex-${rand()}`, student: `st-${rand()}` })
    const steps = buildSteps(config.questionBank, selectedTest, selectedPart)
    if (!steps.length) return
    stepsRef.current = steps
    stepIdxRef.current = 0
    retriesRef.current = 0
    requireRepeatRef.current = null
    scoreRef.current = { correct: 0, retry: 0, showAnswer: 0 }
    setStartDisabled(true)
    setInputVisible(true)
    setSendDisabled(true)
    setRecordDisabled(true)
    setSettingsOpen(false)
    setStatus("考试中", "success")
    runExamStep()
  }

  function sendAnswer() {
    const text = inputText.trim()
    if (!text) return
    submitAnswer(text)
    setInputText("")
  }

  async function toggleRecording() {
    const asr = asrRef.current
    if (recording) {
      setRecording(false)
      setRecordDisabled(true)
      setStatus("识别中...")
      const result = await asr.stopAndRecognize(config.currentAsr?.id || "sensevoice-small")
      if ("error" in result) {
        setStatus(result.error, "error")
        if (waitingRef.current) setRecordDisabled(false)
      } else {
        if (config.voiceOnly) {
          submitAnswer(result.text)
        } else {
          setInputText(result.text)
          setStatus("识别完成，可编辑后发送", "success")
          setSendDisabled(false)
          if (waitingRef.current) setRecordDisabled(false)
        }
      }
    } else {
      unlockAudio()
      setRecording(true)
      const ok = await asr.startRecording((sec) => setStatus(`录音中 ${sec}s`, "success"))
      if (!ok) { setRecording(false); setStatus("麦克风未授权", "error") }
    }
  }

  useEffect(() => {
    return () => {
      asrRef.current.release()
      stopTts()
    }
  }, [])

  const statusColor = examStatusType === "success" ? "text-emerald-600 bg-emerald-50 border-emerald-200"
    : examStatusType === "error" ? "text-red-600 bg-red-50 border-red-200"
    : "text-blue-600 bg-blue-50 border-blue-200"

  const [currentImages, setCurrentImages] = useState<string[]>([])
  const [avatarSeeds, setAvatarSeeds] = useState({ examiner: "ex-0", student: "st-0" })

  const [replayBusyId, setReplayBusyId] = useState<number | null>(null)
  async function replayMessage(msg: ExamMessage) {
    const target = msg.replay || msg.text
    if (!target || replayBusyId !== null) return
    setReplayBusyId(msg.id)
    try { await speakText(target) } catch {}
    setReplayBusyId(null)
  }

  return (
    <div className="exam-shell">
      {/* Header */}
      <header className="exam-header">
        <button type="button" className="yle-back" onClick={() => router.push("/speech/yle")}>
          <ArrowLeft className="size-[18px]" />
          <span>返回</span>
        </button>
        <div className="exam-header-center">
          <h1 className="exam-header-title">{seriesName || levelName}</h1>
          {examProgress && <span className="exam-header-progress">{examProgress}</span>}
        </div>
        <button type="button" className="home-icon-btn" onClick={() => setSettingsOpen((v) => !v)}>
          <Settings className="size-[18px]" />
        </button>
      </header>

      <div className="exam-body">
        {/* Left Panel */}
        <aside className={`exam-left ${settingsOpen ? "exam-left-open" : "exam-left-closed"}`}>
          {/* Settings Toggle (mobile) */}
          <button type="button" className="exam-settings-toggle" onClick={() => setSettingsOpen((v) => !v)}>
            <Settings className="size-4" />
            <span>设置</span>
            {settingsOpen ? <ChevronUp className="size-4 ml-auto" /> : <ChevronDown className="size-4 ml-auto" />}
          </button>

          {settingsOpen && (
            <div className="exam-settings">
              {/* Test & Part */}
              <div className="exam-setting-group">
                <label className="exam-setting-label">试卷</label>
                <select className="exam-select" value={selectedTest} onChange={(e) => setSelectedTest(e.target.value)}>
                  {testOptions.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
                </select>
              </div>
              <div className="exam-setting-group">
                <label className="exam-setting-label">起始部分</label>
                <select className="exam-select" value={selectedPart} onChange={(e) => setSelectedPart(e.target.value)}>
                  {PART_OPTIONS.map((p) => <option key={p.value} value={p.value}>{p.label}</option>)}
                </select>
              </div>

              {/* Toggles */}
              <div className="exam-setting-row">
                <span className="exam-setting-label">仅语音模式</span>
                <button type="button" className={`exam-toggle ${config.voiceOnly ? "exam-toggle-on" : ""}`} onClick={() => config.setVoiceOnly(!config.voiceOnly)}>
                  <span className="exam-toggle-thumb" />
                </button>
              </div>
              <div className="exam-setting-row">
                <span className="exam-setting-label">隐藏对话</span>
                <button type="button" className={`exam-toggle ${config.hideChat ? "exam-toggle-on" : ""}`} onClick={() => config.setHideChat(!config.hideChat)}>
                  <span className="exam-toggle-thumb" />
                </button>
              </div>
              <div className="exam-setting-row">
                <span className="exam-setting-label">测试模式</span>
                <button type="button" className={`exam-toggle ${testMode ? "exam-toggle-on" : ""}`} onClick={() => setTestMode(!testMode)}>
                  <span className="exam-toggle-thumb" />
                </button>
              </div>

              {/* LLM */}
              <div className="exam-setting-group">
                <label className="exam-setting-label">LLM</label>
                <select className="exam-select" value={config.selectedLlmKey} onChange={(e) => config.setSelectedLlmKey(e.target.value)}>
                  {config.llmModels.map((m, i) => <option key={`${m.provider}:${m.model}:${i}`} value={`${m.provider}:${m.model}`}>{m.label}</option>)}
                </select>
              </div>
              <div className="exam-setting-row">
                <span className="exam-setting-label">代理</span>
                <button type="button" className={`exam-toggle ${config.llmProxy ? "exam-toggle-on" : ""}`} onClick={() => config.setLlmProxy(!config.llmProxy)}>
                  <span className="exam-toggle-thumb" />
                </button>
              </div>

              {/* ASR */}
              <div className="exam-setting-group">
                <label className="exam-setting-label">ASR</label>
                <select className="exam-select" value={config.selectedAsrId} onChange={(e) => config.setSelectedAsrId(e.target.value)}>
                  {config.asrModels.map((m) => <option key={m.id} value={m.id}>{m.label}</option>)}
                </select>
              </div>

              {/* TTS */}
              <div className="exam-setting-row">
                <span className="exam-setting-label">语音播报</span>
                <button type="button" className={`exam-toggle ${config.ttsEnabled ? "exam-toggle-on" : ""}`} onClick={() => config.setTtsEnabled(!config.ttsEnabled)}>
                  <span className="exam-toggle-thumb" />
                </button>
              </div>
              <div className="exam-setting-group">
                <label className="exam-setting-label">播报引擎</label>
                <select className="exam-select" value={config.ttsEngine} onChange={(e) => config.setTtsEngine(e.target.value)}>
                  {config.ttsEngines.map((e) => <option key={e.id} value={e.id}>{e.label}</option>)}
                </select>
              </div>
              <div className="exam-setting-group">
                <label className="exam-setting-label">音色</label>
                <select className="exam-select" value={config.selectedVoice} onChange={(e) => config.setSelectedVoice(e.target.value)}>
                  {config.voiceOptions.map((v) => <option key={v.name} value={v.name}>{v.label || v.name}</option>)}
                </select>
              </div>
            </div>
          )}

          {/* Start Button */}
          <div className="exam-start-area">
            <div className={`exam-status-badge ${statusColor}`}>
              {examStatus}
            </div>
            <div className="exam-action-row">
              <button type="button" className="exam-start-btn" disabled={startDisabled || !selectedTest} onClick={startExam}>
                {startDisabled ? <Loader2 className="size-4 animate-spin" /> : <Play className="size-4" />}
                开始考试
              </button>
              {ttsSpeaking && (
                <button type="button" className="exam-stop-tts" onClick={stopTts}>
                  <Square className="size-3.5" /> 停止播报
                </button>
              )}
              {ttsLoading && <Loader2 className="size-4 animate-spin text-blue-400" />}
            </div>
          </div>
        </aside>

        {/* Right Panel - Chat / Avatar */}
        <main className="exam-right">
          {currentImages.length > 0 && (
            <div className={`exam-images ${currentImages.length === 1 ? "exam-images-single" : "exam-images-pair"}`}>
              {currentImages.map((src, i) => (
                <img key={`${src}-${i}`} src={src} alt={`Exam image ${i + 1}`} className="exam-image" />
              ))}
            </div>
          )}

          {config.hideChat ? (
            /* Avatar mode — animated character instead of chat */
            <div className="exam-avatar-container">
              <ExamAvatar state={avatarState} label={avatarLabel} />
              {examProgress && <p className="exam-avatar-progress">{examProgress}</p>}
            </div>
          ) : (
            /* Chat mode — normal message list */
            <div className="exam-chat" ref={chatRef}>
              {messages.length === 0 ? (
                <div className="exam-chat-empty">
                  <Volume2 className="size-8 text-slate-200" />
                  <p>选择试卷后点击"开始考试"</p>
                  <p className="text-xs text-slate-300">对话记录将显示在这里</p>
                </div>
              ) : (
                messages.map((msg) => {
                  const hasAvatar = msg.role === "examiner" || msg.role === "student"
                  const avatarSeed = msg.role === "examiner" ? avatarSeeds.examiner : avatarSeeds.student
                  return (
                    <div key={msg.id} className={`exam-msg exam-msg-${msg.role}`}>
                      {hasAvatar && (
                        <img
                          src={`https://api.dicebear.com/9.x/fun-emoji/svg?seed=${avatarSeed}`}
                          alt=""
                          className="exam-avatar"
                        />
                      )}
                      <div className="exam-msg-body">
                        <span className="exam-msg-text">{msg.text}</span>
                        {msg.replay && (
                          <button
                            type="button"
                            className="exam-replay-btn"
                            disabled={replayBusyId !== null && replayBusyId !== msg.id}
                            onClick={() => replayMessage(msg)}
                          >
                            {replayBusyId === msg.id ? <Loader2 className="size-3 animate-spin" /> : <Volume2 className="size-3" />}
                          </button>
                        )}
                      </div>
                    </div>
                  )
                })
              )}
            </div>
          )}

          {/* Input Bar */}
          {inputVisible && (
            <div className="exam-input-bar">
              <input
                className="exam-input-field"
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                placeholder="输入或点击麦克风回答..."
                onKeyDown={(e) => { if (e.key === "Enter" && !sendDisabled) sendAnswer() }}
                disabled={sendDisabled && !waitingRef.current}
              />
              <button
                type="button"
                className={`exam-mic-btn ${recording ? "exam-mic-recording" : ""}`}
                disabled={recordDisabled && !recording}
                onClick={toggleRecording}
              >
                {recording ? <MicOff className="size-[18px]" /> : <Mic className="size-[18px]" />}
              </button>
              <button
                type="button"
                className="exam-send-btn"
                disabled={sendDisabled || !inputText.trim()}
                onClick={sendAnswer}
              >
                <Send className="size-[18px]" />
              </button>
            </div>
          )}
        </main>
      </div>
    </div>
  )
}
