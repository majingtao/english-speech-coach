"use client"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, BookOpenCheck, ChevronRight, GitCompareArrows, Loader2, Repeat2 } from "lucide-react"
import { fetchGrammarPoints, type GrammarPoint } from "@/lib/api/grammar"

export function GrammarTopics() {
  const router = useRouter(); const [points, setPoints] = useState<GrammarPoint[]>([]); const [loading, setLoading] = useState(true); const [error, setError] = useState("")
  useEffect(() => { fetchGrammarPoints().then(setPoints).catch((e) => setError(e instanceof Error ? e.message : "知识点加载失败")).finally(() => setLoading(false)) }, [])
  return <main className="grammar-shell">
    <header className="grammar-topbar"><button type="button" onClick={() => router.push("/")} title="返回首页"><ArrowLeft /></button><div><h1>KET 语法练习</h1><p>先理解规则，再练习使用</p></div></header>
    <section className="grammar-featured-topics">
      <button type="button" onClick={() => router.push("/grammar/verbs")}>
        <span className="grammar-featured-icon"><Repeat2 /></span>
        <span><strong>动词三态</strong><em>原形 · 过去式 · 过去分词</em><small>含一般过去时、现在完成时和英式发音</small></span>
        <ChevronRight />
      </button>
      <button type="button" onClick={() => router.push("/grammar/comparisons")}>
        <span className="grammar-featured-icon comparison"><GitCompareArrows /></span>
        <span><strong>比较级与最高级</strong><em>tall · taller · tallest</em><small>按变化规则学习常用形容词</small></span>
        <ChevronRight />
      </button>
    </section>
    {loading ? <div className="grammar-state"><Loader2 className="animate-spin" />正在加载其他知识点</div> : error ? <div className="grammar-state grammar-error">{error}</div> :
      <><h2 className="grammar-more-title">其他语法知识点</h2><section className="grammar-topic-list">{points.map((point, i) => <button key={point.id} type="button" className="grammar-topic-row" onClick={() => router.push(`/grammar/practice?point=${point.id}&name=${encodeURIComponent(point.nameCn)}`)}>
        <span className="grammar-topic-index">{String(i + 1).padStart(2, "0")}</span><span className="grammar-topic-copy"><strong>{point.nameCn}</strong><em>{point.nameEn}</em><small>{point.description}</small></span><span className="grammar-topic-action"><BookOpenCheck /><ChevronRight /></span>
      </button>)}</section></>}
  </main>
}
