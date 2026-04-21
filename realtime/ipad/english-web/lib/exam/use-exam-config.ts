"use client"

import { useCallback, useEffect, useState } from "react"
import type { LlmModel, AsrModel, TtsVoiceInfo, QuestionBank } from "@/lib/types/speech"
import type { QuestionBankQuery, TtsEngineOption } from "@/lib/api/speech"
import { fetchQuestionBank, fetchAiConfig } from "@/lib/api/speech"
import { fetchVoices, getSystemVoices } from "./tts"

export function useExamConfig(query?: QuestionBankQuery) {
  const [llmModels, setLlmModels] = useState<LlmModel[]>([])
  const [selectedLlmKey, setSelectedLlmKey] = useState("")
  const [llmProxy, setLlmProxy] = useState(false)

  const [asrModels, setAsrModels] = useState<AsrModel[]>([])
  const [selectedAsrId, setSelectedAsrId] = useState("")

  const [ttsEnabled, setTtsEnabled] = useState(true)
  const [ttsEngine, setTtsEngine] = useState("")
  const [ttsEngines, setTtsEngines] = useState<TtsEngineOption[]>([])
  const [selectedVoice, setSelectedVoice] = useState("")
  const [edgeVoices, setEdgeVoices] = useState<TtsVoiceInfo[]>([])
  const [vibeVoices, setVibeVoices] = useState<TtsVoiceInfo[]>([])
  const [systemVoices, setSystemVoices] = useState<TtsVoiceInfo[]>([])

  const [questionBank, setQuestionBank] = useState<QuestionBank | null>(null)
  const [voiceOnly, setVoiceOnly] = useState(false)
  const [hideChat, setHideChat] = useState(false)

  const currentLlm = llmModels.length > 0
    ? (selectedLlmKey
      ? llmModels.find((m) => `${m.provider}:${m.model}` === selectedLlmKey) || llmModels[0]
      : llmModels[0])
    : null

  const currentAsr = asrModels.length > 0
    ? (selectedAsrId
      ? asrModels.find((m) => m.id === selectedAsrId) || asrModels[0]
      : asrModels[0])
    : null

  function getVoiceOptions(): TtsVoiceInfo[] {
    if (ttsEngine === "edge") return edgeVoices
    if (ttsEngine === "vibevoice") return vibeVoices
    if (ttsEngine === "qwen-tts")
      return [
        { name: "Chelsie", label: "Chelsie (EN Female)" },
        { name: "Ethan", label: "Ethan (EN Male)" },
        { name: "Cherry", label: "Cherry (ZH Female)" },
        { name: "Serena", label: "Serena (ZH Female)" },
      ]
    if (ttsEngine === "piper") return [{ name: "amy", label: "Amy (en-US)" }]
    if (ttsEngine === "system") return systemVoices
    return []
  }

  const voiceOptions = getVoiceOptions()

  const ttsEngineLabel = ttsEngines.find((e) => e.id === ttsEngine)?.label || ttsEngine

  const init = useCallback(async () => {
    const [aiConfigRes, edgeRes, vibeRes, qbRes] = await Promise.allSettled([
      fetchAiConfig(),
      fetchVoices("edge"),
      fetchVoices("vibevoice"),
      fetchQuestionBank(query),
    ])

    const aiConfig = aiConfigRes.status === "fulfilled" ? aiConfigRes.value : null
    const defaults = aiConfig?.defaults || {}

    // LLM
    const llms: LlmModel[] = aiConfig?.llmModels?.length
      ? aiConfig.llmModels
      : [{ provider: "openai", model: "gpt-4o-mini", label: "GPT-4o Mini" }]
    setLlmModels(llms)
    const defaultLlmKey = defaults.llm || `${llms[0].provider}:${llms[0].model}`
    setSelectedLlmKey(
      llms.some((m) => `${m.provider}:${m.model}` === defaultLlmKey)
        ? defaultLlmKey
        : `${llms[0].provider}:${llms[0].model}`,
    )
    setLlmProxy(defaults.llmProxy ?? false)

    // ASR
    const asrs: AsrModel[] = aiConfig?.asrModels?.length
      ? aiConfig.asrModels
      : [{ id: "sensevoice-small", port: 0, type: "offline" as const, label: "SenseVoice Small" }]
    setAsrModels(asrs)
    const defaultAsrId = defaults.asr || asrs[0].id
    setSelectedAsrId(asrs.some((m) => m.id === defaultAsrId) ? defaultAsrId : asrs[0].id)

    // TTS engines
    const engines: TtsEngineOption[] = aiConfig?.ttsEngines?.length
      ? aiConfig.ttsEngines
      : [{ id: "edge", label: "Edge-TTS" }]
    setTtsEngines(engines)
    const defaultEngine = defaults.ttsEngine || engines[0].id
    setTtsEngine(engines.some((e) => e.id === defaultEngine) ? defaultEngine : engines[0].id)
    setTtsEnabled(defaults.ttsEnabled ?? true)

    // TTS voices
    if (edgeRes.status === "fulfilled") setEdgeVoices(edgeRes.value)
    if (vibeRes.status === "fulfilled") setVibeVoices(vibeRes.value)
    setSystemVoices(getSystemVoices())

    // Question bank
    if (qbRes.status === "fulfilled") {
      setQuestionBank(qbRes.value)
    } else {
      console.error("题库加载失败", qbRes.reason)
    }
  }, [query])

  useEffect(() => { init() }, [init])

  useEffect(() => {
    const opts = getVoiceOptions()
    if (opts.length && !opts.some((v) => v.name === selectedVoice)) {
      setSelectedVoice(opts[0].name)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ttsEngine, edgeVoices, vibeVoices, systemVoices])

  return {
    llmModels, selectedLlmKey, setSelectedLlmKey, llmProxy, setLlmProxy, currentLlm,
    asrModels, selectedAsrId, setSelectedAsrId, currentAsr,
    ttsEnabled, setTtsEnabled, ttsEngine, setTtsEngine, ttsEngines, selectedVoice, setSelectedVoice,
    voiceOptions, ttsEngineLabel,
    questionBank, voiceOnly, setVoiceOnly, hideChat, setHideChat,
  }
}
