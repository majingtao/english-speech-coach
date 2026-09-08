"use client"

import { useCallback, useEffect, useState } from "react"
import type { AsrModel, LlmModel, TtsVoiceInfo } from "@/lib/types/speech"
import type { TtsEngineOption } from "@/lib/api/speech"
import { fetchAiConfig } from "@/lib/api/speech"
import { fetchVoices, getSystemVoices } from "./tts"

const EDGE_DEFAULT_VOICE = "en-US-AnaNeural"

export function useAiConfig() {
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

  const currentLlm = llmModels.length > 0
    ? llmModels.find((model) => `${model.provider}:${model.model}` === selectedLlmKey) || llmModels[0]
    : null
  const currentAsr = asrModels.length > 0
    ? asrModels.find((model) => model.id === selectedAsrId) || asrModels[0]
    : null

  function getVoiceOptions(): TtsVoiceInfo[] {
    if (ttsEngine === "edge") return edgeVoices
    if (ttsEngine === "vibevoice") return vibeVoices
    if (ttsEngine === "qwen-tts") return [
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
  const ttsEngineLabel = ttsEngines.find((engine) => engine.id === ttsEngine)?.label || ttsEngine

  const init = useCallback(async () => {
    const [aiConfigRes, edgeRes, vibeRes] = await Promise.allSettled([
      fetchAiConfig(),
      fetchVoices("edge"),
      fetchVoices("vibevoice"),
    ])
    const aiConfig = aiConfigRes.status === "fulfilled" ? aiConfigRes.value : null
    const defaults = aiConfig?.defaults || {}

    const llms = aiConfig?.llmModels?.length
      ? aiConfig.llmModels
      : [{ provider: "openai", model: "gpt-4o-mini", label: "GPT-4o Mini" }]
    setLlmModels(llms)
    const defaultLlmKey = defaults.llm || `${llms[0].provider}:${llms[0].model}`
    setSelectedLlmKey(llms.some((model) => `${model.provider}:${model.model}` === defaultLlmKey)
      ? defaultLlmKey
      : `${llms[0].provider}:${llms[0].model}`)
    setLlmProxy(defaults.llmProxy ?? false)

    const asrs: AsrModel[] = aiConfig?.asrModels?.length
      ? aiConfig.asrModels
      : [{ id: "sensevoice-small", port: 0, type: "offline", label: "SenseVoice Small" }]
    setAsrModels(asrs)
    const defaultAsrId = defaults.asr || asrs[0].id
    setSelectedAsrId(asrs.some((model) => model.id === defaultAsrId) ? defaultAsrId : asrs[0].id)

    const engines = aiConfig?.ttsEngines?.length
      ? aiConfig.ttsEngines
      : [{ id: "edge", label: "Edge-TTS" }]
    setTtsEngines(engines)
    const defaultEngine = defaults.ttsEngine || engines[0].id
    setTtsEngine(engines.some((engine) => engine.id === defaultEngine) ? defaultEngine : engines[0].id)
    setTtsEnabled(defaults.ttsEnabled ?? true)

    if (edgeRes.status === "fulfilled") setEdgeVoices(edgeRes.value)
    if (vibeRes.status === "fulfilled") setVibeVoices(vibeRes.value)
    setSystemVoices(getSystemVoices())
  }, [])

  // Initialization intentionally hydrates client-only model and voice state.
  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { void init() }, [init])
  useEffect(() => {
    const options = getVoiceOptions()
    if (options.length && !options.some((voice) => voice.name === selectedVoice)) {
      const preferred = ttsEngine === "edge"
        ? options.find((voice) => voice.name === EDGE_DEFAULT_VOICE)
        : undefined
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setSelectedVoice((preferred || options[0]).name)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ttsEngine, edgeVoices, vibeVoices, systemVoices])

  return {
    llmModels, selectedLlmKey, setSelectedLlmKey, llmProxy, setLlmProxy, currentLlm,
    asrModels, selectedAsrId, setSelectedAsrId, currentAsr,
    ttsEnabled, setTtsEnabled, ttsEngine, setTtsEngine, ttsEngines, selectedVoice, setSelectedVoice,
    voiceOptions, ttsEngineLabel,
  }
}
