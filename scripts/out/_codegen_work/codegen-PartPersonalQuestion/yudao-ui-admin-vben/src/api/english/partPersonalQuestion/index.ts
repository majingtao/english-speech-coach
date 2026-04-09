import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPartPersonalQuestionApi {
  /** 个人问答 - 问题信息 */
  export interface PartPersonalQuestion {
    id: number; // 主键
    partId?: number; // esc_exam_part.id
    qNo?: number; // 题号
    questionText?: string; // 问题
    topic: string; // 主题分组
    sort?: number; // 排序
  }
}

/** 查询个人问答 - 问题分页 */
export function getPartPersonalQuestionPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartPersonalQuestionApi.PartPersonalQuestion>>(
    '/english/part-personal-question/page',
    { params },
  );
}

/** 查询个人问答 - 问题详情 */
export function getPartPersonalQuestion(id: number) {
  return requestClient.get<EnglishPartPersonalQuestionApi.PartPersonalQuestion>(
    `/english/part-personal-question/get?id=${id}`,
  );
}

/** 新增个人问答 - 问题 */
export function createPartPersonalQuestion(data: EnglishPartPersonalQuestionApi.PartPersonalQuestion) {
  return requestClient.post('/english/part-personal-question/create', data);
}

/** 修改个人问答 - 问题 */
export function updatePartPersonalQuestion(data: EnglishPartPersonalQuestionApi.PartPersonalQuestion) {
  return requestClient.put('/english/part-personal-question/update', data);
}

/** 删除个人问答 - 问题 */
export function deletePartPersonalQuestion(id: number) {
  return requestClient.delete(`/english/part-personal-question/delete?id=${id}`);
}

/** 批量删除个人问答 - 问题 */
export function deletePartPersonalQuestionList(ids: number[]) {
  return requestClient.delete(
    `/english/part-personal-question/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出个人问答 - 问题 */
export function exportPartPersonalQuestion(params: any) {
  return requestClient.download('/english/part-personal-question/export-excel', { params });
}

