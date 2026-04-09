import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPartInfoExchangeCardApi {
  /** 信息互换 - 卡片信息 */
  export interface PartInfoExchangeCard {
    id: number; // 主键
    partId?: number; // esc_exam_part.id
    phase?: string; // 阶段：A=学生提问 B=学生回答
    cardTitle: string; // 卡片标题，如 Football Match
    cardImageUrl: string; // 卡片图片 URL
    intro: string; // 卡片说明
    sort?: number; // 排序
  }
}

/** 查询信息互换 - 卡片分页 */
export function getPartInfoExchangeCardPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartInfoExchangeCardApi.PartInfoExchangeCard>>(
    '/english/part-info-exchange-card/page',
    { params },
  );
}

/** 查询信息互换 - 卡片详情 */
export function getPartInfoExchangeCard(id: number) {
  return requestClient.get<EnglishPartInfoExchangeCardApi.PartInfoExchangeCard>(
    `/english/part-info-exchange-card/get?id=${id}`,
  );
}

/** 新增信息互换 - 卡片 */
export function createPartInfoExchangeCard(data: EnglishPartInfoExchangeCardApi.PartInfoExchangeCard) {
  return requestClient.post('/english/part-info-exchange-card/create', data);
}

/** 修改信息互换 - 卡片 */
export function updatePartInfoExchangeCard(data: EnglishPartInfoExchangeCardApi.PartInfoExchangeCard) {
  return requestClient.put('/english/part-info-exchange-card/update', data);
}

/** 删除信息互换 - 卡片 */
export function deletePartInfoExchangeCard(id: number) {
  return requestClient.delete(`/english/part-info-exchange-card/delete?id=${id}`);
}

/** 批量删除信息互换 - 卡片 */
export function deletePartInfoExchangeCardList(ids: number[]) {
  return requestClient.delete(
    `/english/part-info-exchange-card/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出信息互换 - 卡片 */
export function exportPartInfoExchangeCard(params: any) {
  return requestClient.download('/english/part-info-exchange-card/export-excel', { params });
}

