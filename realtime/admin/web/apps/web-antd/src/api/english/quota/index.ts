import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishQuotaApi {
  /** 全局默认配额 */
  export interface QuotaDefault {
    id?: number;
    llmDaily: number;
    asrDailySec: number;
    ttsDailyChars: number;
  }

  /** 单用户配额覆盖 */
  export interface UserQuota {
    id?: number;
    userId: number;
    nickname?: string;
    llmDaily?: null | number;
    asrDailySec?: null | number;
    ttsDailyChars?: null | number;
    enabled: boolean;
    remark?: string;
    createTime?: string;
  }

  /** 分页入参 */
  export interface UserQuotaPageParams extends PageParam {
    userId?: number;
    nickname?: string;
    enabled?: boolean;
  }

  /** 每日用量归档行 */
  export interface UsageDaily {
    id: number;
    userId: number;
    nickname?: string;
    statDate: string;
    llmUsed: number;
    asrUsedSec: number;
    ttsUsedChars: number;
    createTime?: string;
  }

  /** 用量查询分页入参 */
  export interface UsagePageParams extends PageParam {
    userId?: number;
    nickname?: string;
    dateFrom?: string;
    dateTo?: string;
  }
}

/** 查询全局默认配额 */
export function getQuotaDefault() {
  return requestClient.get<EnglishQuotaApi.QuotaDefault>('/esc/quota/default');
}

/** 更新全局默认配额 */
export function updateQuotaDefault(data: EnglishQuotaApi.QuotaDefault) {
  return requestClient.put('/esc/quota/default', data);
}

/** 查询单用户配额分页 */
export function getUserQuotaPage(params: EnglishQuotaApi.UserQuotaPageParams) {
  return requestClient.get<PageResult<EnglishQuotaApi.UserQuota>>(
    '/esc/quota/user/page',
    { params },
  );
}

/** 查询某用户配额 */
export function getUserQuota(userId: number) {
  return requestClient.get<EnglishQuotaApi.UserQuota>(
    `/esc/quota/user/get?userId=${userId}`,
  );
}

/** 新增或更新单用户配额（按 userId upsert） */
export function saveUserQuota(data: EnglishQuotaApi.UserQuota) {
  return requestClient.post('/esc/quota/user/save', data);
}

/** 删除单用户配额覆盖（回归默认） */
export function deleteUserQuota(userId: number) {
  return requestClient.delete(`/esc/quota/user/delete?userId=${userId}`);
}

/** 重置某用户今日已用量（LLM/ASR/TTS 全部清零） */
export function resetUserUsage(userId: number) {
  return requestClient.post(`/esc/quota/user/reset-usage?userId=${userId}`);
}

/** 每日用量归档分页 */
export function getUsagePage(params: EnglishQuotaApi.UsagePageParams) {
  return requestClient.get<PageResult<EnglishQuotaApi.UsageDaily>>(
    '/esc/quota/usage/page',
    { params },
  );
}

/** 手动触发某日归档（date 留空 = 昨天） */
export function archiveUsage(date?: string) {
  const qs = date ? `?date=${date}` : '';
  return requestClient.post<number>(`/esc/quota/usage/archive${qs}`);
}
