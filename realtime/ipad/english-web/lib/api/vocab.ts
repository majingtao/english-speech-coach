import { apiClient } from "@/lib/api/client"
import { pyFetch, handleAuthRejection, handleQuotaRejection } from "@/lib/api/py"

export type VocabProgressStatus = 0 | 1 | 2 // 0 新词, 1 学习中, 2 已掌握

export interface VocabExample {
  en: string
  cn: string
}

export interface VocabDefinition {
  cefr?: string
  definition_cn?: string
  definition_en?: string
  examples?: VocabExample[]
}

export interface VocabEntry {
  headword?: string
  pos?: string
  definitions?: VocabDefinition[]
}

export interface VocabContent {
  definition_cn?: string
  definition_en?: string
  ipa?: string
  forms?: VocabForms
  examples?: VocabExample[]
  entries?: VocabEntry[]
  gen_at?: string
  model_used?: string
}

export interface VocabForms {
  base?: string
  past?: string
  past_participle?: string
  ing?: string
  third_person?: string
  singular?: string
  plural?: string
  comparative?: string
  superlative?: string
}

export interface VocabDetail {
  id: number
  word: string
  levelCode: string
  pos?: string
  difficulty?: number
  contentJson?: VocabContent
  formsJson?: VocabForms
  audioUkUrl?: string
  audioUsUrl?: string
  themeCodes?: string[]
  progressStatus?: VocabProgressStatus
  repetitions?: number
  nextReviewAt?: string
}

export type VocabAccent = "uk" | "us"

export interface VocabListItem {
  id: number
  word: string
  levelCode: string
  pos?: string
  difficulty?: number
  progressStatus?: VocabProgressStatus
  repetitions?: number
}

export interface VocabTheme {
  id: number
  code: string
  nameCn: string
  nameEn: string
  levelCode: string
  sort?: number
}

export interface VocabReviewResult {
  status: VocabProgressStatus
  repetitions: number
  intervalDays: number
  nextReviewAt: string
  correctCount: number
  wrongCount: number
}

export interface UserVocabList {
  id: number
  name: string
  description?: string
  source?: string
  wordCount: number
  createTime?: string
}

const BASE = "/app-api/english/vocab"
const LIST_BASE = "/app-api/english/user-vocab-list"

function safeParse<T>(value: unknown): T | undefined {
  if (value == null) return undefined
  if (typeof value !== "string") return value as T
  try {
    return JSON.parse(value) as T
  } catch {
    return undefined
  }
}

/** 后端 content_json / forms_json 是 TEXT 列直存的字符串，前端需先解析 */
function normalizeVocabDetail(raw: unknown): VocabDetail {
  const r = (raw ?? {}) as unknown as VocabDetail & {
    contentJson?: unknown
    formsJson?: unknown
  }
  return {
    ...r,
    contentJson: safeParse<VocabContent>(r.contentJson),
    formsJson: safeParse<VocabForms>(r.formsJson),
  }
}

export async function fetchTodayReview(limit = 20): Promise<VocabDetail[]> {
  const list = (await apiClient.get(`${BASE}/today-review`, {
    params: { limit },
  })) as unknown as VocabDetail[]
  return (list || []).map((v) => normalizeVocabDetail(v))
}

export async function fetchVocabDetail(id: number): Promise<VocabDetail> {
  const raw = (await apiClient.get(`${BASE}/${id}`)) as unknown
  return normalizeVocabDetail(raw)
}

export async function submitVocabReview(
  id: number,
  remembered: boolean,
): Promise<VocabReviewResult> {
  return apiClient.post(`${BASE}/${id}/review`, {
    remembered,
  }) as unknown as VocabReviewResult
}

export interface EnrollResult {
  /** 本次实际入队数；为 0 可能是题库学完或当日上限触顶 */
  count: number
  /** 当日已入队总数（含本次） */
  enrolledToday: number
  /** 当日新词上限（后端常量） */
  dailyCap: number
}

/**
 * 把已发布词库中用户尚未学过的词批量加入 SRS 队列。
 * 受后端 DAILY_NEW_WORD_CAP（默认 50）限制：超过当日剩余配额会被截短。
 * @param themeCode 可选；非空时只从该主题下挑词
 */
export async function enrollNewVocabs(
  level = "ket",
  limit = 20,
  themeCode?: string,
): Promise<EnrollResult> {
  const params: Record<string, string | number> = { level, limit }
  if (themeCode) params.themeCode = themeCode
  return apiClient.post(`${BASE}/enroll-new`, null, {
    params,
  }) as unknown as EnrollResult
}

