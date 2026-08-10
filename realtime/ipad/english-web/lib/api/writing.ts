import { handleAuthRejection, handleQuotaRejection, pyFetch } from "@/lib/api/py"
import { apiClient } from "@/lib/api/client"
import type { LlmModel } from "@/lib/types/speech"

export interface KetWritingTask {
  id: string
  status: "reviewed" | "needs_image" | "candidate" | "rewrite_later"
  sourceCandidateId?: string
  sourceBook: string
  sourceUnit?: number
  sourcePage?: number
  part: 6 | 7
  type: "email" | "note" | "story" | "model"
  title: string
  promptEn: string
  promptCn?: string
  requirements: string[]
  targetWords: string
  sampleAnswer: string
  sampleCn?: string
  writingFrame: string[]
  supportWords: string[]
  supportPhrases: string[]
  aiDraftFields?: Array<{
    key: string
    label: string
    placeholder?: string
  }>
  picturePrompts: string[]
  imageSlots: Array<{ slot: number; status: string; note?: string }>
  imageUrls: string[]
}

export interface KetWritingGradeResult {
  level: "great" | "almost" | "practice"
  summary_cn: string
  teacher_notes: string[]
  corrections: Array<{
    original: string
    corrected: string
    reason_cn: string
  }>
  revised_answer: string
  useful_sentences: string[]
  next_goal_cn: string
  dimensions: {
    task: "好" | "还差一点" | "需要练习"
    grammar: "好" | "还差一点" | "需要练习"
    spelling: "好" | "还差一点" | "需要练习"
    organization: "好" | "还差一点" | "需要练习"
  }
}

export interface KetWritingDraftResult {
  draft: string
  tips_cn: string[]
}

export interface KetWritingAttempt {
  attemptId: number
  taskId: string
  levelCode: string
  part: 6 | 7
  practiceMode: "guided" | "imitate" | "free"
  promptSnapshotJson?: string | null
  learnerInfoJson?: string | null
  aiDraftText?: string | null
  responseText: string
  wordCount: number
  feedbackJson?: string | null
  feedback?: KetWritingGradeResult | null
  scoreLabel?: KetWritingGradeResult["level"] | null
  createTime?: string | null
  attemptCount: number
}

export interface KetWritingLatestAttempt {
  attemptCount: number
  latest?: KetWritingAttempt | null
}

function normalizeStringArray(value: unknown): string[] {
  return Array.isArray(value) ? value.filter((item): item is string => typeof item === "string") : []
}

function normalizeTask(raw: unknown): KetWritingTask | null {
  if (!raw || typeof raw !== "object") return null
  const data = raw as Record<string, unknown>
  if (typeof data.id !== "string" || typeof data.title !== "string" || typeof data.promptEn !== "string") {
    return null
  }
  return {
    id: data.id,
    status: data.status === "needs_image" || data.status === "candidate" || data.status === "rewrite_later" ? data.status : "reviewed",
    sourceCandidateId: typeof data.sourceCandidateId === "string" ? data.sourceCandidateId : undefined,
    sourceBook: typeof data.sourceBook === "string" ? data.sourceBook : "",
    sourceUnit: typeof data.sourceUnit === "number" ? data.sourceUnit : undefined,
    sourcePage: typeof data.sourcePage === "number" ? data.sourcePage : undefined,
    part: data.part === 7 ? 7 : 6,
    type: data.type === "note" || data.type === "story" || data.type === "model" ? data.type : "email",
    title: data.title,
    promptEn: data.promptEn,
    promptCn: typeof data.promptCn === "string" ? data.promptCn : "",
    requirements: normalizeStringArray(data.requirements),
    targetWords: typeof data.targetWords === "string" ? data.targetWords : data.part === 7 ? "35+" : "25+",
    sampleAnswer: typeof data.sampleAnswer === "string" ? data.sampleAnswer : "",
    sampleCn: typeof data.sampleCn === "string" ? data.sampleCn : "",
    writingFrame: normalizeStringArray(data.writingFrame),
    supportWords: normalizeStringArray(data.supportWords),
    supportPhrases: normalizeStringArray(data.supportPhrases),
    aiDraftFields: Array.isArray(data.aiDraftFields)
      ? data.aiDraftFields
        .filter((item): item is Record<string, unknown> => !!item && typeof item === "object")
        .map((item) => ({
          key: typeof item.key === "string" ? item.key : "",
          label: typeof item.label === "string" ? item.label : "",
          placeholder: typeof item.placeholder === "string" ? item.placeholder : undefined,
        }))
        .filter((item) => item.key && item.label)
      : [],
    picturePrompts: normalizeStringArray(data.picturePrompts),
    imageSlots: Array.isArray(data.imageSlots)
      ? data.imageSlots
        .filter((item): item is Record<string, unknown> => !!item && typeof item === "object")
        .map((item) => ({
          slot: typeof item.slot === "number" ? item.slot : 0,
          status: typeof item.status === "string" ? item.status : "manual",
          note: typeof item.note === "string" ? item.note : undefined,
        }))
        .filter((item) => item.slot > 0)
      : [],
    imageUrls: normalizeStringArray(data.imageUrls),
  }
}

