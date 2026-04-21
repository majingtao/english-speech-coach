import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishAiModelApi {
  export interface AiModel {
    id: number;
    type?: string;
    provider?: string;
    modelKey?: string;
    label?: string;
    configJson?: string;
    isDefault?: number;
    sort?: number;
    status?: number;
  }
}

/** 查询AI模型配置分页 */
export function getAiModelPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishAiModelApi.AiModel>>(
    '/english/ai-model/page',
    { params },
  );
}

/** 查询AI模型配置详情 */
export function getAiModel(id: number) {
  return requestClient.get<EnglishAiModelApi.AiModel>(
    `/english/ai-model/get?id=${id}`,
  );
}

/** 新增AI模型配置 */
export function createAiModel(data: EnglishAiModelApi.AiModel) {
  return requestClient.post('/english/ai-model/create', data);
}

/** 修改AI模型配置 */
export function updateAiModel(data: EnglishAiModelApi.AiModel) {
  return requestClient.put('/english/ai-model/update', data);
}

/** 删除AI模型配置 */
export function deleteAiModel(id: number) {
  return requestClient.delete(`/english/ai-model/delete?id=${id}`);
}

/** 批量删除AI模型配置 */
export function deleteAiModelList(ids: number[]) {
  return requestClient.delete(
    `/english/ai-model/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出AI模型配置 */
export function exportAiModel(params: any) {
  return requestClient.download('/english/ai-model/export-excel', { params });
}
