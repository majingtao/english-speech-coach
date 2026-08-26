import { apiClient } from "@/lib/api/client"
import { handleAuthRejection, handleQuotaRejection, pyFetch } from "@/lib/api/py"
import type { LlmModel } from "@/lib/types/speech"

export type PersonalPracticeType = "speaking" | "writing"

export interface PersonalReferenceLine {
  en: string
  cn: string
}

export interface PersonalPractice {
  id: number
  practiceType: PersonalPracticeType
  title: string
  promptEn: string
  promptCn?: string
  referenceLines: PersonalReferenceLine[]
  contentPoints: string[]
  minSentences: number
  minWords: number
  sort?: number
  progressStatus?: number
  attemptCount?: number
  bestScore?: number
  lastScore?: number
  lastPracticeAt?: string
}

export interface PersonalPracticeGrade {
  grammarCorrect: boolean
  grammarScore: number
  contentScore: number
  totalScore: number
  correctedAnswer: string
  enrichedAnswer: string
  contentFeedbackCn: string
  missingPoints: string[]
}

function parseArray<T>(value: unknown): T[] {
  if (Array.isArray(value)) return value as T[]
  if (typeof value !== "string" || !value.trim()) return []
  try {
    const parsed = JSON.parse(value)
    return Array.isArray(parsed) ? parsed as T[] : []
  } catch {
    return []
  }
}

function normalizePractice(raw: unknown): PersonalPractice | null {
  if (!raw || typeof raw !== "object") return null
  const data = raw as Record<string, unknown>
  if (typeof data.id !== "number" || (data.practiceType !== "speaking" && data.practiceType !== "writing")) return null
  return {
    id: data.id,
    practiceType: data.practiceType,
    title: typeof data.title === "string" ? data.title : "专属练习",
    promptEn: typeof data.promptEn === "string" ? data.promptEn : "",
    promptCn: typeof data.promptCn === "string" ? data.promptCn : "",
    referenceLines: parseArray<PersonalReferenceLine>(data.referenceJson)
      .filter((line) => line && typeof line.en === "string" && line.en.trim())
      .map((line) => ({ en: line.en, cn: typeof line.cn === "string" ? line.cn : "" })),
    contentPoints: parseArray<string>(data.contentPointsJson).filter((point) => typeof point === "string" && point.trim()),
    minSentences: typeof data.minSentences === "number" ? Math.max(1, data.minSentences) : 1,
    minWords: typeof data.minWords === "number" ? Math.max(0, data.minWords) : 0,
    sort: typeof data.sort === "number" ? data.sort : 0,
    progressStatus: typeof data.progressStatus === "number" ? data.progressStatus : 0,
    attemptCount: typeof data.attemptCount === "number" ? data.attemptCount : 0,
    bestScore: typeof data.bestScore === "number" ? data.bestScore : undefined,
    lastScore: typeof data.lastScore === "number" ? data.lastScore : undefined,
    lastPracticeAt: typeof data.lastPracticeAt === "string" ? data.lastPracticeAt : undefined,
  }
}

export async function fetchPersonalPractices(type: PersonalPracticeType): Promise<PersonalPractice[]> {
  const data = await apiClient.get("/app-api/english/personal-practice/list", { params: { type } }) as unknown
  if (!Array.isArray(data)) return []
  return data.map(normalizePractice).filter((item): item is PersonalPractice => !!item)
}

export async function gradePersonalPractice(input: {
  practice: PersonalPractice
  responseText: string
  llm?: LlmModel | null
  useProxy?: boolean
}): Promise<PersonalPracticeGrade> {
  const res = await pyFetch("/py/personal-practice/grade", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      practice_type: input.practice.practiceType,
      question: input.practice.promptEn,
      question_cn: input.practice.promptCn,
      reference_lines: input.practice.referenceLines,
      content_points: input.practice.contentPoints,
      min_sentences: input.practice.minSentences,
      min_words: input.practice.minWords,
      response_text: input.responseText,
      provider: input.llm?.provider,
      model: input.llm?.model,
      use_proxy: input.useProxy ?? false,
    }),
  })
  if (await handleAuthRejection(res)) throw new Error("登录已失效")
  if (await handleQuotaRejection(res)) throw new Error("今日 AI 额度已用完")
  const data = await res.json().catch(() => ({})) as Record<string, unknown>
  if (!res.ok) throw new Error(typeof data.error === "string" ? data.error : `评分失败 (${res.status})`)
  return {
    grammarCorrect: data.grammar_correct === true,
    grammarScore: typeof data.grammar_score === "number" ? data.grammar_score : 0,
    contentScore: typeof data.content_score === "number" ? data.content_score : 0,
    totalScore: typeof data.total_score === "number" ? data.total_score : 0,
    correctedAnswer: typeof data.corrected_answer === "string" ? data.corrected_answer : input.responseText,
    enrichedAnswer: typeof data.enriched_answer === "string" ? data.enriched_answer : input.responseText,
    contentFeedbackCn: typeof data.content_feedback_cn === "string" ? data.content_feedback_cn : "",
    missingPoints: Array.isArray(data.missing_points)
      ? data.missing_points.filter((item): item is string => typeof item === "string")
      : [],
  }
}

export async function savePersonalPracticeAttempt(input: {
  practiceId: number
  responseText: string
  grade: PersonalPracticeGrade
  durationSeconds?: number
}) {
  await apiClient.post(`/app-api/english/personal-practice/${input.practiceId}/attempt`, {
    responseText: input.responseText,
    grammarScore: input.grade.grammarScore,
    contentScore: input.grade.contentScore,
    totalScore: input.grade.totalScore,
    feedbackJson: JSON.stringify(input.grade),
    durationSeconds: input.durationSeconds,
  })
}
