"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  BookOpen,
  Check,
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  ListRestart,
  Search,
  Trophy,
  Volume2,
  X,
} from "lucide-react"
import {
  COMPARISONS,
  VERBS,
  acceptedForms,
  primaryForm,
  type ComparisonForm,
  type VerbForm,
} from "@/lib/grammar/word-forms"
import { VERB_TENSE_EXAMPLES } from "@/lib/grammar/verb-tense-examples"
import { COMPARISON_EXAMPLES } from "@/lib/grammar/comparison-examples"

type ModuleKind = "verbs" | "comparisons"
type ViewMode = "learn" | "practice" | "wrong"
type FormRole = "base" | "past" | "participle" | "comparative" | "superlative" | "sentence"

interface StoredProgress {
  seen: number
  correct: number
  wrong: number
  wrongIds: string[]
}

interface PracticeQuestion {
  key: string
  itemId: string
  prompt: string
  completeSentence: string
  instruction: string
  answer: string
  accepted: string[]
  options: string[]
  formLabel: string
  translation?: string
}

const PAGE_SIZE = 8
const EMPTY_PROGRESS: StoredProgress = { seen: 0, correct: 0, wrong: 0, wrongIds: [] }

function hashText(value: string) {
  let hash = 2166136261
  for (let index = 0; index < value.length; index += 1) {
    hash ^= value.charCodeAt(index)
    hash = Math.imul(hash, 16777619)
  }
  return hash >>> 0
}

function shuffled<T>(items: T[], seed: string) {
  return [...items].sort((a, b) => hashText(`${seed}:${JSON.stringify(a)}`) - hashText(`${seed}:${JSON.stringify(b)}`))
}

function uniqueOptions(answer: string, ownForms: string[], pool: string[], seed: string) {
  const values = [answer, ...ownForms, ...shuffled(pool, seed)]
    .flatMap((value) => value.split(" / "))
    .map((value) => value.trim())
    .filter(Boolean)
  const unique = values.filter((value, index) => values.indexOf(value) === index)
  return shuffled(unique.slice(0, 4), `${seed}:final`)
}

function verbQuestion(item: VerbForm, index: number): PracticeQuestion {
  const examples = VERB_TENSE_EXAMPLES[item.id]
  const continuousPhrase = examples.continuous?.[0].match(/\b(?:was|were)\s+\w+ing\b/i)?.[0]
  const continuous = index % 3 === 1 && !!continuousPhrase
  const perfect = index % 3 === 2 || (index % 3 === 1 && !continuousPhrase)
  const form = perfect ? item.participle : item.past
  const answer = continuousPhrase || (item.id === "be" && !perfect ? "was" : primaryForm(form))
  const prompt = continuous && examples.continuous
    ? examples.continuous[0].replace(continuousPhrase!, "___")
    : perfect ? item.perfectSentence : item.pastSentence
  const pool = VERBS.flatMap((verb) => [primaryForm(verb.past), primaryForm(verb.participle)])
  return {
    key: `${item.id}:${continuous ? "continuous" : perfect ? "perfect" : "past"}:${index}`,
    itemId: item.id,
    prompt,
    completeSentence: continuous && examples.continuous ? examples.continuous[0] : prompt.replace("___", answer),
    instruction: continuous ? "过去正在进行的动作：选择完整的过去进行时" : perfect ? "根据 have / has 填入过去分词" : "根据时间提示填入过去式",
    answer,
    accepted: continuous ? [answer.toLowerCase()] : item.id === "be" && !perfect ? ["was"] : acceptedForms(form),
    options: uniqueOptions(answer, [item.base, item.past, item.participle, continuousPhrase || ""], pool, `${item.id}:${index}`),
    formLabel: continuous ? "过去进行时" : perfect ? "过去分词" : "过去式",
    translation: continuous && examples.continuous ? examples.continuous[1] : perfect ? examples.perfectZh : examples.pastZh,
  }
}

