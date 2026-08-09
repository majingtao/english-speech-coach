"use client"

import { useCallback, useEffect, useRef, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, Check, Loader2, Plus, RefreshCw, Search, Trash2 } from "lucide-react"
import {
  addWordToWordbook,
  fetchWordbook,
  removeWordFromWordbook,
  searchVocab,
  type VocabListItem,
} from "@/lib/api/vocab"

const LEVEL = "ket"

function statusLabel(status?: number): { text: string; cls: string } {
  switch (status) {
    case 2:
      return { text: "已掌握", cls: "wb-badge-done" }
    case 1:
      return { text: "学习中", cls: "wb-badge-learning" }
    case 0:
      return { text: "新学中", cls: "wb-badge-new" }
    default:
      return { text: "待加入", cls: "wb-badge-pending" }
  }
}

export function VocabWordbook() {
  const router = useRouter()
  const [items, setItems] = useState<VocabListItem[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")

  const [keyword, setKeyword] = useState("")
  const [suggests, setSuggests] = useState<VocabListItem[]>([])
  const [searching, setSearching] = useState(false)
  const [searched, setSearched] = useState(false)
  const [addingId, setAddingId] = useState<number | null>(null)
  const [removingId, setRemovingId] = useState<number | null>(null)
  const [hint, setHint] = useState("")

  // 竞态保护：只认最后一次输入对应的搜索结果
  const seqRef = useRef(0)
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      const list = await fetchWordbook()
      setItems(list || [])
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "加载失败")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    const kw = keyword.trim()
    if (debounceRef.current) clearTimeout(debounceRef.current)
    if (!kw) {
      setSuggests([])
      setSearched(false)
      setSearching(false)
      return
    }
    setSearching(true)
    const seq = ++seqRef.current
    debounceRef.current = setTimeout(async () => {
      try {
        const res = await searchVocab(kw, LEVEL, 10)
        if (seq !== seqRef.current) return
        setSuggests(res || [])
      } catch {
        if (seq !== seqRef.current) return
        setSuggests([])
      } finally {
        if (seq === seqRef.current) {
          setSearching(false)
          setSearched(true)
        }
      }
    }, 250)
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current)
    }
  }, [keyword])

  const inWordbook = useCallback(
    (id: number) => items.some((it) => it.id === id),
    [items],
  )

  async function handleAdd(v: VocabListItem) {
    if (addingId) return
    setHint("")
    setAddingId(v.id)
    try {
      const added = await addWordToWordbook(v.id)
      if (added === 0) {
        setHint(`「${v.word}」已经在生词本里啦`)
      } else {
        setHint(`已加入「${v.word}」`)
      }
      setKeyword("")
      setSuggests([])
      setSearched(false)
      await load()
    } catch (e: unknown) {
      setHint(e instanceof Error ? e.message : "加入失败")
    } finally {
      setAddingId(null)
    }
  }

  async function handleRemove(v: VocabListItem) {
    if (removingId) return
    setRemovingId(v.id)
    try {
      await removeWordFromWordbook(v.id)
      setItems((prev) => prev.filter((it) => it.id !== v.id))
    } catch (e: unknown) {
      setHint(e instanceof Error ? e.message : "移除失败")
    } finally {
      setRemovingId(null)
    }
  }

  const kw = keyword.trim()

  return (
    <main className="vocab-shell">
      <header className="vocab-header">
        <button
          type="button"
          className="vocab-back"
          onClick={() => router.push("/vocab")}
        >
          <ArrowLeft className="size-[18px]" />
          <span>词汇</span>
        </button>
        <h1 className="vocab-header-title">我的生词本</h1>
        <button
          type="button"
          className="vocab-refresh"
          disabled={loading}
          onClick={load}
          title="刷新"
        >
          <RefreshCw className={`size-[16px] ${loading ? "animate-spin" : ""}`} />
        </button>
      </header>

      <p className="wb-intro">
        输入孩子不会的单词加入生词本（只能加词库里已有的词）。系统每天会自动从这里挑最多 50 个新词进入背诵计划。
      </p>

      <div className="wb-search">
        <div className="wb-search-box">
          <Search className="size-[18px] wb-search-icon" />
          <input
            className="wb-search-input"
            type="text"
            inputMode="text"
            autoComplete="off"
            autoCapitalize="off"
            placeholder="输入英文单词，如 apple"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          {searching && <Loader2 className="size-4 animate-spin wb-search-spin" />}
        </div>

        {kw && (
          <div className="wb-suggest">
            {suggests.length > 0 ? (
              suggests.map((v) => {
                const added = inWordbook(v.id)
                return (
                  <button
                    key={v.id}
                    type="button"
                    className="wb-suggest-row"
                    disabled={added || addingId != null}
                    onClick={() => handleAdd(v)}
                  >
                    <span className="wb-suggest-word">{v.word}</span>
                    {v.pos && <span className="wb-suggest-pos">{v.pos}</span>}
                    <span className="wb-suggest-action">
                      {added ? (
                        <>
                          <Check className="size-4" /> 已在
                        </>
                      ) : addingId === v.id ? (
                        <Loader2 className="size-4 animate-spin" />
                      ) : (
                        <>
                          <Plus className="size-4" /> 加入
                        </>
                      )}
                    </span>
                  </button>
                )
              })
            ) : searched && !searching ? (
              <p className="wb-suggest-empty">
                词库里没有「{kw}」，换个词或检查拼写试试
              </p>
            ) : null}
          </div>
        )}
      </div>

      {hint && <p className="wb-hint">{hint}</p>}

      <section className="wb-list-section">
        <div className="wb-list-head">
          <h3 className="vocab-section-title">生词本（{items.length}）</h3>
        </div>

        {loading ? (
          <div className="vocab-review-empty">
            <Loader2 className="size-6 animate-spin text-blue-400" />
            <span>加载中…</span>
          </div>
        ) : error ? (
          <div className="vocab-review-empty vocab-review-error">
            <span>{error}</span>
            <button type="button" className="vocab-review-retry" onClick={load}>
              重试
            </button>
          </div>
        ) : items.length === 0 ? (
          <div className="vocab-review-empty">
            <span>生词本还是空的，上面输入单词加进来吧 ✏️</span>
          </div>
        ) : (
          <ul className="wb-list">
            {items.map((v) => {
              const badge = statusLabel(v.progressStatus)
              return (
                <li key={v.id} className="wb-item">
                  <div className="wb-item-main">
                    <span className="wb-item-word">{v.word}</span>
                    {v.pos && <span className="wb-item-pos">{v.pos}</span>}
                  </div>
                  <span className={`wb-badge ${badge.cls}`}>{badge.text}</span>
                  <button
                    type="button"
                    className="wb-item-del"
                    disabled={removingId != null}
                    onClick={() => handleRemove(v)}
                    title="移除"
                  >
                    {removingId === v.id ? (
                      <Loader2 className="size-4 animate-spin" />
                    ) : (
                      <Trash2 className="size-4" />
                    )}
                  </button>
                </li>
              )
            })}
          </ul>
        )}
      </section>
    </main>
  )
}
