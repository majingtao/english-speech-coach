import type { QuotaMe } from "@/lib/types/quota"
import { apiClient } from "@/lib/api/client"

export async function fetchMyQuota(): Promise<QuotaMe> {
  return apiClient.get("/app-api/esc/quota/me") as unknown as QuotaMe
}

export function isQuotaCode(code: number | undefined | null): boolean {
  if (code == null) return false
  return code >= 1_040_060_000 && code <= 1_040_060_099
}

export function quotaCodeLabel(code: number, fallbackMsg?: string): string {
  switch (code) {
    case 1_040_060_002:
      return "账号被冻结，请联系管理员"
    case 1_040_060_003:
      return "无效的资源类型"
    case 1_040_060_010:
      return "今日对话额度已用完"
    case 1_040_060_011:
      return "今日语音识别额度已用完"
    case 1_040_060_012:
      return "今日语音合成额度已用完"
    default:
      return fallbackMsg || "额度受限"
  }
}
