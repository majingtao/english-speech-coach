import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishPartPersonalQaSampleApi {
  /** 个人问答 - 示例答案信息 */
  export interface PartPersonalQaSample {
    id: number;
    questionId?: number;
    sampleNo?: number;
    sampleText: string;
    levelTag: string;
    sort?: number;
  }
}

/** 查询个人问答 - 示例答案分页 */
export function getPartPersonalQaSamplePage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartPersonalQaSampleApi.PartPersonalQaSample>>(
    '/english/part-personal-qa-sample/page',
    { params },
  );
}

/** 查询个人问答 - 示例答案详情 */
export function getPartPersonalQaSample(id: number) {
  return requestClient.get<EnglishPartPersonalQaSampleApi.PartPersonalQaSample>(
    `/english/part-personal-qa-sample/get?id=${id}`,
  );
}

/** 新增个人问答 - 示例答案 */
export function createPartPersonalQaSample(data: EnglishPartPersonalQaSampleApi.PartPersonalQaSample) {
  return requestClient.post('/english/part-personal-qa-sample/create', data);
}

/** 修改个人问答 - 示例答案 */
export function updatePartPersonalQaSample(data: EnglishPartPersonalQaSampleApi.PartPersonalQaSample) {
  return requestClient.put('/english/part-personal-qa-sample/update', data);
}

/** 删除个人问答 - 示例答案 */
export function deletePartPersonalQaSample(id: number) {
  return requestClient.delete(`/english/part-personal-qa-sample/delete?id=${id}`);
}

/** 批量删除个人问答 - 示例答案 */
export function deletePartPersonalQaSampleList(ids: number[]) {
  return requestClient.delete(
    `/english/part-personal-qa-sample/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出个人问答 - 示例答案 */
export function exportPartPersonalQaSample(params: any) {
  return requestClient.download('/english/part-personal-qa-sample/export-excel', { params });
}
