import type { PageParam, PageResult } from '@vben/request';
import { requestClient } from '#/api/request';

export namespace EnglishSynonymApi {
  export interface Point {
    id?: number;
    code: string;
    mode: 'synonym' | 'antonym';
    levelCode: string;
    source: string;
    sectionName: string;
    leftText: string;
    leftCn?: string;
    rightText: string;
    rightCn?: string;
    sort?: number;
    status?: number;
  }
}

export const getSynonymPointPage = (params: PageParam & Record<string, any>) =>
  requestClient.get<PageResult<EnglishSynonymApi.Point>>('/english/synonym/point/page', { params });
export const getSynonymPoint = (id: number) =>
  requestClient.get<EnglishSynonymApi.Point>('/english/synonym/point/get', { params: { id } });
export const createSynonymPoint = (data: EnglishSynonymApi.Point) =>
  requestClient.post('/english/synonym/point/create', data);
export const updateSynonymPoint = (data: EnglishSynonymApi.Point) =>
  requestClient.put('/english/synonym/point/update', data);
export const deleteSynonymPoint = (id: number) =>
  requestClient.delete('/english/synonym/point/delete', { params: { id } });
