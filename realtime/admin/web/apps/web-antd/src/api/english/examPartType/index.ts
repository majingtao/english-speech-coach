import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishExamPartTypeApi {
  /** 题型字典信息 */
  export interface ExamPartType {
    id: number; // 主键
    code?: string; // 题型编码
    name?: string; // 显示名
    description: string; // 说明
    sort?: number; // 排序
    status?: number; // 0=启用 1=停用
  }
}

/** 查询题型字典分页 */
export function getExamPartTypePage(params: PageParam) {
  return requestClient.get<PageResult<EnglishExamPartTypeApi.ExamPartType>>(
    '/english/exam-part-type/page',
    { params },
  );
}

/** 查询题型字典详情 */
export function getExamPartType(id: number) {
  return requestClient.get<EnglishExamPartTypeApi.ExamPartType>(
    `/english/exam-part-type/get?id=${id}`,
  );
}

/** 新增题型字典 */
export function createExamPartType(data: EnglishExamPartTypeApi.ExamPartType) {
  return requestClient.post('/english/exam-part-type/create', data);
}

/** 修改题型字典 */
export function updateExamPartType(data: EnglishExamPartTypeApi.ExamPartType) {
  return requestClient.put('/english/exam-part-type/update', data);
}

/** 删除题型字典 */
export function deleteExamPartType(id: number) {
  return requestClient.delete(`/english/exam-part-type/delete?id=${id}`);
}

/** 批量删除题型字典 */
export function deleteExamPartTypeList(ids: number[]) {
  return requestClient.delete(
    `/english/exam-part-type/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出题型字典 */
export function exportExamPartType(params: any) {
  return requestClient.download('/english/exam-part-type/export-excel', { params });
}

