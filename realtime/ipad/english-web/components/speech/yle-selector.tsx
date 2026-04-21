"use client"

import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, BookOpen, Loader2 } from "lucide-react"
import type { ExamSeries } from "@/lib/api/speech"
import { fetchExamSeriesList } from "@/lib/api/speech"

type LevelKey = "starters" | "movers" | "flyers"

interface YleLevel {
  key: LevelKey
  title: string
  shortTitle: string
}

const levels: YleLevel[] = [
  { key: "starters", title: "Pre A1 Starters", shortTitle: "Starters" },
  { key: "movers", title: "A1 Movers", shortTitle: "Movers" },
  { key: "flyers", title: "A2 Flyers", shortTitle: "Flyers" },
]

export function YleSelector() {
  const router = useRouter()
  const [activeLevel, setActiveLevel] = useState<LevelKey>("flyers")
  const [allSeries, setAllSeries] = useState<ExamSeries[]>([])
  const [loading, setLoading] = useState(false)

  const currentLevel = levels.find((l) => l.key === activeLevel)!

  const loadSeries = useCallback(async () => {
    setLoading(true)
    try {
      setAllSeries(await fetchExamSeriesList())
    } catch (error) {
      console.error("[yle-selector] fetch series failed", error)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadSeries()
  }, [loadSeries])

  const currentBooks = allSeries.filter((s) => s.levelCode === activeLevel)

  function openBook(series: ExamSeries) {
    const params = new URLSearchParams({
      examSeries: "yle",
      levelCode: activeLevel,
      levelName: currentLevel.title,
      seriesCode: series.code,
      seriesName: series.name,
    })
    router.push(`/speech/exam?${params.toString()}`)
  }

  return (
    <div className="yle-shell">
      {/* Header */}
      <header className="yle-header">
        <button type="button" className="yle-back" onClick={() => router.push("/")}>
          <ArrowLeft className="size-[18px]" />
          <span>首页</span>
        </button>
        <h1 className="yle-header-title">YLE 剑桥少儿</h1>
        <div className="w-[72px]" />
      </header>

      {/* Level Tabs */}
      <div className="yle-tabs">
        {levels.map((level) => (
          <button
            key={level.key}
            type="button"
            className={`yle-tab ${activeLevel === level.key ? "yle-tab-active" : ""}`}
            onClick={() => setActiveLevel(level.key)}
          >
            {level.shortTitle}
          </button>
        ))}
      </div>

      {/* Content */}
      <div className="yle-content">
        <div className="yle-content-header">
          <h2 className="yle-content-title">{currentLevel.title}</h2>
          <span className="yle-content-count">{currentBooks.length} 套真题</span>
        </div>

        {loading ? (
          <div className="yle-empty">
            <Loader2 className="size-6 animate-spin text-blue-400" />
            <span>加载中...</span>
          </div>
        ) : currentBooks.length === 0 ? (
          <div className="yle-empty">
            <BookOpen className="size-6 text-slate-300" />
            <span>暂无真题集</span>
          </div>
        ) : (
          <div className="yle-book-grid">
            {currentBooks.map((series) => (
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
