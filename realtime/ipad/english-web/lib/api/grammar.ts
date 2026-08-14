import { apiClient } from "@/lib/api/client"

export interface GrammarPoint { id: number; code: string; nameCn: string; nameEn: string; description: string; difficultyConfigJson?: string }
export interface GrammarQuestion { id: number; grammarPointId: number; questionType: "single_choice" | "text_input"; difficulty: number; instruction: string; stem: string; optionsJson?: string; mediaJson?: string }
export interface GrammarAnswer { correct: boolean; acceptedAnswers: string[]; explanationZh: string; ruleText: string; errorHint: string }
export const fetchGrammarPoints = () => apiClient.get<GrammarPoint[], GrammarPoint[]>("/app-api/english/grammar/point/list", { params: { level: "ket" } })
export const fetchGrammarPractice = (grammarPointId: number, difficulty = 1, count = 10) => apiClient.get<GrammarQuestion[], GrammarQuestion[]>("/app-api/english/grammar/practice", { params: { grammarPointId, difficulty, count } })
export const submitGrammarAnswer = (id: number, answer: string, durationSeconds: number) => apiClient.post<GrammarAnswer, GrammarAnswer>(`/app-api/english/grammar/question/${id}/answer`, { answer, durationSeconds })
