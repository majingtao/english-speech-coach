"use client"
import { useEffect, useMemo, useRef, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { ArrowLeft, Check, ChevronRight, Loader2, RotateCcw, X } from "lucide-react"
import { fetchGrammarPractice, submitGrammarAnswer, type GrammarAnswer, type GrammarQuestion } from "@/lib/api/grammar"

function parseOptions(json?: string) { try { const v = JSON.parse(json || "[]"); return Array.isArray(v) ? v.map(String) : [] } catch { return [] } }
export function GrammarPractice() {
  const router = useRouter(), params = useSearchParams(); const pointId = Number(params.get("point")); const pointName = params.get("name") || "语法练习"
  const [questions, setQuestions] = useState<GrammarQuestion[]>([]), [index, setIndex] = useState(0), [answer, setAnswer] = useState(""), [feedback, setFeedback] = useState<GrammarAnswer | null>(null)
  const [loading, setLoading] = useState(true), [submitting, setSubmitting] = useState(false), [error, setError] = useState(""), [stats, setStats] = useState({ correct: 0, wrong: 0 }); const startedAt = useRef(Date.now())
  const question = questions[index], options = useMemo(() => parseOptions(question?.optionsJson), [question]); const done = questions.length > 0 && index >= questions.length
  useEffect(() => { if (!pointId) { setError("知识点参数无效"); setLoading(false); return } fetchGrammarPractice(pointId, 1, 10).then(setQuestions).catch((e) => setError(e instanceof Error ? e.message : "题目加载失败")).finally(() => setLoading(false)) }, [pointId])
  async function submit() { if (!question || !answer.trim()) return; setSubmitting(true); try { const result = await submitGrammarAnswer(question.id, answer.trim(), Math.round((Date.now() - startedAt.current) / 1000)); setFeedback(result); setStats((s) => result.correct ? { ...s, correct: s.correct + 1 } : { ...s, wrong: s.wrong + 1 }) } catch (e) { setError(e instanceof Error ? e.message : "提交失败") } finally { setSubmitting(false) } }
  function next() { setIndex((i) => i + 1); setAnswer(""); setFeedback(null); setError(""); startedAt.current = Date.now() }
  if (loading) return <main className="grammar-shell"><div className="grammar-state"><Loader2 className="animate-spin" />正在准备练习</div></main>
  if (error && questions.length === 0) return <main className="grammar-shell"><div className="grammar-state grammar-error">{error}<button onClick={() => router.push("/grammar")}>返回知识点</button></div></main>
  if (done) return <main className="grammar-shell"><section className="grammar-result"><div className="grammar-result-mark"><Check /></div><h1>完成练习</h1><p>{pointName}</p><div className="grammar-result-stats"><span><strong>{stats.correct}</strong>答对</span><span><strong>{stats.wrong}</strong>需要巩固</span></div><button onClick={() => window.location.reload()}><RotateCcw />再练一组</button><button className="secondary" onClick={() => router.push("/grammar")}>选择其他知识点</button></section></main>
  if (!question) return <main className="grammar-shell"><div className="grammar-state grammar-error">这个知识点还没有已发布题目<button onClick={() => router.push("/grammar")}>返回知识点</button></div></main>
  return <main className="grammar-shell grammar-practice-shell">
    <header className="grammar-practice-head"><button onClick={() => router.push("/grammar")} title="退出练习"><ArrowLeft /></button><div><strong>{pointName}</strong><span>{index + 1} / {questions.length}</span></div><div className="grammar-score"><span><Check />{stats.correct}</span><span><X />{stats.wrong}</span></div></header>
    <div className="grammar-progress"><i style={{ width: `${((index + 1) / questions.length) * 100}%` }} /></div>
    <section className="grammar-question-card"><span className="grammar-level">L{question.difficulty}</span><p className="grammar-instruction">{question.instruction}</p><h1>{question.stem}</h1></section>
    <section className="grammar-answer-area">{question.questionType === "single_choice" ? <div className="grammar-options">{options.map((option, i) => <button key={option} disabled={!!feedback} className={`${answer === option ? "selected" : ""} ${feedback && feedback.acceptedAnswers.includes(option) ? "correct" : ""} ${feedback && answer === option && !feedback.correct ? "wrong" : ""}`} onClick={() => setAnswer(option)}><span>{String.fromCharCode(65 + i)}</span>{option}</button>)}</div> : <label className="grammar-text-answer"><span>填入正确形式</span><input value={answer} disabled={!!feedback} onChange={(e) => setAnswer(e.target.value)} onKeyDown={(e) => { if (e.key === "Enter" && !feedback) submit() }} autoCapitalize="none" autoComplete="off" /></label>}
      {feedback && <div className={`grammar-feedback ${feedback.correct ? "is-correct" : "is-wrong"}`}><div><span>{feedback.correct ? <Check /> : <X />}</span><strong>{feedback.correct ? "答对了" : `正确答案：${feedback.acceptedAnswers.join(" / ")}`}</strong></div>{!feedback.correct && feedback.errorHint && <p>{feedback.errorHint}</p>}<p>{feedback.explanationZh}</p>{feedback.ruleText && <small>{feedback.ruleText}</small>}</div>}
      {error && <p className="grammar-inline-error">{error}</p>}{!feedback ? <button className="grammar-submit" disabled={!answer.trim() || submitting} onClick={submit}>{submitting && <Loader2 className="animate-spin" />}{submitting ? "正在检查" : "提交答案"}</button> : <button className="grammar-submit" onClick={next}>{index + 1 === questions.length ? "查看结果" : "下一题"}<ChevronRight /></button>}
    </section>
  </main>
}
