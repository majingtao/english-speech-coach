import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishVocabThemeApi {
  export interface VocabTheme {
    id?: number;
    code?: string;
    nameCn?: string;
    nameEn?: string;
    levelCode?: string;
    sort?: number;
    status?: number;
    createTime?: string;
  }
}

/** 分页查询词汇主题 */
export function getVocabThemePage(params: PageParam) {
  return requestClient.get<PageResult<EnglishVocabThemeApi.VocabTheme>>(
    '/english/vocab-theme/page',
    { params },
  );
}

/** 获取主题详情 */
export function getVocabTheme(id: number) {
  return requestClient.get<EnglishVocabThemeApi.VocabTheme>(
    `/english/vocab-theme/get?id=${id}`,
  );
}

/** 按级别查启用主题（下拉用） */
export function listVocabThemeByLevel(levelCode?: string) {
  return requestClient.get<EnglishVocabThemeApi.VocabTheme[]>(
    '/english/vocab-theme/list',
    { params: levelCode ? { levelCode } : {} },
  );
}

/** 新增主题 */
export function createVocabTheme(data: EnglishVocabThemeApi.VocabTheme) {
  return requestClient.post('/english/vocab-theme/create', data);
}

/** 修改主题 */
export function updateVocabTheme(data: EnglishVocabThemeApi.VocabTheme) {
  return requestClient.put('/english/vocab-theme/update', data);
}

/** 删除主题 */
export function deleteVocabTheme(id: number) {
  return requestClient.delete(`/english/vocab-theme/delete?id=${id}`);
}
