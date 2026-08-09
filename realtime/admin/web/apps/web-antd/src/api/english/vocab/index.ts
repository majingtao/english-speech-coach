import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishVocabApi {
  export interface Vocab {
    id?: number;
    word?: string;
    levelCode?: string;
    pos?: string;
    difficulty?: number;
    /** 1=三会(receptive) / 2=四会(productive，必须会拼写) */
    masteryLevel?: number;
    cefr?: string;
    cefrList?: string;
    status?: number;
    sort?: number;
    contentJson?: string;
    formsJson?: string;
    audioUkUrl?: string;
    audioUsUrl?: string;
    themeIds?: number[];
    createTime?: string;
    updateTime?: string;
  }

  export interface VocabExample {
    en: string;
    cn: string;
  }

  export interface VocabFormsObj {
    base?: string;
    past?: string;
    past_participle?: string;
    ing?: string;
    third_person?: string;
    singular?: string;
    plural?: string;
    comparative?: string;
    superlative?: string;
  }

  export interface VocabContentObj {
    definition_cn?: string;
    definition_en?: string;
    ipa?: string;
    forms?: VocabFormsObj;
    examples?: VocabExample[];
    gen_at?: string;
    model_used?: string;
  }

  export interface BatchImportItem {
    word: string;
    /** 可选；不填用批次 levelCode */
    level?: string;
    pos?: string;
    difficulty?: number;
    /** 1=三会 / 2=四会；不填默认 1 */
    masteryLevel?: number;
    cefr?: string;
    cefrList?: string[];
    themes?: string[];
    status?: number;
    /** LLM 生成的内容；存为 content_json，forms 节点同步到 forms_json */
    content?: VocabContentObj;
    audioUkUrl?: string;
    audioUsUrl?: string;
  }

  export interface BatchImportReq {
    levelCode: string;
    items: BatchImportItem[];
  }
}

/** 分页查询词汇 */
export function getVocabPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishVocabApi.Vocab>>(
    '/english/vocab/page',
    { params },
  );
}

/** 获取词条详情（含 themeIds） */
export function getVocab(id: number) {
  return requestClient.get<EnglishVocabApi.Vocab>(
    `/english/vocab/get?id=${id}`,
  );
}

/** 新增词条 */
export function createVocab(data: EnglishVocabApi.Vocab) {
  return requestClient.post('/english/vocab/create', data);
}

/** 修改词条 */
export function updateVocab(data: EnglishVocabApi.Vocab) {
  return requestClient.put('/english/vocab/update', data);
}

/** 删除词条 */
export function deleteVocab(id: number) {
  return requestClient.delete(`/english/vocab/delete?id=${id}`);
}

/** 重新生成 content_json */
export function regenVocabContent(id: number) {
  return requestClient.post(`/english/vocab/regen-content/${id}`);
}

/** 批量导入词条 */
export function batchImportVocab(data: EnglishVocabApi.BatchImportReq) {
  return requestClient.post<number>('/english/vocab/batch-import', data);
}
