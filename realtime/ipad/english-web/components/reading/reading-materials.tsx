"use client"

import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, Eye, EyeOff, Headphones, Loader2, RefreshCw, Settings, Volume2 } from "lucide-react"
import {
  fetchReadingMaterialPage,
  fetchReadingTags,
  submitReadingSelfCheck,
  type NormalizedReadingMaterial,
  type ReadingWordForms,
} from "@/lib/api/reading-material"
import { useAiConfig } from "@/lib/exam/use-ai-config"
import { speakWithServer, speakWithSystem, stopTts, unlockAudio } from "@/lib/exam/tts"
import { playVocab, stopWord } from "@/lib/vocab/audio"
import type { VocabAccent } from "@/lib/api/vocab"
import { AiSettingsPanel } from "@/components/ai/ai-settings-panel"

const typeLabels: Record<string, string> = {
  word: "单词",
  phrase: "短语",
  sentence: "句子",
}

const formLabels: Array<[keyof ReadingWordForms, string]> = [
  ["base", "原型"],
  ["plural", "复数"],
  ["thirdPerson", "三单"],
  ["pastTense", "过去式"],
  ["pastParticiple", "过去分词"],
  ["presentParticiple", "现在分词"],
  ["comparative", "比较级"],
  ["superlative", "最高级"],
]

const DEFAULT_PAGE_SIZE = 100
const KET_SPELL_PAGE_SIZE = 50
type ReadingMode = "follow" | "translate" | "spell"
type PriorityFilter = "" | "mustKnow" | "highFrequency" | "mustSpell"
type ReadingVariant = "ketSpell" | "reading"

interface ReadingMaterialsProps {
  variant?: ReadingVariant
}

