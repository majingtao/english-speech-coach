import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishReadingMaterialApi {
  export interface Material {
    id?: number;
    textEn?: string;
    textCn?: string;
    description?: string;
    materialType?: 'word' | 'phrase' | 'sentence';
    partOfSpeech?:
      | 'noun'
      | 'verb'
      | 'adjective'
      | 'adverb'
      | 'phrase'
      | 'sentence'
      | 'unknown';
    levelCode?: string;
    tagsJson?: string;
    examplesJson?: string;
    wordFormsJson?: string;
    sort?: number;
    status?: number;
    createTime?: string;
  }
}

export function getReadingMaterialPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishReadingMaterialApi.Material>>(
    '/english/reading-material/page',
    { params },
  );
}

export function getReadingMaterial(id: number) {
  return requestClient.get<EnglishReadingMaterialApi.Material>(
    '/english/reading-material/get',
    { params: { id } },
  );
}

export function createReadingMaterial(
  data: EnglishReadingMaterialApi.Material,
) {
  return requestClient.post('/english/reading-material/create', data);
}

export function updateReadingMaterial(
  data: EnglishReadingMaterialApi.Material,
) {
  return requestClient.put('/english/reading-material/update', data);
}

export function deleteReadingMaterial(id: number) {
  return requestClient.delete('/english/reading-material/delete', {
    params: { id },
  });
}