function comparisonQuestion(item: ComparisonForm, index: number): PracticeQuestion {
  const superlative = index % 2 === 1
  const form = superlative ? item.superlative : item.comparative
  const answer = primaryForm(form)
  const prompt = superlative ? item.superlativeSentence : item.comparativeSentence
  const pool = COMPARISONS.flatMap((word) => [primaryForm(word.comparative), primaryForm(word.superlative)])
  return {
    key: `${item.id}:${superlative ? "superlative" : "comparative"}:${index}`,
    itemId: item.id,
    prompt,
    completeSentence: prompt.replace("___", answer),
    instruction: superlative ? "根据句意填入最高级" : "根据句意填入比较级",
    answer,
    accepted: acceptedForms(form),
    options: uniqueOptions(answer, [item.base, item.comparative, item.superlative], pool, `${item.id}:${index}`),
    formLabel: superlative ? "最高级" : "比较级",
  }
}

function pronunciationText(text: string, itemId: string, role: FormRole) {
  if (itemId === "read" && (role === "past" || role === "participle")) return "red"
  return primaryForm(text)
}

function escapeRegExp(value: string) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")
}

function HighlightedSentence({ sentence, forms }: { sentence: string; forms: string[] }) {
  const candidates = forms.filter(Boolean).sort((a, b) => b.length - a.length)
  const pattern = candidates.length ? new RegExp(`\\b(${candidates.map(escapeRegExp).join("|")})\\b`, "i") : null
  const match = pattern?.exec(sentence)
  if (!match || match.index === undefined) return <>{sentence}</>
  return <>{sentence.slice(0, match.index)}<strong>{match[0]}</strong>{sentence.slice(match.index + match[0].length)}</>
}

function presentCandidates(base: string) {
  if (base === "be") return ["am", "is", "are"]
  if (base === "have") return ["has", "have"]
  if (base === "do") return ["does", "do"]
  if (/[^aeiou]y$/i.test(base)) return [`${base.slice(0, -1)}ies`, base]
  if (/(?:s|x|z|ch|sh|o)$/i.test(base)) return [`${base}es`, base]
  return [`${base}s`, base]
}

function TenseRow({
  label,
  formula,
  usage,
  sentence,
  translation,
  forms,
  itemId,
  speaking,
  onSpeak,
}: {
  label: string
  formula: string
  usage: string
  sentence?: string
  translation?: string
  forms: string[]
  itemId: string
  speaking: string
  onSpeak: (text: string, key: string) => void
}) {
  return (
    <div className={`wordlab-tense-row ${sentence ? "" : "is-limited"}`}>
      <div className="wordlab-tense-label"><strong>{label}</strong><span>{formula}</span></div>
      <div className="wordlab-tense-copy">
        <small>{usage}</small>
        {sentence ? <><p><HighlightedSentence sentence={sentence} forms={forms} /></p><em>{translation}</em></> : <p>{translation}</p>}
      </div>
      {sentence && <AudioButton text={sentence} itemId={`${itemId}:${label}`} role="sentence" speaking={speaking} onSpeak={onSpeak} label={`${label}例句`} />}
    </div>
  )
}

function AudioButton({
  text,
  itemId,
  role,
  speaking,
  onSpeak,
  label,
}: {
  text: string
  itemId: string
  role: FormRole
  speaking: string
  onSpeak: (text: string, key: string) => void
  label?: string
}) {
  const key = `${itemId}:${role}:${text}`
  return (
    <button
      type="button"
      className={`wordlab-audio ${speaking === key ? "is-playing" : ""}`}
      onClick={(event) => {
        event.stopPropagation()
        onSpeak(pronunciationText(text, itemId, role), key)
      }}
      title={label ? `播放${label}` : "播放英式发音"}
      aria-label={label ? `播放${label}` : `播放 ${text}`}
    >
      <Volume2 />
    </button>
  )
}

