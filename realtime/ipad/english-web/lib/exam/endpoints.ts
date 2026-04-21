const PY_BASE = "/py"

export const endpoints = {
  tts: () => `${PY_BASE}/tts`,
  ttsVoices: () => `${PY_BASE}/tts/voices`,
  asrModels: () => `${PY_BASE}/asr/models`,
  asrOffline: () => `${PY_BASE}/asr`,
  llmModels: () => `${PY_BASE}/llm/models`,
  llmChat: () => `${PY_BASE}/llm/chat`,
  judge: () => `${PY_BASE}/llm/judge`,
}
