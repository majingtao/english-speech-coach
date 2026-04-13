import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPartGeneralConvoTopicApi {
  /** 一般对话 - 主题信息 */
  export interface PartGeneralConvoTopic {
    id: number; // 主键
    partId?: number; // esc_exam_part.id
    topic?: string; // 对话主题
    intro: string; // 主题引导
    sort?: number; // 排序
  }
}

/** 查询一般对话 - 主题分页 */
export function getPartGeneralConvoTopicPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartGeneralConvoTopicApi.PartGeneralConvoTopic>>(
    '/english/part-general-convo-topic/page',
    { params },
  );
}

/** 查询一般对话 - 主题详情 */
export function getPartGeneralConvoTopic(id: number) {
  return requestClient.get<EnglishPartGeneralConvoTopicApi.PartGeneralConvoTopic>(
    `/english/part-general-convo-topic/get?id=${id}`,
  );
}

/** 新增一般对话 - 主题 */
export function createPartGeneralConvoTopic(data: EnglishPartGeneralConvoTopicApi.PartGeneralConvoTopic) {
  return requestClient.post('/english/part-general-convo-topic/create', data);
}

/** 修改一般对话 - 主题 */
export function updatePartGeneralConvoTopic(data: EnglishPartGeneralConvoTopicApi.PartGeneralConvoTopic) {
  return requestClient.put('/english/part-general-convo-topic/update', data);
}

/** 删除一般对话 - 主题 */
export function deletePartGeneralConvoTopic(id: number) {
  return requestClient.delete(`/english/part-general-convo-topic/delete?id=${id}`);
}

/** 批量删除一般对话 - 主题 */
export function deletePartGeneralConvoTopicList(ids: number[]) {
  return requestClient.delete(
    `/english/part-general-convo-topic/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出一般对话 - 主题 */
export function exportPartGeneralConvoTopic(params: any) {
  return requestClient.download('/english/part-general-convo-topic/export-excel', { params });
}