/** 懒获取词条发音 URL；后端会 TTS 生成 + 文件服务上传后回写并缓存。 */
export async function ensureVocabAudio(
  id: number,
  accent: VocabAccent = "uk",
): Promise<string> {
  const data = (await apiClient.post(`${BASE}/${id}/audio`, null, {
    params: { accent },
  })) as unknown as { url?: string }
  if (!data?.url) throw new Error("发音生成失败")
  return data.url
}

export async function fetchVocabThemes(
  levelCode: string,
): Promise<VocabTheme[]> {
  return apiClient.get(`${BASE}/theme/list`, {
    params: { level: levelCode },
  }) as unknown as VocabTheme[]
}

export interface VocabQuery {
  level: string
  themeCode?: string
  difficulty?: number
  pageNo?: number
  pageSize?: number
}

export interface VocabPage {
  list: VocabListItem[]
  total: number
}

export async function fetchVocabList(
  query: VocabQuery,
): Promise<VocabPage> {
  return apiClient.get(`${BASE}/list`, {
    params: query,
  }) as unknown as VocabPage
}

export interface GradeSentenceResult {
  ok: boolean
  fb: string
  cn: string
  revised: string
}

export async function gradeSentence(
  word: string,
  sentence: string,
  level = "ket",
): Promise<GradeSentenceResult> {
  const res = await pyFetch("/py/grade_sentence", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ word, sentence, level }),
  })
  if (await handleAuthRejection(res)) {
    throw new Error("未登录")
  }
  if (await handleQuotaRejection(res)) {
    throw new Error("今日额度已用完")
  }
  if (!res.ok) throw new Error(`批改失败 (${res.status})`)
  return (await res.json()) as GradeSentenceResult
}

const WORDBOOK_BASE = "/app-api/english/vocab/wordbook"

/** 按单词前缀搜索已发布词库，用于生词本联想补全 + 校验存在。 */
export async function searchVocab(
  word: string,
  level = "ket",
  limit = 10,
): Promise<VocabListItem[]> {
  const kw = word.trim()
  if (!kw) return []
  return apiClient.get(`${BASE}/search`, {
    params: { word: kw, level, limit },
  }) as unknown as VocabListItem[]
}

/** 我的生词本（最新加入在前，含待学/学习进度状态）。 */
export async function fetchWordbook(): Promise<VocabListItem[]> {
  return apiClient.get(WORDBOOK_BASE) as unknown as VocabListItem[]
}

/** 加词到生词本；返回实际新增数（重复加入返回 0）。 */
export async function addWordToWordbook(vocabId: number): Promise<number> {
  return apiClient.post(`${WORDBOOK_BASE}/add`, {
    vocabId,
  }) as unknown as number
}

/** 从生词本移除词条。 */
export async function removeWordFromWordbook(vocabId: number): Promise<void> {
  await apiClient.delete(`${WORDBOOK_BASE}/${vocabId}`)
}

/**
 * 把生词本待学池中尚未学过的词批量加入 SRS 队列。
 * 受后端 DAILY_NEW_WORD_CAP（默认 50）限制；池空返回 count=0。
 */
export async function enrollFromWordbook(limit = 25): Promise<EnrollResult> {
  return apiClient.post(`${BASE}/enroll-wordbook`, null, {
    params: { limit },
  }) as unknown as EnrollResult
}

export async function fetchMyVocabLists(): Promise<UserVocabList[]> {
  return apiClient.get(`${LIST_BASE}/mine`) as unknown as UserVocabList[]
}

export async function createVocabList(data: {
  name: string
  description?: string
}): Promise<number> {
  return apiClient.post(LIST_BASE, data) as unknown as number
}

export async function updateVocabList(data: {
  id: number
  name?: string
  description?: string
}): Promise<void> {
  const { id, ...body } = data
  await apiClient.put(`${LIST_BASE}/${id}`, body)
}

export async function deleteVocabList(id: number): Promise<void> {
  await apiClient.delete(`${LIST_BASE}/${id}`)
}

export async function addVocabsToList(
  listId: number,
  vocabIds: number[],
): Promise<number> {
  return apiClient.post(`${LIST_BASE}/${listId}/items`, {
    vocabIds,
  }) as unknown as number
}

export async function removeVocabFromList(
  listId: number,
  vocabId: number,
): Promise<void> {
  await apiClient.delete(`${LIST_BASE}/${listId}/items/${vocabId}`)
}

export async function fetchVocabListItems(
  listId: number,
  pageNo = 1,
  pageSize = 30,
): Promise<VocabPage> {
  return apiClient.get(`${LIST_BASE}/${listId}/items`, {
    params: { pageNo, pageSize },
  }) as unknown as VocabPage
}
