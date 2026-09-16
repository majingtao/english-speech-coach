"use client"

import { useCallback, useEffect, useRef, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { ArrowLeft, CheckCircle2, Clock, Headphones, Lightbulb, Mic, Play, RefreshCw, Square, Target, Volume2 } from "lucide-react"
import { useAiConfig } from "@/lib/exam/use-ai-config"
import { useSpeechRecorder } from "@/lib/exam/use-speech-recorder"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"
import { RecordingFeedback } from "@/components/speech/recording-feedback"
import { coachAction, coachDashboard, coachGrade, coachPartner, coachSession, coachStart, audioBase64,
  type CoachAttempt, type CoachDashboard, type CoachSession, type CoachTask, type CoachTurn } from "@/lib/api/speaking-coach"
import { readCoachDraft, writeCoachDraft } from "@/lib/exam/coach-draft"
import { CoachAudioPlayer } from "./audio-player"
import "./speaking-coach.css"

const LABELS: Record<string, string> = { relevance: "回答切题", reason: "补充理由", detail: "展开细节", question: "主动提问", response: "回应搭档", past: "讲述经历" }
const STATUS: Record<string, string> = { unobserved: "待了解", developing: "正在进步", practice: "优先练习", stable: "表现稳定" }
type Draft = { taskId: string; parentId: string; turns: CoachTurn[]; input: string; original: string; blob: Blob | null; submissionId: string; editing: boolean }
const blank = (task: CoachTask, parentId = ""): Draft => ({ taskId: task.id, parentId, turns: task.kind === "dialogue" ? [{ role: "partner", text: task.opening || "What do you think?" }] : [], input: "", original: "", blob: null, submissionId: crypto.randomUUID(), editing: true })
const errorText = (error: unknown) => error instanceof Error ? error.message : "操作未完成，请重试"

export function SpeakingCoach() {
  const router = useRouter(); const query = useSearchParams(); const sessionId = query.get("session") || ""
  const config = useAiConfig()
  const [dashboard, setDashboard] = useState<CoachDashboard | null>(null)
  const [session, setSession] = useState<CoachSession | null>(null)
  const [draft, setDraft] = useState<Draft | null>(null)
  const [busy, setBusy] = useState(""); const [error, setError] = useState(""); const [storageNotice, setStorageNotice] = useState("")
  const minutes = 12; const [remaining, setRemaining] = useState(600)
  const loading = useRef(false); const locked = useRef(false); const alive = useRef(true)
  const clockOffset = useRef(0)
  const speech = useSpeechRecorder({ modelId: config.currentAsr?.id || config.selectedAsrId, maxSeconds: 29,
    onText: text => setDraft(old => old ? { ...old, input: text, original: text } : old), onError: setError,
    onAudio: (blob, original) => setDraft(old => old ? { ...old, blob, original } : old) })

  const load = useCallback(async () => {
    loading.current = true; setBusy("加载练习…"); setError("")
    try {
      if (sessionId) {
        let value = await coachSession(sessionId)
        if (value.mode === "mock" && value.status === "active" && (value.deadline <= value.serverTime || value.tasks.every(t => value.attempts.some(a => a.taskId === t.id)))) value = await coachAction(sessionId, "finish")
        if (!alive.current) return
        clockOffset.current = value.serverTime - Date.now(); setSession(value)
        const task = value.tasks.find(t => !value.attempts.some(a => a.taskId === t.id)) || value.tasks[value.tasks.length - 1]
        let stored: Draft | null = null
        try { stored = await readCoachDraft<Draft>(sessionId) } catch { setStorageNotice("浏览器暂不能保存草稿，请完成后再离开") }
        if (!alive.current) return
        if (stored && value.tasks.some(t => t.id === stored.taskId) && !value.attempts.some(a => a.id === stored.submissionId)) setDraft(stored)
        else if (task) setDraft({ ...blank(task), editing: !value.attempts.some(a => a.taskId === task.id), parentId: value.tasks.indexOf(task) > 0 && value.mode === "practice" ? value.attempts[value.attempts.length - 1]?.id || "" : "" })
      } else {
        setSession(null); setDraft(null); setDashboard(await coachDashboard(minutes))
      }
    } catch (e) { setError(errorText(e)) }
    finally { loading.current = false; if (alive.current) setBusy("") }
  }, [sessionId, minutes])
  useEffect(() => { alive.current = true; const timer = setTimeout(() => void load(), 0); return () => { clearTimeout(timer); alive.current = false; stopTts() } }, [load])
  useEffect(() => {
    if (!session || !draft || loading.current) return
    const timer = setTimeout(() => {
      void writeCoachDraft(session.id, session.status === "finished" ? null : draft).catch(() => setStorageNotice("草稿未能保存在此设备，请先提交作答"))
    }, 200)
    return () => clearTimeout(timer)
  }, [draft, session])
  useEffect(() => {
    if (!session?.deadline || session.status !== "active") return
    const tick = () => setRemaining(Math.max(0, Math.ceil((session.deadline - Date.now() - clockOffset.current) / 1000)))
    const initial = setTimeout(tick, 0); const timer = setInterval(tick, 1000); return () => { clearTimeout(initial); clearInterval(timer) }
  }, [session?.deadline, session?.status])

  async function run(label: string, operation: () => Promise<void>) {
    if (locked.current) return
    locked.current = true; setBusy(label); setError("")
    try { await operation() } catch (e) { setError(errorText(e)) }
    finally { locked.current = false; if (alive.current) setBusy("") }
  }
  async function speak(text: string) {
    unlockAudio(); stopTts()
    try { if (config.ttsEngine === "system") await speakWithSystem(text, config.selectedVoice)
      else await speakWithServer(text, config.ttsEngine, config.selectedVoice, { onLoadingChange: () => {}, onSpeakingChange: () => {} }) }
    catch { setError("语音播放失败，可以再次点击听题") }
  }
  function setCurrent(value: CoachSession, task: CoachTask, parent = "") {
    speech.reset(); setSession(value); setDraft(blank(task, parent)); stopTts()
  }
  async function gradePending(value: CoachSession) {
    for (const a of value.attempts.filter(a => a.status !== "graded")) {
      setBusy(`正在生成反馈（${value.attempts.indexOf(a) + 1}/${value.attempts.length}）…`)
      const updated = await coachGrade(a.id); setSession(updated)
    }
  }
  async function finish() {
    if (!session) return
    speech.reset(); stopTts(); const value = await coachAction(session.id, "finish")
    setSession(value); await writeCoachDraft(session.id, null).catch(() => undefined)
    await gradePending(value)
  }
  const task = session?.tasks.find(t => t.id === draft?.taskId)
  const last = session?.attempts.filter(a => a.taskId === task?.id).slice(-1)[0]
  const showingFeedback = !!last && !draft?.editing
  const recordingBusy = speech.busy || speech.recording
  const learnerTurns = draft?.turns.filter(t => t.role === "learner").length || 0
  const awaitingPartner = task?.kind === "dialogue" && draft?.turns[draft.turns.length - 1]?.role === "learner" && learnerTurns < 4
  const expired = session?.mode === "mock" && remaining === 0

  async function addTurn() {
    if (!draft || !session || !task || !draft.input.trim()) return
    const turn: CoachTurn = { role: "learner", text: draft.input.trim(), asrText: draft.original }
    if (draft.blob) turn.audioBase64 = await audioBase64(draft.blob)
    const next = [...draft.turns, turn]
    setDraft({ ...draft, turns: next, input: "", original: "", blob: null })
    speech.reset()
    if (learnerTurns + 1 < 4) {
      const reply = await coachPartner(session.id, task.id, next)
      setDraft(old => old ? { ...old, turns: [...next, { role: "partner", text: reply.text }] } : old)
      await speak(reply.text)
    }
  }
  async function submit() {
    if (!draft || !session || !task) return
    let turns = draft.turns
    if (task.kind === "interview") {
      const turn: CoachTurn = { role: "learner", text: draft.input.trim(), asrText: draft.original }
      if (draft.blob) turn.audioBase64 = await audioBase64(draft.blob)
      turns = [turn]
    }
    const value = await coachAction(session.id, "attempts", { id: draft.submissionId, taskId: task.id, parentId: draft.parentId, turns })
    speech.reset(); setSession(value); setDraft(old => old ? { ...old, editing: false, input: "", blob: null } : old)
    await writeCoachDraft(session.id, null).catch(() => undefined)
    if (session.mode === "practice") setSession(await coachGrade(draft.submissionId))
    else {
      const next = value.tasks.find(t => !value.attempts.some(a => a.taskId === t.id))
      if (next) setCurrent(value, next)
      else { const done = await coachAction(session.id, "finish"); setSession(done); await gradePending(done) }
    }
  }

  return <main className="coach-shell">
    <header className="coach-header"><button onClick={() => { speech.reset(); stopTts(); router.push(sessionId ? "/speaking-coach" : "/") }} disabled={!!busy}><ArrowLeft size={18} />{sessionId ? "练习中心" : "首页"}</button><strong>KET 口语成长</strong><span className="coach-pill">学习 · 表达 · 进步</span></header>
    {error && <div className="coach-alert" role="alert">{error}<button onClick={() => void load()} disabled={!!busy}>刷新已保存的记录</button></div>}
    {storageNotice && <p className="coach-notice">{storageNotice}</p>}
    {busy && <div className="coach-busy" role="status"><span className="coach-spinner" />{busy}</div>}

    {!sessionId && dashboard && <>
      <section className="coach-hero"><div><p className="coach-eyebrow">TODAY’S SPEAKING</p><h1>今天，再多说一点。</h1><p>练习表达，听见自己的进步，再用新题试一试。</p></div><div className="coach-hero-icon"><Headphones size={54} /></div></section>
      <section className="coach-card"><div className="coach-section-title"><h2><Target size={21} />今日练习</h2><span className="coach-muted">约 {dashboard.plan.minutes} 分钟</span></div><p className="coach-muted">{dashboard.plan.date} · 今日计划按 {dashboard.plan.minutes} 分钟安排，刷新后仍可继续。</p><div className="coach-plan">{dashboard.plan.items.map((item, i) => <article key={item.taskId}><span className="coach-number">{item.completed ? <CheckCircle2 /> : i + 1}</span><div><h3>{item.title}</h3><p>{item.reason}</p><small>{Math.round(item.seconds / 60)} 分钟</small></div><button disabled={!!busy} onClick={() => void run("准备练习…", async () => { const value = await coachStart("practice", item.taskId, dashboard.plan.id); router.push(`/speaking-coach?session=${value.id}`) })}>{item.completed ? "再练一次" : "开始"}<Play size={15} /></button></article>)}</div></section>
      <section className="coach-card"><h2>我的六项能力</h2><div className="coach-skills">{dashboard.skills.map(s => <div key={s.code}><span>{s.label}</span><strong className={`coach-status-${s.status}`}>{STATUS[s.status]}</strong><small>{s.observed ? `${s.observed} 次独立观察` : "完成练习后逐步了解"}</small></div>)}</div><p className="coach-muted">跨新题、跨日期的独立表现会计入进度。提示与重说帮助学习，不直接证明掌握。</p></section>
      <div className="coach-two"><section className="coach-card"><h2><Mic size={21} />专项练习</h2><p>可以看提示、重说，再换题独立验证。</p><div className="coach-task-list">{dashboard.tasks.filter((t, i, all) => all.findIndex(x => x.focus === t.focus) === i).map(t => <button key={t.id} disabled={!!busy} onClick={() => void run("准备练习…", async () => { const value = await coachStart("practice", t.id); router.push(`/speaking-coach?session=${value.id}`) })}>{LABELS[t.focus]} →</button>)}</div></section><section className="coach-card coach-mock"><h2><Clock size={21} />限时专项模考</h2><p>个人问答＋搭档讨论，10 分钟内完成。全程录音，结束后统一反馈。</p><p className="coach-muted">原创 KET 训练题；本版为专项模拟，非剑桥官方试卷或官方成绩。</p><button className="coach-primary" disabled={!!busy} onClick={() => void run("准备模考…", async () => { const value = await coachStart("mock"); router.push(`/speaking-coach?session=${value.id}`) })}>开始模拟</button></section></div>
      {dashboard.sessions.length > 0 && <section className="coach-card"><h2>最近的练习</h2><div className="coach-history">{dashboard.sessions.map(s => <button key={s.id} onClick={() => router.push(`/speaking-coach?session=${s.id}`)}><span>{s.mode === "mock" ? "专项模考" : "口语练习"}</span><small>{String(s.created_at).replace("T", " ").slice(0, 16)}</small><strong>{s.status === "active" ? "继续练习" : "查看反馈"} →</strong></button>)}</div></section>}
    </>}

    {session && session.status === "active" && task && draft && <>
      <section className="coach-session-heading"><div><span className="coach-eyebrow">{session.mode === "mock" ? "MOCK TEST" : "SPEAKING PRACTICE"}</span><h1>{task.title}</h1></div>{session.mode === "mock" && <strong className={`coach-timer ${remaining < 60 ? "urgent" : ""}`}>{Math.floor(remaining / 60)}:{String(remaining % 60).padStart(2, "0")}</strong>}</section>
      {expired && <div className="coach-alert">时间已到，已提交的作答会保留。<button onClick={() => void run("结束模考…", finish)} disabled={!!busy}>结束并查看报告</button></div>}
      <section className="coach-card"><p className="coach-muted">{task.kind === "dialogue" ? "搭档讨论" : "个人问答"} · {session.tasks.indexOf(task) + 1}/{session.tasks.length}</p><h2 className="coach-question">{task.prompt}</h2><button disabled={recordingBusy || !!busy} onClick={() => void speak(task.prompt)}><Volume2 size={17} />听题</button>{task.options.length > 0 && <div className="coach-options">{task.options.map(option => <div key={option}>{option}</div>)}</div>}
        {session.mode === "practice" && <div className="coach-hint">{task.hint ? <><p><Lightbulb size={18} />{task.hint}</p><p lang="en">{task.sample}</p><small>这是表达示范，可以用你自己的想法回答。</small></> : <button disabled={!!busy || recordingBusy} onClick={() => void run("加载提示…", async () => setSession(await coachAction(session.id, "hint", { taskId: task.id })))}><Lightbulb size={16} />我需要一点提示</button>}</div>}
      </section>
      {showingFeedback && last ? <>
        <Feedback attempt={last} />
        <div className="coach-actions">{last.status !== "graded" ? <button className="coach-primary" disabled={!!busy} onClick={() => void run("重新生成反馈…", async () => setSession(await coachGrade(last.id)))}>重试评分</button> : <><button disabled={!!busy} onClick={() => { speech.reset(); setDraft(blank(task, last.id)) }}><RefreshCw size={16} />按建议再说一次</button><button className="coach-primary" disabled={!!busy} onClick={() => void run("准备新的验证题…", async () => { const value = await coachAction(session.id, "transfer", { parentId: last.id }); setCurrent(value, value.tasks[value.tasks.length - 1], last.id) })}>换题独立验证 →</button></>}<button disabled={!!busy} onClick={() => void run("保存学习结果…", finish)}>完成本次练习</button></div>
        {session.attempts.filter(a => a.taskId === task.id && a.id !== last.id).map(a => <details className="coach-card" key={a.id}><summary>查看之前的回答与录音</summary><Feedback attempt={a} /></details>)}
      </> : <section className="coach-card">
        {draft.parentId && <p className="coach-stage">{session.attempts.find(a => a.id === draft.parentId)?.taskId === task.id ? "重说：尝试用上刚才的建议" : "新题验证：先独立回答，再看反馈"}</p>}
        {task.kind === "dialogue" && <div className="coach-conversation">{draft.turns.map((turn, index) => <div className={`coach-turn ${turn.role}`} key={index}><small>{turn.role === "learner" ? "你" : "AI 搭档"}</small><p lang="en">{turn.text}</p><button aria-label="朗读这一句" disabled={recordingBusy || !!busy} onClick={() => void speak(turn.text)}><Volume2 size={15} /></button></div>)}</div>}
        {awaitingPartner && <button disabled={!!busy || expired} onClick={() => void run("搭档正在回复…", async () => { const reply = await coachPartner(session.id, task.id, draft.turns); setDraft(old => old ? { ...old, turns: [...old.turns, { role: "partner", text: reply.text }] } : old); await speak(reply.text) })}>请搭档继续回复</button>}
        {(task.kind !== "dialogue" || learnerTurns < 4) && !awaitingPartner && <>
          <div className="coach-record"><button className={speech.recording ? "coach-recording" : "coach-primary"} disabled={!!busy || speech.busy || expired} onClick={() => { stopTts(); if (!speech.recording) setDraft(old => old ? { ...old, input: "", original: "", blob: null } : old); void speech.toggle() }}>{speech.recording ? <Square size={21} /> : <Mic size={21} />}{speech.recording ? "说完了" : "开始录音"}</button><span>每次最多 29 秒，可在对话中分多轮表达。</span></div>
          <RecordingFeedback recorder={speech} />
          {draft.blob && <CoachAudioPlayer key={`${draft.original}-${draft.blob.size}`} blob={draft.blob} label="刚才的录音" />}
          <label className="coach-answer-label">{session.mode === "mock" ? "识别结果" : "我的回答（可以纠正识别错误）"}<textarea rows={3} value={draft.input} readOnly={session.mode === "mock"} disabled={!!busy || recordingBusy} onChange={e => setDraft(old => old ? { ...old, input: e.target.value } : old)} placeholder="点击录音，试着用自己的话回答…" /></label>
          {session.mode === "practice" && !draft.blob && <small className="coach-muted">也可以先打字练习；发音评价和独立口语进度需要录音。</small>}
          {task.kind === "dialogue" && <button disabled={!draft.input.trim() || !!busy || recordingBusy || expired || (session.mode === "mock" && !draft.blob)} onClick={() => void run("发送回答…", addTurn)}>发给搭档 →</button>}
        </>}
        <div className="coach-actions"><button className="coach-primary" disabled={!!busy || recordingBusy || expired || (task.kind === "dialogue" ? learnerTurns < 3 || !!draft.input.trim() : !draft.input.trim()) || (session.mode === "mock" && task.kind === "interview" && !draft.blob)} onClick={() => void run(session.mode === "mock" ? "保存作答…" : "保存并生成反馈…", submit)}>{session.mode === "mock" ? "提交并继续" : "完成并查看反馈"}</button>{task.kind === "dialogue" && <span>{learnerTurns}/3 轮练习目标（不是考试评分门槛）</span>}</div>
      </section>}
      <button className="coach-end" disabled={!!busy || recordingBusy} onClick={() => { if (window.confirm("结束本次练习？尚未提交的回答不会计入报告。")) void run("结束练习…", finish) }}>结束并查看已提交的记录</button>
    </>}

    {session?.status === "finished" && <>
      <section className="coach-hero"><div><p className="coach-eyebrow">YOUR SPEAKING JOURNEY</p><h1>听见今天的进步。</h1><p>这份报告来自你的实际作答。未完成的题目不会记成能力不足。</p></div><CheckCircle2 size={58} /></section>
      <section className="coach-card"><h2>本次练习记录</h2><p>完成 {new Set(session.attempts.map(a => a.taskId)).size}/{session.tasks.length} 道题 · {session.attempts.length} 次作答</p><p className="coach-muted">训练反馈不等同于剑桥考试成绩。发音评价使用录音及原始识别文本，独立展示。</p>{session.attempts.some(a => a.status !== "graded") && <button className="coach-primary" disabled={!!busy} onClick={() => void run("继续生成反馈…", () => gradePending(session))}>生成剩余反馈</button>}</section>
      {session.tasks.map(t => <section className="coach-card" key={t.id}><h2>{t.title}</h2><p lang="en">{t.prompt}</p>{session.attempts.filter(a => a.taskId === t.id).map(a => <Feedback key={a.id} attempt={a} />)}{!session.attempts.some(a => a.taskId === t.id) && <p className="coach-muted">本题未作答</p>}<button disabled={!!busy} onClick={() => void run("准备专项复练…", async () => { const value = await coachStart("practice", t.id); router.push(`/speaking-coach?session=${value.id}`) })}>练一练这个目标</button></section>)}
      <button className="coach-primary" onClick={() => router.push("/speaking-coach")}>返回今日练习</button>
    </>}
    {!busy && !dashboard && !session && <button onClick={() => void load()}>重新加载</button>}
  </main>
}

function Feedback({ attempt }: { attempt: CoachAttempt }) {
  const feedback = attempt.feedback
  return <section className="coach-feedback">
    <div className="coach-section-title"><h3>{attempt.stage === "retry" ? "重说反馈" : attempt.stage === "transfer" ? "新题验证" : "首次作答"}</h3><small>{attempt.assisted ? "辅助练习记录" : "独立作答记录"}</small></div>
    {attempt.turns.map((turn, i) => turn.role === "learner" && <div key={i}><p className="coach-quote" lang="en">“{turn.text}”</p>{turn.hasAudio && <CoachAudioPlayer attemptId={attempt.id} turnIndex={i} label={`我的第 ${i + 1} 轮录音`} />}</div>)}
    {!feedback ? <p role="status">{attempt.error || (attempt.status === "grading" ? "正在评分，可稍后刷新" : "作答已保存，等待生成反馈")}</p> : <>
      <p className="coach-positive"><CheckCircle2 size={18} />{feedback.strength}</p><p className="coach-improvement"><Target size={18} />{feedback.improvement}</p>
      <div className="coach-example"><small>保留你的想法，试着这样表达</small><p lang="en">{feedback.revised}</p></div>
      {feedback.skills.map(skill => <div className="coach-evidence" key={skill.code}><strong>{LABELS[skill.code]} · {!skill.observed ? "暂未观察到" : skill.met ? "本次做到了" : "继续练习"}</strong>{skill.evidence && <blockquote lang="en">{skill.evidence}</blockquote>}<p>{skill.feedback}</p></div>)}
      <div className="coach-pronunciation"><h4>发音与流利度</h4>{feedback.pronunciation.map(p => <div key={p.turnIndex}>{p.status === "assessed" ? <><p>第 {p.turnIndex + 1} 轮 · 发音准确度 {p.accuracy}{p.fluency != null && ` · 流利度 ${p.fluency}`}{p.prosody != null && ` · 韵律 ${p.prosody}`}</p><div className="coach-words">{p.words?.map((w, i) => <span key={i} className={w.accuracy < 70 ? "needs-practice" : ""} title={`发音准确度 ${w.accuracy}`}>{w.word} <small>{w.accuracy}</small></span>)}</div><small>基于原始录音与识别文本；如识别有误，请回听核对。</small></> : <p>{p.message || "本轮未获得发音评价"}</p>}</div>)}</div>
    </>}
  </section>
}
