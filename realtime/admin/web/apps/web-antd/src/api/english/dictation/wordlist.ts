import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace DictationWordlistApi {
  export interface Wordlist {
    id: number;
    name?: string;
    categoryType?: string;
    schoolLevel?: string;
    grade?: number;
    semester?: number;
    edition?: string;
    unitLabel?: string;
    examLevelCode?: string;
    description?: string;
    wordCount?: number;
    sort?: number;
    status?: number;
    createTime?: string;
  }
}

export function getDictationWordlistPage(params: PageParam) {
  return requestClient.get<PageResult<DictationWordlistApi.Wordlist>>(
    '/english/dictation/wordlist/page',
    { params },
  );
}

export function getDictationWordlist(id: number) {
  return requestClient.get<DictationWordlistApi.Wordlist>(
    `/english/dictation/wordlist/get?id=${id}`,
  );
}

export function createDictationWordlist(data: DictationWordlistApi.Wordlist) {
  return requestClient.post('/english/dictation/wordlist/create', data);
}

export function updateDictationWordlist(data: DictationWordlistApi.Wordlist) {
  return requestClient.put('/english/dictation/wordlist/update', data);
}

export function deleteDictationWordlist(id: number) {
  return requestClient.delete(`/english/dictation/wordlist/delete?id=${id}`);
}

export function addWordsToWordlist(wordlistId: number, wordIds: number[]) {
  return requestClient.post('/english/dictation/wordlist/add-words', { wordlistId, wordIds });
}

export function removeWordFromWordlist(wordlistId: number, wordId: number) {
  return requestClient.delete(`/english/dictation/wordlist/remove-word?wordlistId=${wordlistId}&wordId=${wordId}`);
}

export function getWordlistWords(wordlistId: number) {
  return requestClient.get<any[]>(`/english/dictation/wordlist/words?wordlistId=${wordlistId}`);
}

export function importJsonToWordlist(wordlistId: number, words: any[]) {
  return requestClient.post<string>('/english/dictation/wordlist/import-json', { wordlistId, words });
}
