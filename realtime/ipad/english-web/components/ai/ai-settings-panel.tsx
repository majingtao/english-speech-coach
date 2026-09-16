"use client"

import type { ReactNode } from "react"
import type { useAiConfig } from "@/lib/exam/use-ai-config"

type AiConfig = ReturnType<typeof useAiConfig>

export function AiSettingsPanel({ config, children }: { config: AiConfig; children?: ReactNode }) {
  return (
    <>
      <div className="exam-setting-row">
        <span className="exam-setting-label">语音播报</span>
        <button type="button" className={`exam-toggle ${config.ttsEnabled ? "exam-toggle-on" : ""}`} onClick={() => config.setTtsEnabled(!config.ttsEnabled)} role="switch" aria-checked={config.ttsEnabled} aria-label="切换语音播报">
          <span className="exam-toggle-thumb" />
        </button>
      </div>
      <div className="exam-setting-group">
        <label className="exam-setting-label">音色</label>
        <select className="exam-select" value={config.selectedVoice} disabled={!config.ttsEnabled} onChange={(event) => config.setSelectedVoice(event.target.value)}>
          {config.voiceOptions.map((voice) => <option key={voice.name} value={voice.name}>{voice.label || voice.name}</option>)}
        </select>
      </div>
      <details className="ai-advanced-settings">
        <summary>家长 / 高级设置</summary>
        <div className="ai-advanced-content">
          <div className="exam-setting-group">
            <label className="exam-setting-label">对话模型</label>
            <select className="exam-select" value={config.selectedLlmKey} onChange={(event) => config.setSelectedLlmKey(event.target.value)}>
              {config.llmModels.map((model, index) => (
                <option key={`${model.provider}:${model.model}:${index}`} value={`${model.provider}:${model.model}`}>{model.label}</option>
              ))}
            </select>
          </div>
          <div className="exam-setting-row">
            <span className="exam-setting-label">对话网络代理</span>
            <button type="button" className={`exam-toggle ${config.llmProxy ? "exam-toggle-on" : ""}`} onClick={() => config.setLlmProxy(!config.llmProxy)} role="switch" aria-checked={config.llmProxy} aria-label="切换对话网络代理">
              <span className="exam-toggle-thumb" />
            </button>
          </div>

          <div className="exam-setting-group">
            <label className="exam-setting-label">语音识别模型</label>
            <select className="exam-select" value={config.selectedAsrId} onChange={(event) => config.setSelectedAsrId(event.target.value)}>
              {config.asrModels.map((model) => <option key={model.id} value={model.id}>{model.label}</option>)}
            </select>
          </div>

          <div className="exam-setting-group">
            <label className="exam-setting-label">播报引擎</label>
            <select className="exam-select" value={config.ttsEngine} disabled={!config.ttsEnabled} onChange={(event) => config.setTtsEngine(event.target.value)}>
              {config.ttsEngines.map((engine) => <option key={engine.id} value={engine.id}>{engine.label}</option>)}
            </select>
          </div>
          {children}
        </div>
      </details>
    </>
  )
}
