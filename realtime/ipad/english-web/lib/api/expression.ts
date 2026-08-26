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

export type DialogueRole = "student_a" | "student_b"

export interface DialogueImageSlot {
  key: string
  label: string
  imageUrl: string
}

export interface ExpressionDialogueConfig {
  source?: {
    book?: string
    page?: number
    unit?: number
    section?: string
    status?: string
  }
  starterRole: DialogueRole
  minLearnerTurns: number
  maxTotalTurns: number
  imageSlots: DialogueImageSlot[]
  targetLanguage: string[]
  followUpQuestions: string[]
  sampleExchange?: Array<{ role: DialogueRole; text: string }>
}

export interface ExpressionDialogueTask {
  id: number
  code: string
  levelCode: string
  titleCn: string
  titleEn: string
  topicCode: string
  promptEn: string
  promptCn?: string
  configJson: ExpressionDialogueConfig
  sort?: number
}

export interface DialogueTurn {
  role: DialogueRole
  text: string
}

export interface ExpressionDialogueGradeResult {
  score: number
  passed: boolean
  summary_en: string
  summary_cn: string
  strengths: string[]
  improvements: string[]
  useful_phrases: string[]
  dimensions: {
    interaction: number
    task_achievement: number
    language: number
    fluency: number
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

const DIALOGUE_BASE = `${BASE}/dialogue`

function normalizeDialogueTask(raw: unknown): ExpressionDialogueTask {
  const task = raw as Omit<ExpressionDialogueTask, "configJson"> & { configJson: unknown }
  return {
    ...task,
    configJson: parseJson<ExpressionDialogueConfig>(task.configJson, {
      starterRole: "student_a",
      minLearnerTurns: 3,
      maxTotalTurns: 8,
      imageSlots: [],
      targetLanguage: [],
      followUpQuestions: [],
    }),
  }
}

export async function fetchExpressionDialogueTasks(level = "ket"): Promise<ExpressionDialogueTask[]> {
  const list = await apiClient.get(`${DIALOGUE_BASE}/list`, { params: { level } }) as unknown as unknown[]
  return (list || []).map(normalizeDialogueTask)
}

export async function fetchExpressionDialogueTask(id: number): Promise<ExpressionDialogueTask> {
  return normalizeDialogueTask(await apiClient.get(`${DIALOGUE_BASE}/${id}`))
}

function otherRole(role: DialogueRole): DialogueRole {
  return role === "student_a" ? "student_b" : "student_a"
}

export async function requestDialoguePartnerTurn(input: {
  task: ExpressionDialogueTask
  learnerRole: DialogueRole
  transcript: DialogueTurn[]
  llm?: LlmModel | null
  useProxy?: boolean
}): Promise<string> {
  const partnerRole = otherRole(input.learnerRole)
  const config = input.task.configJson
  const system = [
    `You are ${partnerRole === "student_a" ? "Student A" : "Student B"} in an A2 Key for Schools Part 2 practice.`,
    `The learner is ${input.learnerRole === "student_a" ? "Student A" : "Student B"}.`,
    `Task: ${input.task.promptEn}`,
    `Options: ${config.imageSlots.map((slot) => slot.label).join(", ")}.`,
    `Useful language: ${config.targetLanguage.join(" | ")}.`,
    `Possible final questions: ${config.followUpQuestions.join(" | ")}.`,
    "Reply as a friendly 9-14 year-old A2 learner. Use one or two short English sentences.",
    "Give a real opinion or reason, respond to the learner, and usually ask one short question back.",
    "Do not explain the exercise, score the learner, use Chinese, or write a role label.",
  ].join("\n")
  const messages: Array<{ role: "system" | "user" | "assistant"; content: string }> = [
    { role: "system", content: system },
  ]
  for (const turn of input.transcript) {
    messages.push({
      role: turn.role === input.learnerRole ? "user" : "assistant",
      content: turn.text,
    })
  }
  if (input.transcript.length === 0) {
    messages.push({
      role: "user",
      content: `Start the discussion as ${partnerRole === "student_a" ? "Student A" : "Student B"}.`,
    })
  }

  const res = await pyFetch("/py/llm/chat", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      provider: input.llm?.provider,
      model: input.llm?.model,
      use_proxy: input.useProxy ?? false,
      messages,
    }),
  })
  if (await handleAuthRejection(res)) throw new Error("登录已失效")
  if (await handleQuotaRejection(res)) throw new Error("今日 AI 额度已用完")
  if (!res.ok || !res.body) throw new Error(`AI 对话失败 (${res.status})`)

  const reader = res.body.getReader()
  const decoder = new TextDecoder()
  let pending = ""
  let answer = ""
  while (true) {
    const { done, value } = await reader.read()
    pending += decoder.decode(value || new Uint8Array(), { stream: !done })
    const lines = pending.split("\n")
    pending = lines.pop() || ""
    for (const line of lines) {
      if (!line.trim()) continue
      try {
        const chunk = JSON.parse(line) as { content?: string }
        answer += chunk.content || ""
      } catch {
        // Ignore an incomplete provider line; the proxy emits newline-delimited JSON.
      }
    }
    if (done) break
  }
  if (pending.trim()) {
    try {
      answer += (JSON.parse(pending) as { content?: string }).content || ""
    } catch {
      // The completed chunks already contain the usable answer.
    }
  }
  const clean = answer.trim().replace(/^(Student A|Student B|A|B):\s*/i, "")
  if (!clean) throw new Error("AI 没有返回有效回复")
  return clean
}

