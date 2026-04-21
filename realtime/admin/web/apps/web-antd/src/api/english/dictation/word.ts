import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace DictationWordApi {
  export interface Word {
    id: number;
    en?: string;
    cn?: string;
    pos?: string;
    forms?: string;
    formsJson?: string;
    example?: string;
    difficulty?: number;
    llmStatus?: number;
    createTime?: string;
  }
}

export function getDictationWordPage(params: PageParam) {
  return requestClient.get<PageResult<DictationWordApi.Word>>(
    '/english/dictation/word/page',
    { params },
  );
}

export function getDictationWord(id: number) {
  return requestClient.get<DictationWordApi.Word>(
    `/english/dictation/word/get?id=${id}`,
  );
}

export function createDictationWord(data: DictationWordApi.Word) {
  return requestClient.post('/english/dictation/word/create', data);
}

export function updateDictationWord(data: DictationWordApi.Word) {
  return requestClient.put('/english/dictation/word/update', data);
}

export function deleteDictationWord(id: number) {
  return requestClient.delete(`/english/dictation/word/delete?id=${id}`);
}

export function batchCreateDictationWords(words: string[]) {
  return requestClient.post<number>('/english/dictation/word/batch-create', words);
}
