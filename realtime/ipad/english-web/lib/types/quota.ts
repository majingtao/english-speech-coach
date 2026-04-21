export interface QuotaMe {
  enabled: boolean
  llmDaily: number
  asrDailySec: number
  ttsDailyChars: number
  llmUsed: number
  asrUsedSec: number
  ttsUsedChars: number
  llmRemaining: number
  asrRemainingSec: number
  ttsRemainingChars: number
}
