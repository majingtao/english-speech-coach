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
  Play,
  Send,
  Settings,
  Square,
  Volume2,
} from "lucide-react"
import type {
  CandidateSeat,
  ExamMessage,
  ExamMessageRole,
  ExamStep,
  KetOption,
  PartnerInfo,
} from "@/lib/types/speech"
import type { AvatarState } from "./exam-avatar"
import { useExamConfig } from "@/lib/exam/use-exam-config"
import { buildSteps } from "@/lib/exam/build-steps"
import { callJudge } from "@/lib/exam/judge"
import { AsrRecorder } from "@/lib/exam/asr"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"
import { ExamAvatar } from "./exam-avatar"
import { AiSettingsPanel } from "@/components/ai/ai-settings-panel"

const ALL_PART_OPTIONS = [
  { value: "all", label: "全流程" },
  { value: "1", label: "Part 1" },
  { value: "2", label: "Part 2" },
  { value: "3", label: "Part 3" },
  { value: "4", label: "Part 4" },
]

let msgIdCounter = 0

const MIC_HINT_STORAGE_KEY = "exam-mic-hint-seen"

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
  const [userSeat, setUserSeat] = useState<CandidateSeat>("A")
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
  const [showMicHint, setShowMicHint] = useState(false)
  const [ttsLoading, setTtsLoading] = useState(false)
  const [ttsSpeaking, setTtsSpeaking] = useState(false)
  const [avatarState, setAvatarState] = useState<AvatarState>("idle")
  const [avatarLabel, setAvatarLabel] = useState("")
  const [activeRole, setActiveRole] = useState<"examiner" | "candidateA" | "candidateB" | null>(null)
  const [testMode, setTestMode] = useState(false)
  const [zoomedImage, setZoomedImage] = useState<string | null>(null)
  const [currentImages, setCurrentImages] = useState<string[]>([])
  const [currentOptions, setCurrentOptions] = useState<KetOption[]>([])
  const [avatarSeeds, setAvatarSeeds] = useState({
    examiner: "ex-0",
    student: "st-0",
    partner: "pt-0",
    candidateA: "ca-0",
    candidateB: "cb-0",
  })

  const chatRef = useRef<HTMLDivElement>(null)
  const asrRef = useRef(new AsrRecorder())
  const micHintTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)

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
    return () => {
      if (micHintTimerRef.current) clearTimeout(micHintTimerRef.current)
    }
  }, [])

  useEffect(() => {
    if (testOptions.length > 0 && !selectedTest) {
      const firstTestId = testOptions[0].value
      setSelectedTest(firstTestId)
      const firstTest = config.questionBank?.tests[firstTestId]
      if (firstTest?.format === "ket") {
        setUserSeat(firstTest.defaultUserSeat === "B" ? "B" : "A")
      }
    }
  }, [testOptions, selectedTest, config.questionBank])

  const currentTest = useMemo(() => {
    if (!config.questionBank || !selectedTest) return undefined
    return config.questionBank.tests[selectedTest] as Record<string, unknown> | undefined
  }, [config.questionBank, selectedTest])

  const isKet = levelCode === "ket" || currentTest?.format === "ket"

  const partnerInfo = useMemo<PartnerInfo | undefined>(() => {
    if (!currentTest) return undefined
    if (isKet && currentTest.candidateProfiles) {
      const profiles = currentTest.candidateProfiles as Partial<Record<CandidateSeat, PartnerInfo>>
      return profiles[userSeat === "A" ? "B" : "A"] || currentTest.virtualCandidate as PartnerInfo | undefined
    }
    return (currentTest.virtualCandidate || currentTest.partner) as PartnerInfo | undefined
  }, [currentTest, isKet, userSeat])

  const partOptions = useMemo(() => {
    if (!currentTest) return ALL_PART_OPTIONS
    const hasPart = (n: string) => !!currentTest[`part${n}`]
    const available = ["1", "2", "3", "4"].filter(hasPart)
    return [
      { value: "all", label: "全流程" },
      ...available.map((n) => ({ value: n, label: `Part ${n}` })),
    ]
  }, [currentTest])

  useEffect(() => {
    if (!partOptions.some((p) => p.value === selectedPart)) {
      setSelectedPart("all")
    }
  }, [partOptions, selectedPart])

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
    if (!isKet) {
      setTimeout(() => {
        chatRef.current?.scrollTo({ top: chatRef.current.scrollHeight, behavior: "smooth" })
      }, 50)
    }
  }

  function setStatus(text: string, type: "default" | "success" | "error" = "default") {
    setExamStatus(text)
    setExamStatusType(type)
  }

  const speakText = useCallback(async (text: string, voiceOverride?: string) => {
    if (!config.ttsEnabled) return
    unlockAudio()
    setAvatarState("talking")
    setAvatarLabel("")
    const voice = voiceOverride || config.selectedVoice
    try {
      if (config.ttsEngine === "system") {
        await speakWithSystem(text, voice)
      } else {
        await speakWithServer(text, config.ttsEngine, voice, {
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
      setCurrentOptions([])
      stepIdxRef.current++
      await runExamStep()
    } else if (step.type === "show-ket-material") {
      setCurrentImages(step.image ? [step.image as string] : [])
      setCurrentOptions((step.options as KetOption[]) || [])
      stepIdxRef.current++
      await runExamStep()
    } else if (step.type === "speak") {
      setActiveRole("examiner")
      addMsg("examiner", step.text as string, step.text as string)
      await speakText(step.text as string)
      stepIdxRef.current++
      await runExamStep()
    } else if (step.type === "candidate-speak") {
      const seat = step.seat as CandidateSeat
      const role = seat === "A" ? "candidateA" : "candidateB"
      setActiveRole(role)
      addMsg(role, step.text as string, step.text as string)
      await speakText(step.text as string, partnerInfo?.voice)
      stepIdxRef.current++
      await runExamStep()
    } else if (step.type === "partner-speak") {
      addMsg("partner", step.text as string, step.text as string)
      await speakText(step.text as string, partnerInfo?.voice)
      stepIdxRef.current++
      await runExamStep()
    } else if (step.type === "partner-turn") {
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
      setActiveRole(isKet ? (userSeat === "A" ? "candidateA" : "candidateB") : null)
      setStatus("轮到你回答了", "success")
    } else if (step.type === "end") {
      const s = scoreRef.current
      addMsg("system", `考试结束 ✅ ${s.correct} 正确 | 🔄 ${s.retry} 重试 | 📖 ${s.showAnswer} 看答案`)
      setAvatarState("idle")
      setAvatarLabel("考试结束")
      setActiveRole(null)
      setStatus("已完成", "success")
      stepIdxRef.current = stepsRef.current.length
      setStartDisabled(false)
      setSendDisabled(true)
      setRecordDisabled(true)
    }
  }, [speakText, partnerInfo, isKet, userSeat])

  const submitAnswer = useCallback(async (text: string) => {
    if (!waitingRef.current) return
    waitingRef.current = false
    setSendDisabled(true)
    setRecordDisabled(true)
    setAvatarState("idle")
    setAvatarLabel("判分中…")
    addMsg(isKet ? (userSeat === "A" ? "candidateA" : "candidateB") : "student", text)

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
      const format = (currentTest?.format as string | undefined) || "flyers"
      const kind = (step.kind as string | undefined) || undefined
      const result = await callJudge(question, judgeExpected, text, config.currentLlm, config.llmProxy, {
        format,
        kind,
        sample,
      })

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

      const aiAns = result.ans || ""
      if (aiAns) addMsg("hint", `🗣️ AI 示范: ${aiAns}`, aiAns)
      const refText = sample || expected || ""
      if (refText && refText !== aiAns) {
        const label = sample ? "💡 Sample" : "📖 Answer"
        addMsg("hint", `${label}: ${refText}`, refText)
      }
      const toSpeak = aiAns || refText
      if (toSpeak) await safeTtsSpeak(toSpeak)

      const openEnded = !!(step.open_ended)
      if (openEnded) {
        if (retriesRef.current >= 3) {
          addMsg("system", "Let's move on.")
          scoreRef.current.showAnswer++
          retriesRef.current = 0
          stepIdxRef.current++
          setTimeout(() => runExamStep(), 1000)
        } else {
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
  }, [config.currentLlm, config.llmProxy, runExamStep, safeTtsSpeak, testMode, currentTest, isKet, userSeat])

  function startExam() {
    if (!selectedTest || !config.questionBank) return
    setMessages([])
    setCurrentImages([])
    setCurrentOptions([])
    const rand = () => Math.random().toString(36).slice(2, 8)
    const studentSeed = `st-${rand()}`
    const partnerSeed = partnerInfo?.avatarSeed || `pt-${rand()}`
    setAvatarSeeds({
      examiner: `ex-${rand()}`,
      student: studentSeed,
      partner: partnerSeed,
      candidateA: userSeat === "A" ? studentSeed : partnerSeed,
      candidateB: userSeat === "B" ? studentSeed : partnerSeed,
    })
    const steps = buildSteps(config.questionBank, selectedTest, selectedPart, { userSeat })
    if (!steps.length) {
      setStatus(isKet ? "KET 题库必须使用 schemaVersion 2" : "试卷没有可用题目", "error")
      return
    }
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
    setActiveRole(null)
    setStatus("考试中", "success")
    runExamStep()
  }

  function sendAnswer() {
    const text = inputText.trim()
    if (!text) return
    submitAnswer(text)
    setInputText("")
  }

  function dismissMicHint() {
    setShowMicHint(false)
    if (micHintTimerRef.current) {
      clearTimeout(micHintTimerRef.current)
      micHintTimerRef.current = null
    }
  }

  async function toggleRecording() {
    const asr = asrRef.current
    if (recording) {
      dismissMicHint()
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
      const ok = await asr.startRecording((sec) => setStatus(`录音中 ${sec}s · 说完请再次点击麦克风`, "success"))
      if (!ok) {
        setRecording(false)
        setStatus(asr.lastError || "麦克风未授权", "error")
      } else if (typeof window !== "undefined" && !window.localStorage.getItem(MIC_HINT_STORAGE_KEY)) {
        window.localStorage.setItem(MIC_HINT_STORAGE_KEY, "1")
        setShowMicHint(true)
        micHintTimerRef.current = setTimeout(() => setShowMicHint(false), 5000)
      }
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

  const [replayBusyId, setReplayBusyId] = useState<number | null>(null)
  async function replayMessage(msg: ExamMessage, voiceOverride?: string) {
    const target = msg.replay || msg.text
    if (!target) return
    if (replayBusyId === msg.id) {
      stopTts()
      setTtsLoading(false)
      setTtsSpeaking(false)
      setAvatarState("idle")
      setReplayBusyId(null)
      return
    }
    if (replayBusyId !== null) return
    setReplayBusyId(msg.id)
    try { await speakText(target, voiceOverride) } catch {}
    setReplayBusyId((current) => current === msg.id ? null : current)
  }

  function replayButtonState(msg: ExamMessage) {
    if (replayBusyId !== msg.id) return "播放"
    if (ttsLoading) return "生成中"
    if (ttsSpeaking) return "停止"
    return "处理中"
  }

  function renderKetRolePanel(role: "examiner" | "candidateA" | "candidateB") {
    const seat = role === "candidateA" ? "A" : role === "candidateB" ? "B" : null
    const isUser = seat === userSeat
    const name = role === "examiner"
      ? "Examiner"
      : `Candidate ${seat} · ${isUser ? "你" : (partnerInfo?.name || "虚拟搭档")}`
    const seed = role === "examiner"
      ? avatarSeeds.examiner
      : role === "candidateA" ? avatarSeeds.candidateA : avatarSeeds.candidateB
    const roleMessages = messages.filter((msg) => msg.role === role).slice(-4)
    const isActive = activeRole === role
    const status = isActive
      ? role === "examiner" ? "正在提问" : isUser ? "轮到你回答" : "正在发言"
      : "等待"
    const replayVoice = seat && !isUser ? partnerInfo?.voice : undefined

    return (
      <section className={`exam-ket-role exam-ket-role-${role} ${isActive ? "exam-ket-role-active" : ""}`}>
        <div className="exam-ket-role-header">
          <img
            src={`https://api.dicebear.com/9.x/fun-emoji/svg?seed=${seed}`}
            alt=""
            className="exam-ket-role-avatar"
          />
          <div className="exam-ket-role-title">
            <strong>{name}</strong>
            <span>{status}</span>
          </div>
          {isActive && <span className="exam-ket-speaking-dot" aria-label={status} />}
        </div>
        <div className="exam-ket-role-history">
          {roleMessages.length === 0 ? (
            <p className="exam-ket-role-empty">等待考试开始</p>
          ) : roleMessages.map((msg, index) => (
            <div key={msg.id} className={`exam-ket-line ${index === roleMessages.length - 1 ? "exam-ket-line-latest" : ""}`}>
              <span>{msg.text}</span>
              {msg.replay && (
                <button
                  type="button"
                  className={`exam-replay-btn ${replayBusyId === msg.id ? "exam-replay-btn-active" : ""}`}
                  disabled={replayBusyId !== null && replayBusyId !== msg.id}
                  onClick={() => replayMessage(msg, replayVoice)}
                  title={replayButtonState(msg)}
                  aria-label={`${replayButtonState(msg)}这条语音`}
                >
                  {replayBusyId === msg.id && ttsLoading ? <Loader2 className="size-3 animate-spin" /> : replayBusyId === msg.id && ttsSpeaking ? <Square className="size-3" /> : <Volume2 className="size-3" />}
                  <span>{replayButtonState(msg)}</span>
                </button>
              )}
            </div>
          ))}
        </div>
      </section>
    )
  }

  const ketFeedback = messages
    .filter((msg) => msg.role === "judge-ok" || msg.role === "judge-fail" || msg.role === "hint" || msg.role === "system")
    .slice(-2)

  return (
    <div className="exam-shell">
      {/* Header */}
      <header className="exam-header">
        <button
          type="button"
          className="yle-back"
          onClick={() => router.push(levelCode === "ket" ? "/speech/ket" : "/speech/yle")}
        >
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
              {config.questionBankError && (
                <div className="reading-state reading-error" style={{ marginBottom: 12 }}>
                  <span>{config.questionBankError}</span>
                  <button type="button" onClick={() => config.reloadQuestionBank()}>重试</button>
                </div>
              )}
              {/* Test & Part */}
              <div className="exam-setting-group">
                <label className="exam-setting-label">试卷</label>
                <select
                  className="exam-select"
                  value={selectedTest}
                  onChange={(e) => {
                    const testId = e.target.value
                    setSelectedTest(testId)
                    const test = config.questionBank?.tests[testId]
                    if (test?.format === "ket") {
                      setUserSeat(test.defaultUserSeat === "B" ? "B" : "A")
                    }
                  }}
                >
                  {testOptions.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
                </select>
              </div>
              {!isKet && (
                <div className="exam-setting-group">
                  <label className="exam-setting-label">起始部分</label>
                  <select className="exam-select" value={selectedPart} onChange={(e) => setSelectedPart(e.target.value)}>
                    {partOptions.map((p) => <option key={p.value} value={p.value}>{p.label}</option>)}
                  </select>
                </div>
              )}
              {isKet && (
                <div className="exam-setting-group">
                  <label className="exam-setting-label">你的考生位置</label>
                  <div className="exam-seat-control" role="group" aria-label="选择考生位置">
                    {(["A", "B"] as CandidateSeat[]).map((seat) => (
                      <button
                        key={seat}
                        type="button"
                        className={`exam-seat-option ${userSeat === seat ? "exam-seat-option-active" : ""}`}
                        disabled={startDisabled}
                        onClick={() => setUserSeat(seat)}
                      >
                        Candidate {seat}
                        <span>{userSeat === seat ? "你" : partnerInfo?.name || "搭档"}</span>
                      </button>
                    ))}
                  </div>
                </div>
              )}

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

              <AiSettingsPanel config={config} />
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
          {!isKet && currentImages.length > 0 && (
            <div className={`exam-images ${currentImages.length === 1 ? "exam-images-single" : "exam-images-pair"}`}>
              {currentImages.map((src, i) => (
                <img
                  key={`${src}-${i}`}
                  src={src}
                  alt={`Exam image ${i + 1}`}
                  className="exam-image exam-image-clickable"
                  onClick={() => setZoomedImage(src)}
                />
              ))}
            </div>
          )}

          {isKet && !config.hideChat ? (
            <div className="exam-ket-room" ref={chatRef}>
              {renderKetRolePanel("examiner")}

              {(currentImages.length > 0 || currentOptions.length > 0) && (
                <section className="exam-ket-material" aria-label="Part 2 讨论材料">
                  {currentImages[0] && (
                    <img
                      src={currentImages[0]}
                      alt="Part 2 discussion material"
                      className="exam-ket-material-image"
                      onClick={() => setZoomedImage(currentImages[0])}
                    />
                  )}
                  {currentOptions.length > 0 && (
                    <div className="exam-ket-options">
                      {currentOptions.map((option, index) => (
                        <div key={option.id} className="exam-ket-option">
                          <span>{index + 1}</span>
                          {option.image && <img src={option.image} alt="" />}
                          <strong>{option.label}</strong>
                        </div>
                      ))}
                    </div>
                  )}
                </section>
              )}

              <div className="exam-ket-candidates">
                {renderKetRolePanel("candidateA")}
                {renderKetRolePanel("candidateB")}
              </div>

              {ketFeedback.length > 0 && (
                <div className="exam-ket-feedback" aria-live="polite">
                  {ketFeedback.map((msg) => (
                    <span key={msg.id} className={`exam-ket-feedback-${msg.role}`}>{msg.text}</span>
                  ))}
                </div>
              )}
            </div>
          ) : config.hideChat ? (
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
                  const hasAvatar = msg.role === "examiner" || msg.role === "student" || msg.role === "partner"
                  const avatarSeed =
                    msg.role === "examiner" ? avatarSeeds.examiner
                    : msg.role === "partner" ? avatarSeeds.partner
                    : avatarSeeds.student
                  const partnerName = msg.role === "partner" ? partnerInfo?.name : null
                  const replayVoice = msg.role === "partner" ? partnerInfo?.voice : undefined
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
                        {partnerName && <span className="exam-msg-sender">{partnerName}</span>}
                        <span className="exam-msg-text">{msg.text}</span>
                        {msg.replay && (
                          <button
                            type="button"
                            className={`exam-replay-btn ${replayBusyId === msg.id ? "exam-replay-btn-active" : ""}`}
                            disabled={replayBusyId !== null && replayBusyId !== msg.id}
                            onClick={() => replayMessage(msg, replayVoice)}
                            title={replayButtonState(msg)}
                            aria-label={`${replayButtonState(msg)}这条语音`}
                          >
                            {replayBusyId === msg.id && ttsLoading ? <Loader2 className="size-3 animate-spin" /> : replayBusyId === msg.id && ttsSpeaking ? <Square className="size-3" /> : <Volume2 className="size-3" />}
                            <span>{replayButtonState(msg)}</span>
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
                placeholder="点右侧麦克风录音或停止录音"
                onKeyDown={(e) => { if (e.key === "Enter" && !sendDisabled) sendAnswer() }}
                disabled={sendDisabled && !waitingRef.current}
              />
              <div className="exam-mic-wrap">
                {showMicHint && (
                  <div className="exam-mic-hint" role="status">
                    说完后再次点击麦克风结束录音
                  </div>
                )}
                <button
                  type="button"
                  className={`exam-mic-btn ${recording ? "exam-mic-recording" : ""}`}
                  disabled={recordDisabled && !recording}
                  onClick={toggleRecording}
                >
                  {recording ? <MicOff className="size-[18px]" /> : <Mic className="size-[18px]" />}
                </button>
              </div>
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

      {zoomedImage && (
        <div className="exam-image-zoom" onClick={() => setZoomedImage(null)}>
          <img src={zoomedImage} alt="Zoomed" className="exam-image-zoom-img" />
          <button type="button" className="exam-image-zoom-close" onClick={() => setZoomedImage(null)}>
            ✕
          </button>
        </div>
      )}
    </div>
  )
}
