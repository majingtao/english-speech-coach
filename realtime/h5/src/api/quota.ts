import request from '@/utils/request'

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

// H5 会员端：/app-api/esc/quota/me，返回生效配额 + 今日已用
export async function fetchMyQuota(): Promise<QuotaMe> {
  return request.get<QuotaMe>('/app-api/esc/quota/me')
}

// 判断某业务码是否为配额业务码（1_040_060_000 ~ 1_040_060_099）
export function isQuotaCode(code: number | undefined | null): boolean {
  if (code == null)
    return false
  return code >= 1_040_060_000 && code <= 1_040_060_099
}

// 将 code 映射到简短中文提示（fallback 到 msg）
export function quotaCodeLabel(code: number, fallbackMsg?: string): string {
  switch (code) {
    case 1_040_060_002: return '账号被冻结，请联系管理员'
    case 1_040_060_003: return '无效的资源类型'
    case 1_040_060_010: return '今日对话额度已用完'
    case 1_040_060_011: return '今日语音识别额度已用完'
    case 1_040_060_012: return '今日语音合成额度已用完'
    default: return fallbackMsg || '额度受限'
  }
}
