"use client"

import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, BookOpen, Loader2 } from "lucide-react"
import type { ExamSeries } from "@/lib/api/speech"
import { fetchExamSeriesList } from "@/lib/api/speech"

const LEVEL_CODE = "ket"
const LEVEL_NAME = "A2 Key (KET)"

export function KetSelector() {
  const router = useRouter()
  const [allSeries, setAllSeries] = useState<ExamSeries[]>([])
  const [loading, setLoading] = useState(false)

  const loadSeries = useCallback(async () => {
    setLoading(true)
    try {
      setAllSeries(await fetchExamSeriesList())
    } catch (error) {
      console.error("[ket-selector] fetch series failed", error)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadSeries()
  }, [loadSeries])

  const ketSeries = allSeries.filter((s) => s.levelCode === LEVEL_CODE)

  function openBook(series: ExamSeries) {
    const params = new URLSearchParams({
      examSeries: "ket",
      levelCode: LEVEL_CODE,
      levelName: LEVEL_NAME,
      seriesCode: series.code,
      seriesName: series.name,
    })
    router.push(`/speech/exam?${params.toString()}`)
  }

  return (
    <div className="yle-shell">
      <header className="yle-header">
        <button type="button" className="yle-back" onClick={() => router.push("/")}>
          <ArrowLeft className="size-[18px]" />
          <span>首页</span>
        </button>
        <h1 className="yle-header-title">KET 剑桥 A2</h1>
        <div className="w-[72px]" />
      </header>

      <div className="yle-content">
        <div className="yle-content-header">
          <h2 className="yle-content-title">{LEVEL_NAME}</h2>
          <span className="yle-content-count">{ketSeries.length} 套</span>
        </div>

        {loading ? (
          <div className="yle-empty">
            <Loader2 className="size-6 animate-spin text-blue-400" />
            <span>加载中...</span>
          </div>
        ) : ketSeries.length === 0 ? (
          <div className="yle-empty">
            <BookOpen className="size-6 text-slate-300" />
            <span>暂无题库</span>
          </div>
        ) : (
          <div className="yle-book-grid">
            {ketSeries.map((series) => (
              <button
                key={series.code}
                type="button"
                className="yle-book-card"
                onClick={() => openBook(series)}
              >
                <div className="yle-book-cover">
                  {series.coverUrl ? (
                    <img src={series.coverUrl} alt={series.name} className="yle-book-img" />
                  ) : (
                    <div className="yle-book-placeholder">
                      <BookOpen className="size-8 text-blue-300" />
                      <span>{series.name}</span>
                    </div>
                  )}
                </div>
                <div className="yle-book-info">
                  <span className="yle-book-name">{series.name}</span>
                  {series.publisher && (
                    <span className="yle-book-pub">{series.publisher}</span>
                  )}
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
