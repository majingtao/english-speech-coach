import { apiClient } from "./client"
import { pyFetch, handleAuthRejection } from "./py"

export interface CoachTask {
  id: string; title: string; kind: "interview" | "dialogue"; focus: string; skills: string[]
  prompt: string; seconds: number; options: string[]; opening?: string; hint?: string; sample?: string
}
export interface CoachTurn { role: "learner" | "partner"; text: string; asrText?: string; audioBase64?: string; hasAudio?: boolean }
export interface CoachPronunciation {
  turnIndex: number; status: string; message?: string; accuracy?: number; fluency?: number; prosody?: number
  words?: Array<{ word: string; accuracy: number; start: number; end: number }>
}
export interface CoachFeedback {
  strength: string; improvement: string; revised: string
  skills: Array<{ code: string; observed: boolean; met: boolean; evidence: string; feedback: string }>
  pronunciation: CoachPronunciation[]
}
export interface CoachAttempt {
  id: string; taskId: string; stage: string; parentId: string; assisted: boolean
  status: "pending" | "grading" | "graded" | "failed"; error: string; turns: CoachTurn[]; feedback?: CoachFeedback
}
export interface CoachSession {
  id: string; mode: "practice" | "mock"; status: "active" | "finished"; deadline: number; serverTime: number
  tasks: CoachTask[]; attempts: CoachAttempt[]
}
export interface CoachDashboard {
  tasks: CoachTask[]
  skills: Array<{ code: string; label: string; status: string; observed: number; successes: number; failures: number; nextReviewAt: string | null }>
  plan: { id: string; date: string; minutes: number; items: Array<{ taskId: string; title: string; seconds: number; reason: string; completed: boolean }> }
  sessions: Array<{ id: string; mode: string; status: string; created_at: string }>
}
const BASE = "/app-api/english/speaking-coach"
export const coachDashboard = (minutes: number) => apiClient.get(`${BASE}/dashboard`, { params: { minutes } }) as unknown as Promise<CoachDashboard>
export const coachSession = (id: string) => apiClient.get(`${BASE}/sessions/${id}`) as unknown as Promise<CoachSession>
export const coachStart = (mode: string, taskId?: string, planId?: string) => apiClient.post(`${BASE}/sessions`, { mode, taskId, planId }) as unknown as Promise<CoachSession>
export const coachAction = (id: string, action: string, body = {}) => apiClient.post(`${BASE}/sessions/${id}/${action}`, body, { timeout: 60000 }) as unknown as Promise<CoachSession>
export const coachGrade = (id: string) => apiClient.post(`${BASE}/attempts/${id}/grade`, {}, { timeout: 270000 }) as unknown as Promise<CoachSession>
export const coachPartner = (id: string, taskId: string, turns: CoachTurn[]) => apiClient.post(`${BASE}/sessions/${id}/partner`, {
  taskId, turns: turns.map(({ role, text }) => ({ role, text })),
}, { timeout: 50000 }) as unknown as Promise<{ text: string }>
export async function coachAudio(id: string, index: number) {
  const res = await pyFetch(`${BASE}/attempts/${id}/audio/${index}`)
  if (await handleAuthRejection(res)) throw new Error("登录已失效")
  if (!res.ok || !res.headers.get("content-type")?.includes("audio")) throw new Error("录音加载失败")
  return res.blob()
}
export function audioBase64(audio: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(String(reader.result).split(",")[1])
    reader.onerror = () => reject(new Error("录音读取失败"))
    reader.readAsDataURL(audio)
  })
}
