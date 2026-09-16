"use client"

import { useCallback, useEffect, useMemo, useRef, useState } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  ChevronLeft,
  ChevronRight,
  Loader2,
  Plus,
  RefreshCw,
  Search,
  X,
} from "lucide-react"
import {
  addWordToWordbook,
  fetchVocabDetail,
  fetchVocabThemes,
  fetchWordList,
  type VocabDetail,
  type VocabListItem,
  type VocabTheme,
} from "@/lib/api/vocab"
import { stopWord } from "@/lib/vocab/audio"
import { VocabCard } from "@/components/vocab/vocab-card"

const LEVEL = "ket"
const PAGE_SIZE = 30

type OpenAfterLoad = "first" | "last" | null

function readQuery() {
  if (typeof window === "undefined") return { theme: "", q: "", page: 1 }
  const sp = new URLSearchParams(window.location.search)
  const page = Number(sp.get("page") || "1")
  return {
    theme: sp.get("theme") || "",
    q: sp.get("q") || "",
    page: Number.isFinite(page) && page > 0 ? Math.floor(page) : 1,
  }
}

function progressBadge(status?: number): { text: string; cls: string } | null {
  if (status === 2) return { text: "已掌握", cls: "wb-badge-done" }
  if (status === 1) return { text: "学习中", cls: "wb-badge-learning" }
  if (status === 0) return { text: "新学中", cls: "wb-badge-new" }
  return null
}

/** 生成页码：1 … 4 5 [6] 7 8 … 20 */
function pageItems(current: number, total: number): (number | "gap")[] {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1)
  const out: (number | "gap")[] = [1]
  const start = Math.max(2, current - 2)
  const end = Math.min(total - 1, current + 2)
  if (start > 2) out.push("gap")
  for (let i = start; i <= end; i += 1) out.push(i)
  if (end < total - 1) out.push("gap")
  out.push(total)
  return out
}

