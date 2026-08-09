import { apiClient } from "@/lib/api/client"
import { handleAuthRejection, handleQuotaRejection, pyFetch } from "@/lib/api/py"
import type { LlmModel } from "@/lib/types/speech"

export interface ExpressionTheme {
  id: number
  code: string
  nameCn: string
  nameEn: string
  description?: string
  levelCode: string
  coverUrl?: string
  sort?: number
}

export interface ExpressionAnswerLevel {
  en: string
  cn: string
}

export interface ExpressionPattern {
  en: string
  cn?: string
  slots?: Record<string, string[]>
}

export interface ExpressionAnswerConfig {
  answers?: {
    basic?: ExpressionAnswerLevel
    expanded?: ExpressionAnswerLevel
    challenge?: ExpressionAnswerLevel
  }
  patterns?: ExpressionPattern[]
  keywords?: string[]
}

export interface ExpressionItem {
  id: number
  themeId: number
  code: string
  promptEn: string
  promptCn?: string
  functionCode?: string
  practiceMode: "speaking" | "writing" | "both"
  difficulty: number
  answerJson: ExpressionAnswerConfig
  imageUrlsJson?: string[]
  sort?: number
  progressStatus?: 0 | 1 | 2
  repetitions?: number
  bestSpeakingScore?: number
  bestWritingScore?: number
  nextReviewAt?: string
}

export interface ExpressionGradeResult {
  score: number
  passed: boolean
  summary_en: string
  summary_cn: string
  revised_answer: string
  strengths: string[]
  improvements: string[]
  dimensions: {
    content: number
    grammar: number
    vocabulary: number
    delivery: number
  }
}

const BASE = "/app-api/english/expression"

function parseJson<T>(value: unknown, fallback: T): T {
  if (value == null) return fallback
  if (typeof value !== "string") return value as T
  try {
    return JSON.parse(value) as T
  } catch {
    return fallback
  }
}

function normalizeItem(raw: unknown): ExpressionItem {
  const item = raw as Omit<ExpressionItem, "answerJson" | "imageUrlsJson"> & {
    answerJson: unknown
    imageUrlsJson?: unknown
  }
  return {
    ...item,
    answerJson: parseJson<ExpressionAnswerConfig>(item.answerJson, {}),
    imageUrlsJson: parseJson<string[]>(item.imageUrlsJson, []),
  }
}

function normalizeScore(value: unknown): number {
  const score = Number(value)
  return Number.isFinite(score) ? Math.max(0, Math.min(100, Math.round(score))) : 0
}

function normalizeGradeResult(raw: unknown): ExpressionGradeResult {
  if (!raw || typeof raw !== "object") throw new Error("AI 返回了无效的批改结果")
  const data = raw as Record<string, unknown>
  const dimensions = data.dimensions
  if (!dimensions || typeof dimensions !== "object") {
    const message = data.error || data.fb || data.cn
    throw new Error(typeof message === "string" ? message : "AI 返回的批改格式不完整，请检查所选模型")
  }
  const scores = dimensions as Record<string, unknown>
  const requiredScores = ["content", "grammar", "vocabulary", "delivery"]
  if (!requiredScores.every((key) => Number.isFinite(Number(scores[key])))) {
    throw new Error("AI 返回的评分维度不完整，请重试或更换模型")
  }
  const score = normalizeScore(data.score)
  return {
    score,
    passed: typeof data.passed === "boolean" ? data.passed : score >= 60,
    summary_en: typeof data.summary_en === "string" ? data.summary_en : "Feedback generated.",
    summary_cn: typeof data.summary_cn === "string" ? data.summary_cn : "批改完成。",
    revised_answer: typeof data.revised_answer === "string" ? data.revised_answer : "",
    strengths: Array.isArray(data.strengths) ? data.strengths.filter((item): item is string => typeof item === "string") : [],
    improvements: Array.isArray(data.improvements) ? data.improvements.filter((item): item is string => typeof item === "string") : [],
    dimensions: {
      content: normalizeScore(scores.content),
      grammar: normalizeScore(scores.grammar),
      vocabulary: normalizeScore(scores.vocabulary),
      delivery: normalizeScore(scores.delivery),
    },
  }
}

export async function fetchExpressionThemes(level = "ket"): Promise<ExpressionTheme[]> {
  return apiClient.get(`${BASE}/theme/list`, { params: { level } }) as unknown as ExpressionTheme[]
}

export async function fetchExpressionItems(
  themeCode: string,
  level = "ket",
): Promise<ExpressionItem[]> {
  const list = await apiClient.get(`${BASE}/item/list`, {
    params: { level, themeCode },
  }) as unknown as unknown[]
  return (list || []).map(normalizeItem)
}

export async function gradeExpression(input: {
  prompt: string
  responseText: string
  practiceMode: "speaking" | "writing"
  answerJson: ExpressionAnswerConfig
  level?: string
  llm?: LlmModel | null
  useProxy?: boolean
}): Promise<ExpressionGradeResult> {
  const res = await pyFetch("/py/expression/grade", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      prompt: input.prompt,
      response_text: input.responseText,
      practice_mode: input.practiceMode,
      answer_json: input.answerJson,
      level: input.level || "ket",
      provider: input.llm?.provider,
      model: input.llm?.model,
      use_proxy: input.useProxy ?? false,
    }),
  })
  if (await handleAuthRejection(res)) throw new Error("登录已失效")
  if (await handleQuotaRejection(res)) throw new Error("今日 AI 额度已用完")
  if (!res.ok) {
    const body = await res.json().catch(() => ({})) as { error?: string }
    throw new Error(body.error || `批改失败 (${res.status})`)
  }
  return normalizeGradeResult(await res.json())
}

export async function saveExpressionAttempt(
  itemId: number,
  data: {
    practiceMode: "speaking" | "writing"
    responseText: string
    score: number
    feedbackJson: ExpressionGradeResult
    durationSeconds?: number
  },
): Promise<void> {
  await apiClient.post(`${BASE}/item/${itemId}/attempt`, {
    ...data,
    feedbackJson: JSON.stringify(data.feedbackJson),
  })
}