export function ReadingMaterials({ variant = "reading" }: ReadingMaterialsProps) {
  const router = useRouter()
  const config = useAiConfig()
  const spellOnly = variant === "ketSpell"
  const pageSize = spellOnly ? KET_SPELL_PAGE_SIZE : DEFAULT_PAGE_SIZE
  const [items, setItems] = useState<NormalizedReadingMaterial[]>([])
  const [total, setTotal] = useState(0)
  const [pageNo, setPageNo] = useState(1)
  const [tags, setTags] = useState<string[]>([])
  const [activeTag, setActiveTag] = useState("")
  const [activeType, setActiveType] = useState("")
  const [activePriority, setActivePriority] = useState<PriorityFilter>(spellOnly ? "mustSpell" : "")
  const [readingMode, setReadingMode] = useState<ReadingMode>(spellOnly ? "spell" : "follow")
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [speakingText, setSpeakingText] = useState("")
  const [loadingText, setLoadingText] = useState("")
  const [ttsLoading, setTtsLoading] = useState(false)
  const [expandedIds, setExpandedIds] = useState<Set<number>>(() => new Set())
  const [checkingKey, setCheckingKey] = useState("")

  const hasMore = items.length < total
  const visibleItems = readingMode === "translate"
    ? items.filter((item) => item.textCn?.trim())
    : items
  const countText = readingMode === "translate"
    ? `已显示 ${visibleItems.length} 条（已加载 ${items.length} 条）`
    : `已显示 ${items.length} / ${total}`

  const load = useCallback(async (nextPage = 1, append = false) => {
    setLoading(true)
    setError("")
    try {
      const [page, tagList] = await Promise.all([
        fetchReadingMaterialPage({
          level: "ket",
          materialType: activeType || undefined,
          tag: activeTag || undefined,
          priority: spellOnly ? "mustSpell" : activePriority || undefined,
          pageNo: nextPage,
          pageSize,
        }),
        fetchReadingTags("ket"),
      ])
      setItems((current) => append ? [...current, ...page.list] : page.list)
      setTotal(page.total)
      setPageNo(nextPage)
      setTags(tagList)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "自由跟读素材加载失败")
    } finally {
      setLoading(false)
    }
  }, [activePriority, activeTag, activeType, pageSize, spellOnly])

  useEffect(() => {
    const timer = window.setTimeout(() => load(1, false), 0)
    return () => {
      window.clearTimeout(timer)
      stopTts()
      stopWord()
    }
  }, [load])

  async function speakMaterial(item: NormalizedReadingMaterial, accent: VocabAccent = "us") {
    if (item.materialType === "word" && item.vocabId) {
      const audioKey = `${item.id}:${accent}`
      setError("")
      setSpeakingText(audioKey)
      setLoadingText(audioKey)
      try {
        await playVocab(
          {
            id: item.vocabId,
            word: item.vocabWord || item.textEn,
            url: accent === "uk" ? item.audioUkUrl : item.audioUsUrl,
            accent,
          },
          {
            onLoadingChange: (loading) => setLoadingText(loading ? audioKey : ""),
            onSpeakingChange: (speaking) => {
              if (!speaking) setSpeakingText("")
            },
          },
        )
      } catch (e: unknown) {
        setError(e instanceof Error ? e.message : "语音播放失败")
      } finally {
        setSpeakingText("")
        setLoadingText("")
        setTtsLoading(false)
      }
      return
    }
    await speak(item.textEn)
  }

  async function speak(text: string) {
    if (!config.ttsEnabled || !text.trim()) return
    unlockAudio()
    setError("")
    setSpeakingText(text)
    setLoadingText(text)
    try {
      if (config.ttsEngine === "system") {
        await speakWithSystem(text, config.selectedVoice)
      } else {
        await speakWithServer(text, config.ttsEngine, config.selectedVoice, {
          onLoadingChange: (loading) => {
            setTtsLoading(loading)
            setLoadingText(loading ? text : "")
          },
          onSpeakingChange: (speaking) => {
            if (!speaking) setSpeakingText("")
          },
        })
      }
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "语音播放失败")
    } finally {
      setSpeakingText("")
      setLoadingText("")
      setTtsLoading(false)
    }
  }

  function toggleExpanded(id: number) {
    setExpandedIds((current) => {
      const next = new Set(current)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  function changeType(type: string) {
    setActiveType(type)
    setExpandedIds(new Set())
  }

  function changeTag(tag: string) {
    setActiveTag(tag)
    setExpandedIds(new Set())
  }

  function changePriority(priority: PriorityFilter) {
    if (spellOnly) return
    setActivePriority(priority)
    setExpandedIds(new Set())
  }

  function changeMode(mode: ReadingMode) {
    if (spellOnly) return
    setReadingMode(mode)
    setExpandedIds(new Set())
  }

  async function markSelfCheck(item: NormalizedReadingMaterial, result: "correct" | "wrong") {
    const mode = readingMode === "spell" ? "spell" : "read"
    const key = `${item.id}:${mode}:${result}`
    setCheckingKey(key)
    setError("")
    try {
      const progress = await submitReadingSelfCheck(item.id, result, mode)
      setItems((current) => current.map((entry) => entry.id === item.id
        ? {
            ...entry,
            readCorrectCount: progress.readCorrectCount,
            readWrongCount: progress.readWrongCount,
            spellCorrectCount: progress.spellCorrectCount,
            spellWrongCount: progress.spellWrongCount,
            lastMode: progress.lastMode,
            lastResult: progress.lastResult,
            lastPracticeAt: progress.lastPracticeAt,
          }
        : entry))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "自评提交失败")
    } finally {
      setCheckingKey("")
    }
  }

  return (
    <main className="reading-shell">
      <header className="reading-header">
        <button type="button" className="reading-icon-btn" onClick={() => router.push("/")} title="返回首页">
          <ArrowLeft className="size-5" />
        </button>
        <div>
          <h1>{spellOnly ? "KET必默" : "自由跟读"}</h1>
          <p>{spellOnly ? "看中文或听音频，默写英文" : "点播放，跟着读"}</p>
        </div>
        <div className="reading-header-actions">
          <button type="button" className="reading-icon-btn" onClick={() => setSettingsOpen((open) => !open)} title="语音设置">
            <Settings className="size-5" />
          </button>
          <button type="button" className="reading-icon-btn" onClick={() => load(1, false)} disabled={loading} title="刷新">
            <RefreshCw className={`size-4 ${loading ? "animate-spin" : ""}`} />
          </button>
        </div>
      </header>

      {settingsOpen && (
        <section className="reading-settings" aria-label="语音设置">
          <div className="reading-settings-head">
            <strong>语音设置</strong>
            {(speakingText || ttsLoading) && <button type="button" onClick={stopTts}>停止播放</button>}
          </div>
          <AiSettingsPanel config={config} />
        </section>
      )}

      {!spellOnly && (
        <section className="reading-filters" aria-label="练习模式">
          <button type="button" className={readingMode === "follow" ? "active" : ""} onClick={() => changeMode("follow")}>跟读</button>
          <button type="button" className={readingMode === "translate" ? "active" : ""} onClick={() => changeMode("translate")}>中译英</button>
        </section>
      )}

      <section className="reading-filters" aria-label="筛选">
        <button type="button" className={!activeType ? "active" : ""} onClick={() => changeType("")}>全部</button>
        {Object.entries(typeLabels).map(([type, label]) => (
          <button key={type} type="button" className={activeType === type ? "active" : ""} onClick={() => changeType(type)}>{label}</button>
        ))}
      </section>

      {!spellOnly && (
        <section className="reading-filters" aria-label="重点筛选">
          <button type="button" className={!activePriority ? "active" : ""} onClick={() => changePriority("")}>全部重点</button>
          <button type="button" className={activePriority === "mustKnow" ? "active" : ""} onClick={() => changePriority("mustKnow")}>必会</button>
          <button type="button" className={activePriority === "highFrequency" ? "active" : ""} onClick={() => changePriority("highFrequency")}>高频</button>
        </section>
      )}

      {tags.length > 0 && (
        <section className="reading-tags" aria-label="标签">
          <button type="button" className={!activeTag ? "active" : ""} onClick={() => changeTag("")}>全部标签</button>
          {tags.map((tag) => (
            <button key={tag} type="button" className={activeTag === tag ? "active" : ""} onClick={() => changeTag(tag)}>{tag}</button>
          ))}
        </section>
      )}

      {loading ? (
        <div className="reading-state"><Loader2 className="size-6 animate-spin" />正在加载</div>
      ) : error ? (
        <div className="reading-state reading-error"><span>{error}</span><button type="button" onClick={() => load(1, false)}>重试</button></div>
      ) : visibleItems.length === 0 ? (
        <div className="reading-state">{spellOnly ? "当前没有已发布的 KET 必默素材" : "当前没有已发布的自由跟读素材"}</div>
      ) : (
        <>
        <section className="reading-list">
          {visibleItems.map((item) => {
            const expanded = expandedIds.has(item.id)
            const answerHidden = (readingMode === "translate" || readingMode === "spell") && !expanded
            const canPlay = !answerHidden || readingMode === "spell"
            const checkMode = readingMode === "spell" ? "spell" : "read"
            const correctKey = `${item.id}:${checkMode}:correct`
            const wrongKey = `${item.id}:${checkMode}:wrong`
            const correctLabel = readingMode === "spell" ? "默写对" : "认读对"
            const wrongLabel = readingMode === "spell" ? "默写错" : "认读错"
            return (
            <article key={item.id} className="reading-card">
              <div className="reading-card-main">
                <span className="reading-type">{typeLabels[item.materialType] || item.materialType}</span>
                <h2>{answerHidden ? item.textCn?.trim() || "听音频，默写英文" : item.textEn}</h2>
                {expanded && item.textCn && <p>{item.textCn}</p>}
                {expanded && item.description && <small>{item.description}</small>}
              </div>
              {canPlay && item.materialType === "word" && item.vocabId ? (
                <div className="reading-accent-actions">
                  {(["uk", "us"] as const).map((accent) => (
                    <button key={accent} type="button" className="reading-accent-play" onClick={() => speakMaterial(item, accent)} title={accent === "uk" ? "播放英音" : "播放美音"}>
                      {speakingText === `${item.id}:${accent}` || loadingText === `${item.id}:${accent}` ? <Loader2 className="size-4 animate-spin" /> : <Volume2 className="size-4" />}
                      <span>{accent.toUpperCase()}</span>
                    </button>
                  ))}
                </div>
              ) : canPlay ? (
                <button type="button" className="reading-play" onClick={() => speakMaterial(item)} title="播放">
                  {speakingText === item.textEn || loadingText === item.textEn ? <Loader2 className="size-6 animate-spin" /> : <Volume2 className="size-6" />}
                </button>
              ) : null}

              <button type="button" className="reading-toggle-detail" onClick={() => toggleExpanded(item.id)}>
                {expanded ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                {expanded ? "隐藏" : "显示"}
              </button>

              <div className="reading-self-check">
                <button type="button" onClick={() => markSelfCheck(item, "correct")} disabled={checkingKey === correctKey}>
                  {checkingKey === correctKey ? <Loader2 className="size-4 animate-spin" /> : null}
                  {correctLabel}
                </button>
                <button type="button" className="wrong" onClick={() => markSelfCheck(item, "wrong")} disabled={checkingKey === wrongKey}>
                  {checkingKey === wrongKey ? <Loader2 className="size-4 animate-spin" /> : null}
                  {wrongLabel}
                </button>
                {spellOnly ? (
                  <span>默写 对 {item.spellCorrectCount || 0} 错 {item.spellWrongCount || 0}</span>
                ) : (
                  <span>认读 对 {item.readCorrectCount || 0} 错 {item.readWrongCount || 0}</span>
                )}
              </div>

              {expanded && Object.keys(item.wordForms || {}).length > 0 && (
                <div className="reading-forms">
                  {formLabels.map(([key, label]) => item.wordForms[key] && (
                    <span key={key}><b>{label}</b>{item.wordForms[key]}</span>
                  ))}
                </div>
              )}

              {expanded && item.examples.length > 0 && (
                <div className="reading-examples">
                  {item.examples.map((example) => (
                    <button key={example.en} type="button" onClick={() => speak(example.en)}>
                      <Headphones className="size-4" />
                      <span>
                        {(example.formLabel || example.target) && (
                          <i className="reading-example-form">
                            {example.formLabel && <b>{example.formLabel}</b>}
                            {example.target}
                          </i>
                        )}
                        <strong>{example.en}</strong>
                        {example.cn && <em>{example.cn}</em>}
                      </span>
                    </button>
                  ))}
                </div>
              )}
            </article>
            )
          })}
        </section>
        <div className="reading-load-more">
          <span>
            {countText}
          </span>
          {hasMore && (
            <button type="button" onClick={() => load(pageNo + 1, true)} disabled={loading}>
              {loading ? <Loader2 className="size-4 animate-spin" /> : null}
              加载更多
            </button>
          )}
        </div>
        </>
      )}
    </main>
  )
}
