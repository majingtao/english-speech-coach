"use client"

import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, ChevronRight, ImageOff, Loader2, RefreshCw, Users } from "lucide-react"
import {
  fetchExpressionDialogueTasks,
  type ExpressionDialogueTask,
} from "@/lib/api/expression"

export function ExpressionDialogueList() {
  const router = useRouter()
  const [tasks, setTasks] = useState<ExpressionDialogueTask[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  const load = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      setTasks(await fetchExpressionDialogueTasks())
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "互动题加载失败")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(load, 0)
    return () => window.clearTimeout(timer)
  }, [load])

  return (
    <main className="expression-shell">
      <header className="expression-header">
        <button type="button" className="expression-icon-btn" onClick={() => router.push("/expression")} title="返回 KET 表达">
          <ArrowLeft className="size-5" />
        </button>
        <div>
          <h1>Part 2 双人互动</h1>
          <p>选择角色，与 AI 讨论图片选项</p>
        </div>
        <button type="button" className="expression-icon-btn" onClick={load} disabled={loading} title="刷新">
          <RefreshCw className={`size-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </header>

      <section className="expression-intro">
        <Users className="size-6" />
        <div>
          <h2>练习回应，而不只是回答</h2>
          <p>进入题目后选择 Student A 或 Student B。AI 会承担另一角色，完成后评价整段互动。</p>
        </div>
      </section>

      {loading ? (
        <div className="expression-state"><Loader2 className="size-6 animate-spin" />正在加载</div>
      ) : error ? (
        <div className="expression-state expression-error"><span>{error}</span><button type="button" onClick={load}>重试</button></div>
      ) : tasks.length === 0 ? (
        <div className="expression-state">当前没有已发布的 Part 2 互动题</div>
      ) : (
        <section className="expression-dialogue-list">
          {tasks.map((task) => (
            <button
              key={task.id}
              type="button"
              className="expression-dialogue-row"
              onClick={() => router.push(`/expression/dialogue/practice?task=${task.id}`)}
            >
              <span className="expression-dialogue-thumb">
                <ImageOff className="size-5" />
                <small>{task.configJson.imageSlots.length} 图位</small>
              </span>
              <span className="expression-theme-copy">
                <strong>{task.titleCn}</strong>
                <span>{task.titleEn}</span>
                <small>{task.promptEn}</small>
              </span>
              <ChevronRight className="size-5" />
            </button>
          ))}
        </section>
      )}
    </main>
  )
}
