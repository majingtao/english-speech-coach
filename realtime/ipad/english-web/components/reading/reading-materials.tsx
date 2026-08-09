"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, Headphones, Loader2, RefreshCw, Settings, Volume2 } from "lucide-react"
import { fetchReadingMaterials, fetchReadingTags, type NormalizedReadingMaterial, type ReadingWordForms } from "@/lib/api/reading-material"
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

export function ReadingMaterials() {
  const router = useRouter()
  const config = useAiConfig()
  const [items, setItems] = useState<NormalizedReadingMaterial[]>([])
  const [tags, setTags] = useState<string[]>([])
  const [activeTag, setActiveTag] = useState("")
  const [activeType, setActiveType] = useState("")
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [speakingText, setSpeakingText] = useState("")
  const [loadingText, setLoadingText] = useState("")
  const [ttsLoading, setTtsLoading] = useState(false)

  const filtered = useMemo(() => items.filter((item) => {
    if (activeTag && !item.tags.includes(activeTag)) return false
    if (activeType && item.materialType !== activeType) return false
    return true
  }), [activeTag, activeType, items])

  const load = useCallback(async () => {
    setLoading(true)
    setError("")
    try {
      const [materialList, tagList] = await Promise.all([
        fetchReadingMaterials({ level: "ket" }),
        fetchReadingTags("ket"),
      ])
      setItems(materialList)
      setTags(tagList)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "自由跟读素材加载失败")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(load, 0)
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

  return (
    <main className="reading-shell">
      <header className="reading-header">
        <button type="button" className="reading-icon-btn" onClick={() => router.push("/")} title="返回首页">
          <ArrowLeft className="size-5" />
        </button>
        <div>
          <h1>自由跟读</h1>
          <p>点播放，跟着读</p>
        </div>
        <div className="reading-header-actions">
          <button type="button" className="reading-icon-btn" onClick={() => setSettingsOpen((open) => !open)} title="语音设置">
            <Settings className="size-5" />
          </button>
          <button type="button" className="reading-icon-btn" onClick={load} disabled={loading} title="刷新">
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

      <section className="reading-filters" aria-label="筛选">
        <button type="button" className={!activeType ? "active" : ""} onClick={() => setActiveType("")}>全部</button>
        {Object.entries(typeLabels).map(([type, label]) => (
          <button key={type} type="button" className={activeType === type ? "active" : ""} onClick={() => setActiveType(type)}>{label}</button>
        ))}
      </section>

      {tags.length > 0 && (
        <section className="reading-tags" aria-label="标签">
          <button type="button" className={!activeTag ? "active" : ""} onClick={() => setActiveTag("")}>全部标签</button>
          {tags.map((tag) => (
            <button key={tag} type="button" className={activeTag === tag ? "active" : ""} onClick={() => setActiveTag(tag)}>{tag}</button>
          ))}
        </section>
      )}

      {loading ? (
        <div className="reading-state"><Loader2 className="size-6 animate-spin" />正在加载</div>
      ) : error ? (
        <div className="reading-state reading-error"><span>{error}</span><button type="button" onClick={load}>重试</button></div>
      ) : filtered.length === 0 ? (
        <div className="reading-state">当前没有已发布的自由跟读素材</div>
      ) : (
        <section className="reading-list">
          {filtered.map((item) => (
            <article key={item.id} className="reading-card">
              <div className="reading-card-main">
                <span className="reading-type">{typeLabels[item.materialType] || item.materialType}</span>
                <h2>{item.textEn}</h2>
                {item.textCn && <p>{item.textCn}</p>}
                {item.description && <small>{item.description}</small>}
              </div>
              {item.materialType === "word" && item.vocabId ? (
                <div className="reading-accent-actions">
                  {(["uk", "us"] as const).map((accent) => (
                    <button key={accent} type="button" className="reading-accent-play" onClick={() => speakMaterial(item, accent)} title={accent === "uk" ? "播放英音" : "播放美音"}>
                      {speakingText === `${item.id}:${accent}` || loadingText === `${item.id}:${accent}` ? <Loader2 className="size-4 animate-spin" /> : <Volume2 className="size-4" />}
                      <span>{accent.toUpperCase()}</span>
                    </button>
                  ))}
                </div>
              ) : (
                <button type="button" className="reading-play" onClick={() => speakMaterial(item)} title="播放">
                  {speakingText === item.textEn || loadingText === item.textEn ? <Loader2 className="size-6 animate-spin" /> : <Volume2 className="size-6" />}
                </button>
              )}

              {Object.keys(item.wordForms || {}).length > 0 && (
                <div className="reading-forms">
                  {formLabels.map(([key, label]) => item.wordForms[key] && (
                    <span key={key}><b>{label}</b>{item.wordForms[key]}</span>
                  ))}
                </div>
              )}

              {item.examples.length > 0 && (
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
          ))}
        </section>
      )}
    </main>
  )
}
