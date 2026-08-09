"use client"

import { useCallback, useEffect, useState } from "react"
import type { QuestionBank } from "@/lib/types/speech"
import type { QuestionBankQuery } from "@/lib/api/speech"
import { fetchQuestionBank } from "@/lib/api/speech"
import { useAiConfig } from "./use-ai-config"

export function useExamConfig(query?: QuestionBankQuery) {
  const aiConfig = useAiConfig()
  const [questionBank, setQuestionBank] = useState<QuestionBank | null>(null)
  const [voiceOnly, setVoiceOnly] = useState(false)
  const [hideChat, setHideChat] = useState(false)

  const loadQuestionBank = useCallback(async () => {
    try {
      setQuestionBank(await fetchQuestionBank(query))
    } catch (error) {
      console.error("题库加载失败", error)
    }
  }, [query])

  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { void loadQuestionBank() }, [loadQuestionBank])

  return {
    ...aiConfig,
    questionBank,
    voiceOnly,
    setVoiceOnly,
    hideChat,
    setHideChat,
  }
}
