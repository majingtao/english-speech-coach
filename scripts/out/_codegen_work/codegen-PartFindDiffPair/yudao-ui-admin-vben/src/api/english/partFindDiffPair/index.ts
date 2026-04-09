import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPartFindDiffPairApi {
  /** 找不同 - 图对信息 */
  export interface PartFindDiffPair {
    id: number; // 主键
    partId?: number; // esc_exam_part.id
    pairNo?: number; // 图对序号
    imageAUrl: string; // 左图 URL
    imageBUrl: string; // 右图 URL
    topic: string; // 主题，如 Two pictures of a kitchen
    sort?: number; // 排序
  }
}

/** 查询找不同 - 图对分页 */
export function getPartFindDiffPairPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartFindDiffPairApi.PartFindDiffPair>>(
    '/english/part-find-diff-pair/page',
    { params },
  );
}

/** 查询找不同 - 图对详情 */
export function getPartFindDiffPair(id: number) {
  return requestClient.get<EnglishPartFindDiffPairApi.PartFindDiffPair>(
    `/english/part-find-diff-pair/get?id=${id}`,
  );
}

/** 新增找不同 - 图对 */
export function createPartFindDiffPair(data: EnglishPartFindDiffPairApi.PartFindDiffPair) {
  return requestClient.post('/english/part-find-diff-pair/create', data);
}

/** 修改找不同 - 图对 */
export function updatePartFindDiffPair(data: EnglishPartFindDiffPairApi.PartFindDiffPair) {
  return requestClient.put('/english/part-find-diff-pair/update', data);
}

/** 删除找不同 - 图对 */
export function deletePartFindDiffPair(id: number) {
  return requestClient.delete(`/english/part-find-diff-pair/delete?id=${id}`);
}

/** 批量删除找不同 - 图对 */
export function deletePartFindDiffPairList(ids: number[]) {
  return requestClient.delete(
    `/english/part-find-diff-pair/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出找不同 - 图对 */
export function exportPartFindDiffPair(params: any) {
  return requestClient.download('/english/part-find-diff-pair/export-excel', { params });
}

