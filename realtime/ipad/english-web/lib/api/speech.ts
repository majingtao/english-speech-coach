import type { QuestionBank, LlmModel, AsrModel } from "@/lib/types/speech"
import { apiClient } from "@/lib/api/client"

export type YleLevelCode = "starters" | "movers" | "flyers"

export interface TtsEngineOption {
  id: string
  label: string
}

export interface AiDefaults {
  llm?: string
  asr?: string
  ttsEngine?: string
  llmProxy?: boolean
  ttsEnabled?: boolean
}

export interface AiConfig {
  llmModels: LlmModel[]
  asrModels: AsrModel[]
  ttsEngines: TtsEngineOption[]
  defaults: AiDefaults
}

export async function fetchAiConfig(): Promise<AiConfig> {
  return apiClient.get("/app-api/esc/ai-config") as unknown as AiConfig
}

export interface QuestionBankQuery {
  examSeries?: string
  levelCode?: string
  seriesCode?: string
  levelName?: string
}

export interface ExamSeries {
  id: number
  code: string
  levelCode: string
  name: string
  coverUrl?: string
  publisher?: string
  description?: string
  sort?: number
  status?: number
}

export async function fetchQuestionBank(
  query?: QuestionBankQuery,
): Promise<QuestionBank> {
  const params: Record<string, string> = {}
  if (query?.levelCode) params.levelCode = query.levelCode
  if (query?.seriesCode) params.seriesCode = query.seriesCode
  return apiClient.get("/app-api/english/question-bank/all", {
    params,
  }) as unknown as QuestionBank
}

export async function fetchExamSeriesList(): Promise<ExamSeries[]> {
  return apiClient.get(
    "/app-api/english/exam-series/list-all-simple",
  ) as unknown as ExamSeries[]
}
