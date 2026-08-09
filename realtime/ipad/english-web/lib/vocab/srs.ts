const INTERVAL_DAYS = [1, 2, 4, 7, 15, 30]

export function previewNextInterval(
  remembered: boolean,
  currentRepetitions: number,
): number {
  if (!remembered) return 0
  const idx = Math.min(currentRepetitions, INTERVAL_DAYS.length - 1)
  return INTERVAL_DAYS[idx]
}

export function formatDueLabel(nextReviewAt?: string): string {
  if (!nextReviewAt) return "新词"
  const next = new Date(nextReviewAt).getTime()
  if (!Number.isFinite(next)) return ""
  const now = Date.now()
  const dayMs = 24 * 60 * 60 * 1000
  const diffDays = Math.round((next - now) / dayMs)
  if (diffDays <= 0) return "今日待复习"
  if (diffDays === 1) return "明天复习"
  return `${diffDays} 天后复习`
}

export function progressStatusLabel(status?: number): string {
  if (status === 2) return "已掌握"
  if (status === 1) return "学习中"
  return "新词"
}