export function VocabWordList() {
  const router = useRouter()
  const initial = useMemo(readQuery, [])

  const [themes, setThemes] = useState<VocabTheme[]>([])
  const [themeCode, setThemeCode] = useState(initial.theme)
  const [keywordInput, setKeywordInput] = useState(initial.q)
  const [keyword, setKeyword] = useState(initial.q)
  const [page, setPage] = useState(initial.page)
  const [jumpInput, setJumpInput] = useState("")

  const [items, setItems] = useState<VocabListItem[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [reloadKey, setReloadKey] = useState(0)

  // 弹层
  const [openIdx, setOpenIdx] = useState<number | null>(null)
  const [flipped, setFlipped] = useState(false)
  const [details, setDetails] = useState<Record<number, VocabDetail>>({})
  const [detailError, setDetailError] = useState("")
  const [adding, setAdding] = useState(false)
  const [hint, setHint] = useState("")
  const openAfterLoadRef = useRef<OpenAfterLoad>(null)
  const listTopRef = useRef<HTMLDivElement | null>(null)
  const firstLoadRef = useRef(true)

  const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE))
  const current = openIdx != null ? items[openIdx] : undefined
  const currentDetail = current ? details[current.id] : undefined
  const globalIndex = openIdx != null ? (page - 1) * PAGE_SIZE + openIdx + 1 : 0

  // 主题列表（失败不影响词表）
  useEffect(() => {
    fetchVocabThemes(LEVEL)
      .then((list) => setThemes((list || []).slice().sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))))
      .catch(() => setThemes([]))
  }, [])

  // 搜索框防抖
  useEffect(() => {
    const kw = keywordInput.trim()
    if (kw === keyword) return
    const timer = setTimeout(() => {
      setKeyword(kw)
      setPage(1)
    }, 300)
    return () => clearTimeout(timer)
  }, [keywordInput, keyword])

  // 同步到地址栏，刷新/返回后停在同一页
  useEffect(() => {
    const sp = new URLSearchParams()
    if (themeCode) sp.set("theme", themeCode)
    if (keyword) sp.set("q", keyword)
    if (page > 1) sp.set("page", String(page))
    const qs = sp.toString()
    window.history.replaceState(null, "", qs ? `/vocab/wordlist?${qs}` : "/vocab/wordlist")
  }, [themeCode, keyword, page])

  // 拉取当前页
  useEffect(() => {
    let aborted = false
    setLoading(true)
    setError("")
    fetchWordList({ level: LEVEL, themeCode, keyword, pageNo: page, pageSize: PAGE_SIZE })
      .then((res) => {
        if (aborted) return
        const pages = Math.max(1, Math.ceil(res.total / PAGE_SIZE))
        if (page > pages && res.total > 0) {
          setPage(pages)
          return
        }
        setItems(res.list)
        setTotal(res.total)
        const pending = openAfterLoadRef.current
        openAfterLoadRef.current = null
        if (pending && res.list.length > 0) {
          setOpenIdx(pending === "first" ? 0 : res.list.length - 1)
          setFlipped(false)
        } else if (!pending && !firstLoadRef.current) {
          listTopRef.current?.scrollIntoView({ block: "start", behavior: "smooth" })
        }
        firstLoadRef.current = false
      })
      .catch((e: unknown) => {
        if (aborted) return
        openAfterLoadRef.current = null
        setError(e instanceof Error ? e.message : "加载失败")
      })
      .finally(() => {
        if (!aborted) setLoading(false)
      })
    return () => {
      aborted = true
    }
  }, [themeCode, keyword, page, reloadKey])

  // 打开卡片时懒加载详情（首次会触发后端生成释义，可能稍慢）
  useEffect(() => {
    if (!current || details[current.id]) return
    let aborted = false
    setDetailError("")
    fetchVocabDetail(current.id)
      .then((d) => {
        if (!aborted) setDetails((prev) => ({ ...prev, [current.id]: d }))
      })
      .catch((e: unknown) => {
        if (!aborted) setDetailError(e instanceof Error ? e.message : "释义加载失败")
      })
    return () => {
      aborted = true
    }
  }, [current, details])

  useEffect(() => () => stopWord(), [])

  const closeCard = useCallback(() => {
    stopWord()
    setOpenIdx(null)
    setFlipped(false)
    setHint("")
    setDetailError("")
  }, [])

  const openCard = useCallback((idx: number) => {
    stopWord()
    setOpenIdx(idx)
    setFlipped(false)
    setHint("")
  }, [])

  const hasPrev = openIdx != null && (openIdx > 0 || page > 1)
  const hasNext = openIdx != null && (openIdx < items.length - 1 || page < totalPages)

  const goPrev = useCallback(() => {
    if (openIdx == null || loading) return
    if (openIdx > 0) return openCard(openIdx - 1)
    if (page > 1) {
      stopWord()
      openAfterLoadRef.current = "last"
      setPage(page - 1)
    }
  }, [openIdx, loading, page, openCard])

  const goNext = useCallback(() => {
    if (openIdx == null || loading) return
    if (openIdx < items.length - 1) return openCard(openIdx + 1)
    if (page < totalPages) {
      stopWord()
      openAfterLoadRef.current = "first"
      setPage(page + 1)
    }
  }, [openIdx, loading, items.length, page, totalPages, openCard])

  // 键盘：Esc 关闭，←/→ 切换，空格翻面
  useEffect(() => {
    if (openIdx == null) return
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") closeCard()
      else if (e.key === "ArrowLeft") goPrev()
      else if (e.key === "ArrowRight") goNext()
      else if (e.key === " ") {
        e.preventDefault()
        setFlipped((v) => !v)
      }
    }
    window.addEventListener("keydown", onKey)
    return () => window.removeEventListener("keydown", onKey)
  }, [openIdx, closeCard, goPrev, goNext])

  function changeTheme(code: string) {
    if (code === themeCode) return
    setThemeCode(code)
    setPage(1)
  }

  function gotoPage(p: number) {
    const next = Math.min(Math.max(1, p), totalPages)
    if (next !== page) setPage(next)
  }

  async function handleAdd() {
    if (!current || adding) return
    setAdding(true)
    setHint("")
    try {
      const added = await addWordToWordbook(current.id)
      setHint(added === 0 ? `「${current.word}」已经在生词本里啦` : `已把「${current.word}」加入生词本`)
    } catch (e: unknown) {
      setHint(e instanceof Error ? e.message : "加入失败")
    } finally {
      setAdding(false)
    }
  }

  const activeTheme = themes.find((t) => t.code === themeCode)

  return (
    <main className="vocab-shell">
      <header className="vocab-header">
        <button type="button" className="vocab-back" onClick={() => router.push("/")}>
          <ArrowLeft className="size-[18px]" />
          <span>首页</span>
        </button>
        <h1 className="vocab-header-title">KET 词表</h1>
        <button
          type="button"
          className="vocab-refresh"
          disabled={loading}
          onClick={() => setReloadKey((k) => k + 1)}
          title="刷新"
        >
          <RefreshCw className={`size-[16px] ${loading ? "animate-spin" : ""}`} />
        </button>
      </header>

      <div className="wl-toolbar" ref={listTopRef}>
        <div className="wb-search-box">
          <Search className="size-[18px] wb-search-icon" />
          <input
            className="wb-search-input"
            type="search"
            inputMode="text"
            autoComplete="off"
            autoCapitalize="off"
            placeholder="按单词开头搜索，如 app"
            value={keywordInput}
            onChange={(e) => setKeywordInput(e.target.value)}
          />
          {keywordInput && (
            <button
              type="button"
              className="wl-search-clear"
              aria-label="清空"
              onClick={() => setKeywordInput("")}
            >
              <X className="size-4" />
            </button>
          )}
        </div>

        {themes.length > 0 && (
          <div className="wl-themes" role="tablist" aria-label="主题场景">
            <button
              type="button"
              role="tab"
              aria-selected={!themeCode}
              className={`wl-theme-chip ${!themeCode ? "is-active" : ""}`}
              onClick={() => changeTheme("")}
            >
              全部
            </button>
            {themes.map((t) => (
              <button
                key={t.code}
                type="button"
                role="tab"
                aria-selected={themeCode === t.code}
                className={`wl-theme-chip ${themeCode === t.code ? "is-active" : ""}`}
                onClick={() => changeTheme(t.code)}
                title={t.nameEn}
              >
                {t.nameCn}
              </button>
            ))}
          </div>
        )}

        <p className="wl-summary">
          {activeTheme ? `「${activeTheme.nameCn}」` : "全部单词"}
          {keyword ? ` · 以 “${keyword}” 开头` : ""}
          {` · 共 ${total} 个`}
          {total > 0 ? ` · 第 ${page}/${totalPages} 页` : ""}
        </p>
      </div>

      <section className="wl-list-section">
        {loading && items.length === 0 ? (
          <div className="vocab-review-empty">
            <Loader2 className="size-6 animate-spin text-blue-400" />
            <span>加载中…</span>
          </div>
        ) : error ? (
          <div className="vocab-review-empty vocab-review-error">
            <span>{error}</span>
            <button type="button" className="vocab-review-retry" onClick={() => setReloadKey((k) => k + 1)}>
              重试
            </button>
          </div>
        ) : items.length === 0 ? (
          <div className="vocab-review-empty">
            <span>{keyword || themeCode ? "没有找到符合条件的单词" : "词库里还没有单词"}</span>
          </div>
        ) : (
          <ul className={`wl-grid ${loading ? "is-loading" : ""}`}>
            {items.map((v, i) => {
              const badge = progressBadge(v.progressStatus)
              return (
                <li key={v.id}>
                  <button type="button" className="wl-card" onClick={() => openCard(i)}>
                    <span className="wl-card-top">
                      <span className="wl-card-word">{v.word}</span>
                      {v.masteryLevel === 2 && <span className="wl-tag-spell" title="四会词：需要会拼写">四会</span>}
                    </span>
                    <span className="wl-card-meta">
                      {v.pos && <span className="wl-card-pos">{v.pos}</span>}
                      {v.ipa && <span className="wl-card-ipa">{v.ipa}</span>}
                    </span>
                    <span className="wl-card-cn">{v.definitionCn || "点击查看释义"}</span>
                    {badge && <span className={`wb-badge wl-card-badge ${badge.cls}`}>{badge.text}</span>}
                  </button>
                </li>
              )
            })}
          </ul>
        )}

        {totalPages > 1 && !error && (
          <nav className="wl-pager" aria-label="分页">
            <button
              type="button"
              className="wl-page-btn"
              disabled={page <= 1 || loading}
              onClick={() => gotoPage(page - 1)}
            >
              <ChevronLeft className="size-4" />
              上一页
            </button>
            <div className="wl-page-nums">
              {pageItems(page, totalPages).map((p, i) =>
                p === "gap" ? (
                  <span key={`gap-${i}`} className="wl-page-gap">…</span>
                ) : (
                  <button
                    key={p}
                    type="button"
                    className={`wl-page-num ${p === page ? "is-active" : ""}`}
                    disabled={loading}
                    aria-current={p === page ? "page" : undefined}
                    onClick={() => gotoPage(p)}
                  >
                    {p}
                  </button>
                ),
              )}
            </div>
            <button
              type="button"
              className="wl-page-btn"
              disabled={page >= totalPages || loading}
              onClick={() => gotoPage(page + 1)}
            >
              下一页
              <ChevronRight className="size-4" />
            </button>
            <form
              className="wl-page-jump"
              onSubmit={(e) => {
                e.preventDefault()
                const n = Number(jumpInput)
                if (Number.isFinite(n) && n > 0) gotoPage(Math.floor(n))
                setJumpInput("")
              }}
            >
              <input
                type="number"
                inputMode="numeric"
                min={1}
                max={totalPages}
                placeholder="页码"
                value={jumpInput}
                onChange={(e) => setJumpInput(e.target.value)}
              />
              <button type="submit" disabled={!jumpInput || loading}>跳转</button>
            </form>
          </nav>
        )}
      </section>

      {current && (
        <div className="wl-modal" role="dialog" aria-modal="true" aria-label={current.word} onClick={closeCard}>
          <div className="wl-modal-panel" onClick={(e) => e.stopPropagation()}>
            <div className="wl-modal-head">
              <span className="vocab-review-pill">
                {globalIndex} / {total}
              </span>
              <button type="button" className="wl-modal-close" aria-label="关闭" onClick={closeCard}>
                <X className="size-5" />
              </button>
            </div>

            <VocabCard
              key={current.id}
              id={current.id}
              word={current.word}
              detail={currentDetail}
              flipped={flipped}
              onToggle={() => setFlipped((v) => !v)}
            />

            {detailError && !currentDetail && <p className="wl-modal-hint is-error">{detailError}</p>}
            {hint && <p className="wl-modal-hint">{hint}</p>}

            <div className="wl-modal-actions">
              <button type="button" className="wl-nav-btn" disabled={!hasPrev || loading} onClick={goPrev}>
                <ChevronLeft className="size-5" />
                上一个
              </button>
              <button type="button" className="wl-add-btn" disabled={adding} onClick={handleAdd}>
                {adding ? <Loader2 className="size-4 animate-spin" /> : <Plus className="size-4" />}
                加入生词本
              </button>
              <button type="button" className="wl-nav-btn" disabled={!hasNext || loading} onClick={goNext}>
                下一个
                <ChevronRight className="size-5" />
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  )
}
