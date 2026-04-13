import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishExamPartApi {
  /** 试卷 Part 多态头信息 */
  export interface ExamPart {
    id: number; // 主键
    examId?: number; // 所属试卷 esc_exam.id
    partNo?: number; // 题段序号 1/2/3/4
    partType?: string; // 题型编码，引用 esc_exam_part_type.code
    title: string; // Part 显示标题
    intro: string; // Part 引导语 / 考官口述
    timeLimitSec: number; // 时间限制（秒），可为空
    sort?: number; // 排序
  }
}

/** 查询试卷 Part 多态头分页 */
export function getExamPartPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishExamPartApi.ExamPart>>(
    '/english/exam-part/page',
    { params },
  );
}

/** 查询试卷 Part 多态头详情 */
export function getExamPart(id: number) {
  return requestClient.get<EnglishExamPartApi.ExamPart>(
    `/english/exam-part/get?id=${id}`,
  );
}

/** 新增试卷 Part 多态头 */
export function createExamPart(data: EnglishExamPartApi.ExamPart) {
  return requestClient.post('/english/exam-part/create', data);
}

/** 修改试卷 Part 多态头 */
export function updateExamPart(data: EnglishExamPartApi.ExamPart) {
  return requestClient.put('/english/exam-part/update', data);
}

/** 删除试卷 Part 多态头 */
export function deleteExamPart(id: number) {
  return requestClient.delete(`/english/exam-part/delete?id=${id}`);
}

/** 批量删除试卷 Part 多态头 */
export function deleteExamPartList(ids: number[]) {
  return requestClient.delete(
    `/english/exam-part/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出试卷 Part 多态头 */
export function exportExamPart(params: any) {
  return requestClient.download('/english/exam-part/export-excel', { params });
}

