"use client"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, BookOpenCheck, ChevronRight, Loader2 } from "lucide-react"
import { fetchGrammarPoints, type GrammarPoint } from "@/lib/api/grammar"

export function GrammarTopics() {
  const router = useRouter(); const [points, setPoints] = useState<GrammarPoint[]>([]); const [loading, setLoading] = useState(true); const [error, setError] = useState("")
  useEffect(() => { fetchGrammarPoints().then(setPoints).catch((e) => setError(e instanceof Error ? e.message : "知识点加载失败")).finally(() => setLoading(false)) }, [])
  return <main className="grammar-shell">
    <header className="grammar-topbar"><button type="button" onClick={() => router.push("/")} title="返回首页"><ArrowLeft /></button><div><h1>KET 语法练习</h1><p>先理解规则，再练习使用</p></div></header>
    {loading ? <div className="grammar-state"><Loader2 className="animate-spin" />正在加载知识点</div> : error ? <div className="grammar-state grammar-error">{error}</div> :
      <section className="grammar-topic-list">{points.map((point, i) => <button key={point.id} type="button" className="grammar-topic-row" onClick={() => router.push(`/grammar/practice?point=${point.id}&name=${encodeURIComponent(point.nameCn)}`)}>
        <span className="grammar-topic-index">{String(i + 1).padStart(2, "0")}</span><span className="grammar-topic-copy"><strong>{point.nameCn}</strong><em>{point.nameEn}</em><small>{point.description}</small></span><span className="grammar-topic-action"><BookOpenCheck /><ChevronRight /></span>
      </button>)}</section>}
  </main>
}