function VerbCard({ item, speaking, onSpeak }: { item: VerbForm; speaking: string; onSpeak: (text: string, key: string) => void }) {
  const examples = VERB_TENSE_EXAMPLES[item.id]
  const pastAnswer = item.id === "be" ? "was" : primaryForm(item.past)
  const perfectAnswer = primaryForm(item.participle)
  const pastSentence = item.pastSentence.replace("___", pastAnswer)
  const perfectSentence = item.perfectSentence.replace("___", perfectAnswer)
  const continuousForm = examples.continuous?.[0].match(/\b(?:was|were)\s+(\w+ing)\b/i)?.[1] || ""
  return (
    <article className="wordlab-card">
      <div className="wordlab-card-head">
        <div><h2>{item.base}</h2><p>{item.zh}</p></div>
        <span className={item.kind === "regular" ? "regular" : "irregular"}>{item.kind === "regular" ? "规则" : "不规则"}</span>
      </div>
      <div className="wordlab-forms wordlab-forms-three">
        {([
          ["原形", item.base, "base"],
          ["过去式", item.past, "past"],
          ["过去分词", item.participle, "participle"],
        ] as const).map(([label, value, role]) => (
          <div key={label}><span>{label}</span><strong>{value}</strong><AudioButton text={value} itemId={item.id} role={role} speaking={speaking} onSpeak={onSpeak} label={`${label} ${value}`} /></div>
        ))}
      </div>
      <div className="wordlab-tense-list">
        <TenseRow label="一般现在时" formula="动词原形 / 三单" usage="习惯、事实或状态" sentence={examples.present[0]} translation={examples.present[1]} forms={presentCandidates(item.base)} itemId={item.id} speaking={speaking} onSpeak={onSpeak} />
        <TenseRow label="一般过去时" formula="past simple" usage="已经结束的过去事件" sentence={pastSentence} translation={examples.pastZh} forms={acceptedForms(item.past)} itemId={item.id} speaking={speaking} onSpeak={onSpeak} />
        <TenseRow label="过去进行时" formula="was / were + -ing" usage="过去正在进行；常与 when / while 连用" sentence={examples.continuous?.[0]} translation={examples.continuous?.[1] || examples.continuousNote} forms={[continuousForm]} itemId={item.id} speaking={speaking} onSpeak={onSpeak} />
        <TenseRow label="现在完成时" formula="have / has + 过去分词" usage="与现在有关；常与 already / yet / ever / for / since 连用" sentence={perfectSentence} translation={examples.perfectZh} forms={acceptedForms(item.participle)} itemId={item.id} speaking={speaking} onSpeak={onSpeak} />
      </div>
      {item.note && <p className="wordlab-note">{item.note}</p>}
    </article>
  )
}

function ComparisonCard({ item, speaking, onSpeak }: { item: ComparisonForm; speaking: string; onSpeak: (text: string, key: string) => void }) {
  const examples = COMPARISON_EXAMPLES[item.id]
  const comparativeSentence = item.comparativeSentence.replace("___", primaryForm(item.comparative))
  const superlativeSentence = item.superlativeSentence.replace("___", primaryForm(item.superlative))
  return (
    <article className="wordlab-card">
      <div className="wordlab-card-head">
        <div><h2>{item.base}</h2><p>{item.zh}</p></div>
        <span>{item.rule}</span>
      </div>
      <div className="wordlab-forms wordlab-forms-three">
        {([
          ["原级", item.base, "base"],
          ["比较级", item.comparative, "comparative"],
          ["最高级", item.superlative, "superlative"],
        ] as const).map(([label, value, role]) => (
          <div key={label}><span>{label}</span><strong>{value}</strong><AudioButton text={value} itemId={item.id} role={role} speaking={speaking} onSpeak={onSpeak} label={`${label} ${value}`} /></div>
        ))}
      </div>
      <div className="wordlab-tense-list wordlab-comparison-examples">
        <TenseRow label="原级" formula={item.base} usage="描述一个人或事物" sentence={examples.base[0]} translation={examples.base[1]} forms={item.base.split(" / ")} itemId={item.id} speaking={speaking} onSpeak={onSpeak} />
        <TenseRow label="比较级" formula={item.comparative} usage="两者比较；常与 than 连用" sentence={comparativeSentence} translation={examples.comparativeZh} forms={acceptedForms(item.comparative)} itemId={item.id} speaking={speaking} onSpeak={onSpeak} />
        <TenseRow label="最高级" formula={item.superlative} usage="三者或以上比较；前面通常有 the" sentence={superlativeSentence} translation={examples.superlativeZh} forms={acceptedForms(item.superlative)} itemId={item.id} speaking={speaking} onSpeak={onSpeak} />
      </div>
      {item.note && <p className="wordlab-note">{item.note}</p>}
    </article>
  )
}

