import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPartCollabTaskApi {
  /** 协作任务 - 主体信息 */
  export interface PartCollabTask {
    id: number; // 主键
    partId?: number; // esc_exam_part.id
    scenario?: string; // 情境描述
    imageUrl: string; // 辅助图片 URL
    instruction: string; // 任务指令
  }
}

/** 查询协作任务 - 主体分页 */
export function getPartCollabTaskPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartCollabTaskApi.PartCollabTask>>(
    '/english/part-collab-task/page',
    { params },
  );
}

/** 查询协作任务 - 主体详情 */
export function getPartCollabTask(id: number) {
  return requestClient.get<EnglishPartCollabTaskApi.PartCollabTask>(
    `/english/part-collab-task/get?id=${id}`,
  );
}

/** 新增协作任务 - 主体 */
export function createPartCollabTask(data: EnglishPartCollabTaskApi.PartCollabTask) {
  return requestClient.post('/english/part-collab-task/create', data);
}

/** 修改协作任务 - 主体 */
export function updatePartCollabTask(data: EnglishPartCollabTaskApi.PartCollabTask) {
  return requestClient.put('/english/part-collab-task/update', data);
}

/** 删除协作任务 - 主体 */
export function deletePartCollabTask(id: number) {
  return requestClient.delete(`/english/part-collab-task/delete?id=${id}`);
}

/** 批量删除协作任务 - 主体 */
export function deletePartCollabTaskList(ids: number[]) {
  return requestClient.delete(
    `/english/part-collab-task/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出协作任务 - 主体 */
export function exportPartCollabTask(params: any) {
  return requestClient.download('/english/part-collab-task/export-excel', { params });
}