function normalizeGradeResult(raw: unknown): KetWritingGradeResult {
  if (!raw || typeof raw !== "object") throw new Error("AI 返回了无效的批改结果")
  const data = raw as Record<string, unknown>
  const dimensions = data.dimensions && typeof data.dimensions === "object"
    ? data.dimensions as Record<string, unknown>
    : {}
  const label = (value: unknown): "好" | "还差一点" | "需要练习" => (
    value === "好" || value === "还差一点" || value === "需要练习" ? value : "还差一点"
  )
  return {
    level: data.level === "great" || data.level === "almost" || data.level === "practice" ? data.level : "almost",
    summary_cn: typeof data.summary_cn === "string" ? data.summary_cn : "批改完成。",
    teacher_notes: normalizeStringArray(data.teacher_notes),
    corrections: Array.isArray(data.corrections)
      ? data.corrections
        .filter((item): item is Record<string, unknown> => !!item && typeof item === "object")
        .map((item) => ({
          original: typeof item.original === "string" ? item.original : "",
          corrected: typeof item.corrected === "string" ? item.corrected : "",
          reason_cn: typeof item.reason_cn === "string" ? item.reason_cn : "",
        }))
        .filter((item) => item.corrected || item.reason_cn)
      : [],
    revised_answer: typeof data.revised_answer === "string" ? data.revised_answer : "",
    useful_sentences: normalizeStringArray(data.useful_sentences),
    next_goal_cn: typeof data.next_goal_cn === "string" ? data.next_goal_cn : "下次注意把句子写完整。",
    dimensions: {
      task: label(dimensions.task),
      grammar: label(dimensions.grammar),
      spelling: label(dimensions.spelling),
      organization: label(dimensions.organization),
    },
  }
}

export async function gradeKetWriting(input: {
  task: KetWritingTask
  responseText: string
  mode: "guided" | "imitate" | "free"
  llm?: LlmModel | null
  useProxy?: boolean
}): Promise<KetWritingGradeResult> {
  const res = await pyFetch("/py/writing/ket/grade", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      task: input.task,
      response_text: input.responseText,
      mode: input.mode,
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

export async function fetchKetWritingTasks(level = "ket"): Promise<KetWritingTask[]> {
  const data = await apiClient.get("/app-api/english/writing/ket/task/list", {
    params: { level },
  }) as unknown
  if (!Array.isArray(data)) return []
  return data.map(normalizeTask).filter((item): item is KetWritingTask => !!item)
}

export async function generateKetWritingDraft(input: {
  task: KetWritingTask
  learnerInfo: Record<string, string>
  llm?: LlmModel | null
  useProxy?: boolean
}): Promise<KetWritingDraftResult> {
  const res = await pyFetch("/py/writing/ket/model", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      task: input.task,
      learner_info: input.learnerInfo,
      provider: input.llm?.provider,
      model: input.llm?.model,
      use_proxy: input.useProxy ?? false,
    }),
  })
  if (await handleAuthRejection(res)) throw new Error("登录已失效")
  if (await handleQuotaRejection(res)) throw new Error("今日 AI 额度已用完")
  if (!res.ok) {
    const body = await res.json().catch(() => ({})) as { error?: string }
    throw new Error(body.error || `AI仿写失败 (${res.status})`)
  }
  const data = await res.json() as Record<string, unknown>
  return {
    draft: typeof data.draft === "string" ? data.draft : "",
    tips_cn: normalizeStringArray(data.tips_cn),
  }
}

