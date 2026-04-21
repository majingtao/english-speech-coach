import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishExamSeriesApi {
  /** 考试系列字典信息 */
  export interface ExamSeries {
    id: number;
    code?: string;
    levelCode?: string;
    name?: string;
    coverUrl?: string;
    publisher?: string;
    description?: string;
    sort?: number;
    status?: number;
  }
}

/** 查询考试系列字典分页 */
export function getExamSeriesPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishExamSeriesApi.ExamSeries>>(
    '/english/exam-series/page',
    { params },
  );
}

/** 查询考试系列字典详情 */
export function getExamSeries(id: number) {
  return requestClient.get<EnglishExamSeriesApi.ExamSeries>(
    `/english/exam-series/get?id=${id}`,
  );
}

/** 全量启用列表（用于级联下拉） */
export function listAllSimpleExamSeries() {
  return requestClient.get<EnglishExamSeriesApi.ExamSeries[]>(
    '/english/exam-series/list-all-simple',
  );
}

/** 新增考试系列字典 */
export function createExamSeries(data: EnglishExamSeriesApi.ExamSeries) {
  return requestClient.post('/english/exam-series/create', data);
}

/** 修改考试系列字典 */
export function updateExamSeries(data: EnglishExamSeriesApi.ExamSeries) {
  return requestClient.put('/english/exam-series/update', data);
}

/** 删除考试系列字典 */
export function deleteExamSeries(id: number) {
  return requestClient.delete(`/english/exam-series/delete?id=${id}`);
}

/** 批量删除考试系列字典 */
export function deleteExamSeriesList(ids: number[]) {
  return requestClient.delete(
    `/english/exam-series/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出考试系列字典 */
export function exportExamSeries(params: any) {
  return requestClient.download('/english/exam-series/export-excel', {
    params,
  });
}
