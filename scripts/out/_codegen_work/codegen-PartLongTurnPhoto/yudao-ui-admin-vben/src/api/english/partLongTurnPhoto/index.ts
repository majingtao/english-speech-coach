import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPartLongTurnPhotoApi {
  /** 独立长答 - 图片描述信息 */
  export interface PartLongTurnPhoto {
    id: number; // 主键
    partId?: number; // esc_exam_part.id
    candidateRole?: string; // 面向的考生 A / B
    topic: string; // 主题
    imageUrl?: string; // 图片 URL
    instruction: string; // 考官指令
    sort?: number; // 排序
  }
}

/** 查询独立长答 - 图片描述分页 */
export function getPartLongTurnPhotoPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartLongTurnPhotoApi.PartLongTurnPhoto>>(
    '/english/part-long-turn-photo/page',
    { params },
  );
}

/** 查询独立长答 - 图片描述详情 */
export function getPartLongTurnPhoto(id: number) {
  return requestClient.get<EnglishPartLongTurnPhotoApi.PartLongTurnPhoto>(
    `/english/part-long-turn-photo/get?id=${id}`,
  );
}

/** 新增独立长答 - 图片描述 */
export function createPartLongTurnPhoto(data: EnglishPartLongTurnPhotoApi.PartLongTurnPhoto) {
  return requestClient.post('/english/part-long-turn-photo/create', data);
}

/** 修改独立长答 - 图片描述 */
export function updatePartLongTurnPhoto(data: EnglishPartLongTurnPhotoApi.PartLongTurnPhoto) {
  return requestClient.put('/english/part-long-turn-photo/update', data);
}

/** 删除独立长答 - 图片描述 */
export function deletePartLongTurnPhoto(id: number) {
  return requestClient.delete(`/english/part-long-turn-photo/delete?id=${id}`);
}

/** 批量删除独立长答 - 图片描述 */
export function deletePartLongTurnPhotoList(ids: number[]) {
  return requestClient.delete(
    `/english/part-long-turn-photo/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出独立长答 - 图片描述 */
export function exportPartLongTurnPhoto(params: any) {
  return requestClient.download('/english/part-long-turn-photo/export-excel', { params });
}