function normalizeAttempt(raw: unknown): KetWritingAttempt | null {
  if (!raw || typeof raw !== "object") return null
  const data = raw as Record<string, unknown>
  let feedback: KetWritingGradeResult | null = null
  if (typeof data.feedbackJson === "string" && data.feedbackJson.trim()) {
    try {
      feedback = normalizeGradeResult(JSON.parse(data.feedbackJson))
    } catch {
      feedback = null
    }
  }
  return {
    attemptId: typeof data.attemptId === "number" ? data.attemptId : 0,
    taskId: typeof data.taskId === "string" ? data.taskId : "",
    levelCode: typeof data.levelCode === "string" ? data.levelCode : "ket",
    part: data.part === 7 ? 7 : 6,
    practiceMode: data.practiceMode === "imitate" || data.practiceMode === "free" ? data.practiceMode : "guided",
    promptSnapshotJson: typeof data.promptSnapshotJson === "string" ? data.promptSnapshotJson : null,
    learnerInfoJson: typeof data.learnerInfoJson === "string" ? data.learnerInfoJson : null,
    aiDraftText: typeof data.aiDraftText === "string" ? data.aiDraftText : null,
    responseText: typeof data.responseText === "string" ? data.responseText : "",
    wordCount: typeof data.wordCount === "number" ? data.wordCount : 0,
    feedbackJson: typeof data.feedbackJson === "string" ? data.feedbackJson : null,
    feedback,
    scoreLabel: data.scoreLabel === "great" || data.scoreLabel === "almost" || data.scoreLabel === "practice" ? data.scoreLabel : null,
    createTime: typeof data.createTime === "string" ? data.createTime : null,
    attemptCount: typeof data.attemptCount === "number" ? data.attemptCount : 0,
  }
}

export async function fetchKetWritingLatest(taskId: string): Promise<KetWritingLatestAttempt> {
  const data = await apiClient.get("/app-api/english/writing/ket/attempt/latest", {
    params: { taskId },
  }) as Record<string, unknown>
  return {
    attemptCount: typeof data.attemptCount === "number" ? data.attemptCount : 0,
    latest: normalizeAttempt(data.latest),
  }
}

export async function saveKetWritingAttempt(input: {
  task: KetWritingTask
  mode: "guided" | "imitate" | "free"
  learnerInfo: Record<string, string>
  aiDraftText?: string
  responseText: string
  wordCount: number
  feedback: KetWritingGradeResult
}): Promise<KetWritingAttempt | null> {
  const data = await apiClient.post("/app-api/english/writing/ket/attempt", {
    taskId: input.task.id,
    levelCode: "ket",
    part: input.task.part,
    practiceMode: input.mode,
    promptSnapshotJson: JSON.stringify(input.task),
    learnerInfoJson: JSON.stringify(input.learnerInfo),
    aiDraftText: input.aiDraftText || "",
    responseText: input.responseText,
    wordCount: input.wordCount,
    feedbackJson: JSON.stringify(input.feedback),
    scoreLabel: input.feedback.level,
  })
  return normalizeAttempt(data)
}
