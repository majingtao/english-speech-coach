import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishExamLevelApi {
  /** 考试级别字典信息 */
  export interface ExamLevel {
    id: number; // 主键
    code?: string; // 级别编码：flyers / ket / pet
    name?: string; // 显示名：Cambridge Flyers / KET / PET
    cefr: string; // CEFR 等级：A2 / B1 ...
    description: string; // 说明
    sort?: number; // 排序
    status?: number; // 0=启用 1=停用
  }
}

/** 查询考试级别字典分页 */
export function getExamLevelPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishExamLevelApi.ExamLevel>>(
    '/english/exam-level/page',
    { params },
  );
}

/** 查询考试级别字典详情 */
export function getExamLevel(id: number) {
  return requestClient.get<EnglishExamLevelApi.ExamLevel>(
    `/english/exam-level/get?id=${id}`,
  );
}

/** 新增考试级别字典 */
export function createExamLevel(data: EnglishExamLevelApi.ExamLevel) {
  return requestClient.post('/english/exam-level/create', data);
}

/** 修改考试级别字典 */
export function updateExamLevel(data: EnglishExamLevelApi.ExamLevel) {
  return requestClient.put('/english/exam-level/update', data);
}

/** 删除考试级别字典 */
export function deleteExamLevel(id: number) {
  return requestClient.delete(`/english/exam-level/delete?id=${id}`);
}

/** 批量删除考试级别字典 */
export function deleteExamLevelList(ids: number[]) {
  return requestClient.delete(
    `/english/exam-level/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出考试级别字典 */
export function exportExamLevel(params: any) {
  return requestClient.download('/english/exam-level/export-excel', { params });
}

