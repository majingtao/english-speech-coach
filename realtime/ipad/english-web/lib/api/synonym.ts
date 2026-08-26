import { apiClient } from "@/lib/api/client"

export type SynonymPracticeMode = "synonym" | "antonym"
export type SynonymPointStatus = "new" | "learning" | "done"

export interface SynonymPoint {
  id: number
  code: string
  mode: SynonymPracticeMode
  levelCode: string
  source: string
  sectionName: string
  leftText: string
  leftCn: string
  rightText: string
  rightCn: string
  sort: number
  seenCount: number
  correctCount: number
  wrongCount: number
  mastered: boolean
  status: SynonymPointStatus
}

export interface SynonymAnswerResp {
  correct: boolean
  correctAnswer: string
  correctAnswerCn: string
  explanation: string
  seenCount: number
  correctCount: number
  wrongCount: number
  mastered: boolean
}

export const fetchSynonymQueue = (mode?: SynonymPracticeMode) =>
  apiClient.get<SynonymPoint[], SynonymPoint[]>("/app-api/english/synonym/queue", {
    params: { level: "ket", mode },
  })

export const submitSynonymAnswer = (id: number, answer: string, durationSeconds = 0) =>
  apiClient.post<SynonymAnswerResp, SynonymAnswerResp>(`/app-api/english/synonym/point/${id}/answer`, {
    answer,
    durationSeconds,
  })

export const ensureSynonymAudio = (text: string, accent: "uk" | "us") =>
  apiClient
    .get<{ url: string }, { url: string }>("/app-api/english/synonym/audio", {
      params: { level: "ket", text, accent },
    })
    .then((data) => data.url)
