import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishPartInfoExchangeQaApi {
  /** 信息互换 - 问答条目信息 */
  export interface PartInfoExchangeQa {
    id: number;
    cardId?: number;
    qaNo?: number;
    promptLabel: string;
    questionText: string;
    answerText: string;
    sort?: number;
  }
}

/** 查询信息互换 - 问答条目分页 */
export function getPartInfoExchangeQaPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartInfoExchangeQaApi.PartInfoExchangeQa>>(
    '/english/part-info-exchange-qa/page',
    { params },
  );
}

/** 查询信息互换 - 问答条目详情 */
export function getPartInfoExchangeQa(id: number) {
  return requestClient.get<EnglishPartInfoExchangeQaApi.PartInfoExchangeQa>(
    `/english/part-info-exchange-qa/get?id=${id}`,
  );
}

/** 新增信息互换 - 问答条目 */
export function createPartInfoExchangeQa(data: EnglishPartInfoExchangeQaApi.PartInfoExchangeQa) {
  return requestClient.post('/english/part-info-exchange-qa/create', data);
}

/** 修改信息互换 - 问答条目 */
export function updatePartInfoExchangeQa(data: EnglishPartInfoExchangeQaApi.PartInfoExchangeQa) {
  return requestClient.put('/english/part-info-exchange-qa/update', data);
}

/** 删除信息互换 - 问答条目 */
export function deletePartInfoExchangeQa(id: number) {
  return requestClient.delete(`/english/part-info-exchange-qa/delete?id=${id}`);
}

/** 批量删除信息互换 - 问答条目 */
export function deletePartInfoExchangeQaList(ids: number[]) {
  return requestClient.delete(
    `/english/part-info-exchange-qa/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出信息互换 - 问答条目 */
export function exportPartInfoExchangeQa(params: any) {
  return requestClient.download('/english/part-info-exchange-qa/export-excel', { params });
}
