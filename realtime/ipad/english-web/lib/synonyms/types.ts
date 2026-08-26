export type SynonymPracticeMode = "synonym" | "antonym"

export interface SynonymKnowledgePoint {
  id: string
  mode: SynonymPracticeMode
  level: string
  source: string
  section: string
  left: string
  leftCn: string
  right: string
  rightCn: string
}

export interface SynonymPointProgress {
  seenCount: number
  correctCount: number
  wrongCount: number
  mastered: boolean
  lastAnsweredAt?: string
}
