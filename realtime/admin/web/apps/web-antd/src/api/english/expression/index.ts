import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishExpressionApi {
  export interface Theme {
    id?: number;
    code?: string;
    nameCn?: string;
    nameEn?: string;
    description?: string;
    levelCode?: string;
    coverUrl?: string;
    sort?: number;
    status?: number;
  }

  export interface Item {
    id?: number;
    themeId?: number;
    code?: string;
    promptEn?: string;
    promptCn?: string;
    functionCode?: string;
    practiceMode?: 'both' | 'speaking' | 'writing';
    difficulty?: number;
    answerJson?: string;
    imageUrlsJson?: string;
    sort?: number;
    status?: number;
    createTime?: string;
  }
}

export function getExpressionThemes(level?: string) {
  return requestClient.get<EnglishExpressionApi.Theme[]>(
    '/english/expression/theme/list',
    { params: { level } },
  );
}

export function createExpressionTheme(data: EnglishExpressionApi.Theme) {
  return requestClient.post('/english/expression/theme/create', data);
}

export function updateExpressionTheme(data: EnglishExpressionApi.Theme) {
  return requestClient.put('/english/expression/theme/update', data);
}

export function deleteExpressionTheme(id: number) {
  return requestClient.delete('/english/expression/theme/delete', { params: { id } });
}

export function getExpressionItemPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishExpressionApi.Item>>(
    '/english/expression/item/page',
    { params },
  );
}

export function getExpressionItem(id: number) {
  return requestClient.get<EnglishExpressionApi.Item>(
    '/english/expression/item/get',
    { params: { id } },
  );
}

export function createExpressionItem(data: EnglishExpressionApi.Item) {
  return requestClient.post('/english/expression/item/create', data);
}

export function updateExpressionItem(data: EnglishExpressionApi.Item) {
  return requestClient.put('/english/expression/item/update', data);
}

export function deleteExpressionItem(id: number) {
  return requestClient.delete('/english/expression/item/delete', { params: { id } });
}