export function WordFormLab({ module }: { module: ModuleKind }) {
  const router = useRouter()
  const isVerbs = module === "verbs"
  const allItems = useMemo(() => isVerbs ? VERBS : COMPARISONS, [isVerbs])
  const storageKey = `ket-wordlab:${module}:v1`
  const [view, setView] = useState<ViewMode>("learn")
  const [search, setSearch] = useState("")
  const [filter, setFilter] = useState("all")
  const [page, setPage] = useState(0)
  const [progress, setProgress] = useState<StoredProgress>(() => {
    if (typeof window === "undefined") return EMPTY_PROGRESS
    try {
      const saved = window.localStorage.getItem(storageKey)
      return saved ? { ...EMPTY_PROGRESS, ...JSON.parse(saved) } : EMPTY_PROGRESS
    } catch {
      return EMPTY_PROGRESS
    }
  })
  const [questions, setQuestions] = useState<PracticeQuestion[]>([])
  const [questionIndex, setQuestionIndex] = useState(0)
  const [selected, setSelected] = useState("")
  const [checked, setChecked] = useState(false)
  const [sessionScore, setSessionScore] = useState({ correct: 0, wrong: 0 })
  const [speaking, setSpeaking] = useState("")

  useEffect(() => () => window.speechSynthesis?.cancel(), [])

  const saveProgress = useCallback((next: StoredProgress) => {
    setProgress(next)
    window.localStorage.setItem(storageKey, JSON.stringify(next))
  }, [storageKey])

  const speak = useCallback((text: string, key: string) => {
    if (!("speechSynthesis" in window)) return
    window.speechSynthesis.cancel()
    if (speaking === key) {
      setSpeaking("")
      return
    }
    const utterance = new SpeechSynthesisUtterance(text.replace("___", " ... "))
    const voices = window.speechSynthesis.getVoices()
    utterance.voice = voices.find((voice) => voice.lang.toLowerCase() === "en-gb")
      || voices.find((voice) => voice.lang.toLowerCase().startsWith("en-gb"))
      || voices.find((voice) => voice.lang.toLowerCase().startsWith("en"))
      || null
    utterance.lang = "en-GB"
    utterance.rate = 0.86
    utterance.onend = () => setSpeaking("")
    utterance.onerror = () => setSpeaking("")
    setSpeaking(key)
    window.speechSynthesis.speak(utterance)
  }, [speaking])

  const filteredItems = useMemo(() => {
    const keyword = search.trim().toLowerCase()
    return allItems.filter((item) => {
      const matchesSearch = !keyword || Object.values(item).some((value) => String(value).toLowerCase().includes(keyword))
      if (!matchesSearch || filter === "all") return matchesSearch
      if (isVerbs) return (item as VerbForm).kind === filter
      return (item as ComparisonForm).rule === filter
    })
  }, [allItems, filter, isVerbs, search])

  const pageCount = Math.max(1, Math.ceil(filteredItems.length / PAGE_SIZE))
  const visibleItems = filteredItems.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)
  const currentQuestion = questions[questionIndex]
  const sessionDone = questions.length > 0 && questionIndex >= questions.length

  function buildQuestions(mode: "practice" | "wrong") {
    const wrongSet = new Set(progress.wrongIds)
    const source = mode === "wrong" ? allItems.filter((item) => wrongSet.has(item.id)) : allItems
    const seed = `${Date.now()}:${progress.seen}`
    const chosen = shuffled(source, seed).slice(0, mode === "wrong" ? Math.min(10, source.length) : 10)
    const next = chosen.map((item, index) => isVerbs
      ? verbQuestion(item as VerbForm, index)
      : comparisonQuestion(item as ComparisonForm, index))
    setQuestions(next)
    setQuestionIndex(0)
    setSelected("")
    setChecked(false)
    setSessionScore({ correct: 0, wrong: 0 })
  }

  function switchView(next: ViewMode) {
    setView(next)
    setPage(0)
    setSearch("")
    if (next === "practice" || next === "wrong") buildQuestions(next)
  }

  function checkAnswer() {
    if (!currentQuestion || !selected || checked) return
    const correct = currentQuestion.accepted.includes(selected.toLowerCase())
    const wrongIds = new Set(progress.wrongIds)
    if (correct) wrongIds.delete(currentQuestion.itemId)
    else wrongIds.add(currentQuestion.itemId)
    saveProgress({
      seen: progress.seen + 1,
      correct: progress.correct + (correct ? 1 : 0),
      wrong: progress.wrong + (correct ? 0 : 1),
      wrongIds: [...wrongIds],
    })
    setSessionScore((value) => ({
      correct: value.correct + (correct ? 1 : 0),
      wrong: value.wrong + (correct ? 0 : 1),
    }))
    setChecked(true)
  }

  function nextQuestion() {
    setQuestionIndex((value) => value + 1)
    setSelected("")
    setChecked(false)
  }

  const isCorrect = currentQuestion?.accepted.includes(selected.toLowerCase())
  const title = isVerbs ? "动词三态" : "比较级与最高级"
  const subtitle = isVerbs ? "过去式 · 过去分词 · 现在完成时" : "看懂规则，练会词形，再放进句子"
  const filters = isVerbs
    ? [["all", "全部"], ["irregular", "不规则"], ["regular", "规则"]]
    : [["all", "全部"], ...Array.from(new Set(COMPARISONS.map((item) => item.rule))).map((rule) => [rule, rule])]

  return (
    <main className="wordlab-shell">
      <header className="wordlab-header">
        <button type="button" onClick={() => router.push("/grammar")} title="返回语法练习"><ArrowLeft /></button>
        <div><h1>{title}</h1><p>{subtitle}</p></div>
        <div className="wordlab-header-stat"><span>待复习</span><strong>{progress.wrongIds.length}</strong></div>
      </header>

      <nav className="wordlab-tabs" aria-label="学习模式">
        <button type="button" className={view === "learn" ? "active" : ""} onClick={() => switchView("learn")}><BookOpen />学习词表</button>
        <button type="button" className={view === "practice" ? "active" : ""} onClick={() => switchView("practice")}><Trophy />专项练习</button>
        <button type="button" className={view === "wrong" ? "active" : ""} onClick={() => switchView("wrong")}><ListRestart />错题复习 <span>{progress.wrongIds.length}</span></button>
      </nav>

      {view === "learn" ? (
        <>
          <section className="wordlab-toolbar">
            <label><Search /><input value={search} onChange={(event) => { setSearch(event.target.value); setPage(0) }} placeholder={isVerbs ? "搜索动词或中文" : "搜索形容词或中文"} /></label>
            <div className="wordlab-filter">
              {filters.map(([value, label]) => <button key={value} type="button" className={filter === value ? "active" : ""} onClick={() => { setFilter(value); setPage(0) }}>{label}</button>)}
            </div>
          </section>
          <div className="wordlab-count">共 {filteredItems.length} 个词，点击喇叭听英式发音</div>
          <section className={`wordlab-grid ${isVerbs ? "wordlab-grid-tense" : ""}`}>
            {visibleItems.map((item) => isVerbs
              ? <VerbCard key={item.id} item={item as VerbForm} speaking={speaking} onSpeak={speak} />
              : <ComparisonCard key={item.id} item={item as ComparisonForm} speaking={speaking} onSpeak={speak} />)}
          </section>
          {filteredItems.length === 0 && <div className="wordlab-empty"><Search /><strong>没有找到相关单词</strong></div>}
          {pageCount > 1 && <div className="wordlab-pagination"><button type="button" disabled={page === 0} onClick={() => setPage((value) => value - 1)}><ChevronLeft /></button><span>{page + 1} / {pageCount}</span><button type="button" disabled={page + 1 >= pageCount} onClick={() => setPage((value) => value + 1)}><ChevronRight /></button></div>}
        </>
      ) : questions.length === 0 ? (
        <section className="wordlab-empty">
          <CheckCircle2 />
          <h2>{view === "wrong" ? "目前没有错题" : "题目准备中"}</h2>
          <p>{view === "wrong" ? "完成专项练习后，答错的单词会自动来到这里。" : "请重新开始一组练习。"}</p>
          <button type="button" onClick={() => switchView(view === "wrong" ? "practice" : view)}>{view === "wrong" ? "开始专项练习" : "重新开始"}</button>
        </section>
      ) : sessionDone ? (
        <section className="wordlab-result">
          <div className="wordlab-result-icon"><Trophy /></div>
          <h2>完成这一组练习</h2>
          <div><span><strong>{sessionScore.correct}</strong>答对</span><span><strong>{sessionScore.wrong}</strong>待巩固</span></div>
          <button type="button" onClick={() => buildQuestions(view === "wrong" ? "wrong" : "practice")}><ListRestart />再练一组</button>
          <button type="button" className="secondary" onClick={() => switchView("learn")}>返回学习词表</button>
        </section>
      ) : currentQuestion ? (
        <section className="wordlab-practice">
          <div className="wordlab-practice-top"><span>{currentQuestion.formLabel}</span><strong>{questionIndex + 1} / {questions.length}</strong></div>
          <div className="wordlab-progress"><i style={{ width: `${((questionIndex + 1) / questions.length) * 100}%` }} /></div>
          <div className="wordlab-question">
            <p>{currentQuestion.instruction}</p>
            <div><h2>{currentQuestion.prompt}</h2><AudioButton text={checked ? currentQuestion.completeSentence : currentQuestion.prompt} itemId={currentQuestion.key} role="sentence" speaking={speaking} onSpeak={speak} label={checked ? "完整正确句子" : "带停顿的题干"} /></div>
          </div>
          <div className="wordlab-options">
            {currentQuestion.options.map((option) => {
              const optionCorrect = currentQuestion.accepted.includes(option.toLowerCase())
              const className = checked
                ? optionCorrect ? "correct" : selected === option ? "wrong" : ""
                : selected === option ? "selected" : ""
              return <button key={option} type="button" disabled={checked} className={className} onClick={() => setSelected(option)}><span>{option}</span></button>
            })}
          </div>
          {checked && <div className={`wordlab-feedback ${isCorrect ? "correct" : "wrong"}`}>
            <div>{isCorrect ? <Check /> : <X />}<strong>{isCorrect ? "答对了" : `正确答案：${currentQuestion.accepted.join(" / ")}`}</strong></div>
            <p>{currentQuestion.completeSentence}</p>
            {currentQuestion.translation && <p className="wordlab-feedback-translation">{currentQuestion.translation}</p>}
            <small>{isVerbs ? "释义和完整三态可在下方查看。" : "记住词形变化，也要留意 than 和 the。"}</small>
            <div className="wordlab-feedback-forms">
              {isVerbs ? (() => { const item = VERBS.find((value) => value.id === currentQuestion.itemId)!; return <><span>原形 <b>{item.base}</b></span><span>过去式 <b>{item.past}</b></span><span>过去分词 <b>{item.participle}</b></span><span>中文 <b>{item.zh}</b></span></> })()
                : (() => { const item = COMPARISONS.find((value) => value.id === currentQuestion.itemId)!; return <><span>原级 <b>{item.base}</b></span><span>比较级 <b>{item.comparative}</b></span><span>最高级 <b>{item.superlative}</b></span><span>中文 <b>{item.zh}</b></span></> })()}
            </div>
          </div>}
          {!checked ? <button type="button" className="wordlab-primary" disabled={!selected} onClick={checkAnswer}>提交答案</button> : <button type="button" className="wordlab-primary" onClick={nextQuestion}>{questionIndex + 1 === questions.length ? "查看结果" : "下一题"}<ChevronRight /></button>}
        </section>
      ) : null}
    </main>
  )
}
