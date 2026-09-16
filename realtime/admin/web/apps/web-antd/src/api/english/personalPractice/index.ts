import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace PersonalPracticeApi {
  export type PracticeType = 'speaking' | 'writing';

  export interface ReferenceLine {
    en: string;
    cn: string;
  }

  export interface Practice {
    id?: number;
    practiceType: PracticeType;
    title: string;
    promptEn: string;
    promptCn?: string;
    referenceJson: string;
    contentPointsJson?: string;
    minSentences: number;
    minWords?: number;
    sort?: number;
    status?: number;
    createTime?: string;
  }
}

export function getPersonalPracticePage(params: PageParam) {
  return requestClient.get<PageResult<PersonalPracticeApi.Practice>>(
    '/english/personal-practice/page',
    { params },
  );
}

export function getPersonalPractice(id: number) {
  return requestClient.get<PersonalPracticeApi.Practice>(
    '/english/personal-practice/get',
    { params: { id } },
  );
}

export function createPersonalPractice(data: PersonalPracticeApi.Practice) {
  return requestClient.post('/english/personal-practice/create', data);
}

export function updatePersonalPractice(data: PersonalPracticeApi.Practice) {
  return requestClient.put('/english/personal-practice/update', data);
}

export function deletePersonalPractice(id: number) {
  return requestClient.delete('/english/personal-practice/delete', {
    params: { id },
  });
}
