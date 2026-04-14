import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishPartFindDiffDifferenceApi {
  /** 找不同 - 差异点信息 */
  export interface PartFindDiffDifference {
    id: number;
    pairId?: number;
    diffNo?: number;
    description: string;
    keyword: string;
    sort?: number;
  }
}

/** 查询找不同 - 差异点分页 */
export function getPartFindDiffDifferencePage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartFindDiffDifferenceApi.PartFindDiffDifference>>(
    '/english/part-find-diff-difference/page',
    { params },
  );
}

/** 查询找不同 - 差异点详情 */
export function getPartFindDiffDifference(id: number) {
  return requestClient.get<EnglishPartFindDiffDifferenceApi.PartFindDiffDifference>(
    `/english/part-find-diff-difference/get?id=${id}`,
  );
}

/** 新增找不同 - 差异点 */
export function createPartFindDiffDifference(data: EnglishPartFindDiffDifferenceApi.PartFindDiffDifference) {
  return requestClient.post('/english/part-find-diff-difference/create', data);
}

/** 修改找不同 - 差异点 */
export function updatePartFindDiffDifference(data: EnglishPartFindDiffDifferenceApi.PartFindDiffDifference) {
  return requestClient.put('/english/part-find-diff-difference/update', data);
}

/** 删除找不同 - 差异点 */
export function deletePartFindDiffDifference(id: number) {
  return requestClient.delete(`/english/part-find-diff-difference/delete?id=${id}`);
}

/** 批量删除找不同 - 差异点 */
export function deletePartFindDiffDifferenceList(ids: number[]) {
  return requestClient.delete(
    `/english/part-find-diff-difference/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出找不同 - 差异点 */
export function exportPartFindDiffDifference(params: any) {
  return requestClient.download('/english/part-find-diff-difference/export-excel', { params });
}
