import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPracticeSessionApi {
  /** 练习会话信息 */
  export interface PracticeSession {
    id: number; // 主键
    studentId?: number; // esc_student.id
    examId?: number; // esc_exam.id（具体版本）
    examCode?: string; // 冗余 exam_code 便于跨版本统计
    mode?: string; // 模式：exam / free_talk
    startTime?: string | Dayjs; // 开始时间
    endTime: string | Dayjs; // 结束时间
    durationSec: number; // 总时长（秒）
    status?: number; // 0=进行中 1=已完成 2=已放弃
    finalOverallScore: number; // 整卷综合分 0-100
    finalComment: string; // 整卷综合评语
  }
}

/** 查询练习会话分页 */
export function getPracticeSessionPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPracticeSessionApi.PracticeSession>>(
    '/english/practice-session/page',
    { params },
  );
}

/** 查询练习会话详情 */
export function getPracticeSession(id: number) {
  return requestClient.get<EnglishPracticeSessionApi.PracticeSession>(
    `/english/practice-session/get?id=${id}`,
  );
}

/** 新增练习会话 */
export function createPracticeSession(data: EnglishPracticeSessionApi.PracticeSession) {
  return requestClient.post('/english/practice-session/create', data);
}

/** 修改练习会话 */
export function updatePracticeSession(data: EnglishPracticeSessionApi.PracticeSession) {
  return requestClient.put('/english/practice-session/update', data);
}

/** 删除练习会话 */
export function deletePracticeSession(id: number) {
  return requestClient.delete(`/english/practice-session/delete?id=${id}`);
}

/** 批量删除练习会话 */
export function deletePracticeSessionList(ids: number[]) {
  return requestClient.delete(
    `/english/practice-session/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出练习会话 */
export function exportPracticeSession(params: any) {
  return requestClient.download('/english/practice-session/export-excel', { params });
}