function normalizeDialogueGrade(raw: unknown): ExpressionDialogueGradeResult {
  if (!raw || typeof raw !== "object") throw new Error("AI 返回了无效的互动反馈")
  const data = raw as Record<string, unknown>
  const values = data.dimensions as Record<string, unknown> | undefined
  if (!values) throw new Error("AI 返回的互动评分不完整")
  const score = normalizeScore(data.score)
  return {
    score,
    passed: typeof data.passed === "boolean" ? data.passed : score >= 60,
    summary_en: typeof data.summary_en === "string" ? data.summary_en : "Dialogue assessed.",
    summary_cn: typeof data.summary_cn === "string" ? data.summary_cn : "互动练习已完成。",
    strengths: Array.isArray(data.strengths) ? data.strengths.filter((v): v is string => typeof v === "string") : [],
    improvements: Array.isArray(data.improvements) ? data.improvements.filter((v): v is string => typeof v === "string") : [],
    useful_phrases: Array.isArray(data.useful_phrases) ? data.useful_phrases.filter((v): v is string => typeof v === "string") : [],
    dimensions: {
      interaction: normalizeScore(values.interaction),
      task_achievement: normalizeScore(values.task_achievement),
      language: normalizeScore(values.language),
      fluency: normalizeScore(values.fluency),
    },
  }
}

export async function gradeExpressionDialogue(input: {
  task: ExpressionDialogueTask
  learnerRole: DialogueRole
  transcript: DialogueTurn[]
  llm?: LlmModel | null
  useProxy?: boolean
}): Promise<ExpressionDialogueGradeResult> {
  const res = await pyFetch("/py/expression/dialogue/grade", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      task: {
        prompt: input.task.promptEn,
        options: input.task.configJson.imageSlots.map((slot) => slot.label),
        target_language: input.task.configJson.targetLanguage,
        follow_up_questions: input.task.configJson.followUpQuestions,
      },
      selected_role: input.learnerRole,
      transcript: input.transcript,
      provider: input.llm?.provider,
      model: input.llm?.model,
      use_proxy: input.useProxy ?? false,
    }),
  })
  if (await handleAuthRejection(res)) throw new Error("登录已失效")
  if (await handleQuotaRejection(res)) throw new Error("今日 AI 额度已用完")
  if (!res.ok) {
    const body = await res.json().catch(() => ({})) as { error?: string }
    throw new Error(body.error || `互动评分失败 (${res.status})`)
  }
  return normalizeDialogueGrade(await res.json())
}

export async function saveExpressionDialogueAttempt(
  taskId: number,
  data: {
    selectedRole: DialogueRole
    transcript: DialogueTurn[]
    score: number
    feedback: ExpressionDialogueGradeResult
    durationSeconds?: number
  },
): Promise<void> {
  await apiClient.post(`${DIALOGUE_BASE}/${taskId}/attempt`, {
    selectedRole: data.selectedRole,
    transcriptJson: JSON.stringify(data.transcript),
    score: data.score,
    feedbackJson: JSON.stringify(data.feedback),
    durationSeconds: data.durationSeconds,
  })
}
