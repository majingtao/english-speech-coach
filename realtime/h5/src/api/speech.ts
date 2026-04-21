import type { QuestionBank } from '@/types/speech'
import request from '@/utils/request'

/** 后端 esc_exam_level.code，如 starters / movers / flyers / ket / pet / nce_1 */
export type YleLevelCode = 'starters' | 'movers' | 'flyers'

export interface QuestionBankQuery {
  /** 保留参数，用于前端区分场景（yle / nce / ...），不会传给后端作为过滤 */
  examSeries?: string
  /** 按级别过滤：starters / movers / flyers / ket / pet / nce_1 */
  levelCode?: string
  /** 按系列过滤：go_flyers / flyers_1 / aep_1 / ... */
  seriesCode?: string
  /** 仅用于页面标题展示 */
  levelName?: string
}

/** 考试系列字典（从后端 list-all-simple 返回） */
export interface ExamSeries {
  id: number
  code: string
  levelCode: string
  name: string
  coverUrl?: string
  publisher?: string
  description?: string
  sort?: number
  status?: number
}

// 从 yudao 后端 API 加载题库（H5 会员端，已登录才可调用）
export async function fetchQuestionBank(query?: QuestionBankQuery): Promise<QuestionBank> {
  const params: Record<string, string> = {}
  if (query?.levelCode) params.levelCode = query.levelCode
  if (query?.seriesCode) params.seriesCode = query.seriesCode
  return request.get<QuestionBank>('/app-api/english/question-bank/all', {
    params,
  })
}

// 获取启用的考试系列字典（用于级别 → 系列级联选择）
export async function fetchExamSeriesList(): Promise<ExamSeries[]> {
  return request.get<ExamSeries[]>('/app-api/english/exam-series/list-all-simple')
}
