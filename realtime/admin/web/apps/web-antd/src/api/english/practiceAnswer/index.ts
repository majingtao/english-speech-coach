import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPracticeAnswerApi {
  /** 练习单题作答信息 */
  export interface PracticeAnswer {
    id: number; // 主键
    sessionId?: number; // esc_practice_session.id
    partId?: number; // esc_exam_part.id
    itemRefTable: string; // 引用题目所在子表名
    itemRefId: number; // 引用题目主键
    seq?: number; // 该 part 内的顺序
    questionSnapshot: string; // 题目快照（防止题库后续变动）
    audioUrl: string; // 学生录音 URL
    asrText: string; // ASR 转写文本
    asrEngine: string; // ASR 引擎
    asrDurationMs: number; // ASR 处理耗时（毫秒）
    scoreGrammarVocab: number; // 语法词汇 0-100
    scorePronunciation: number; // 发音 0-100
    scoreInteraction: number; // 互动 0-100
    scoreDiscourse: number; // 篇章组织 0-100（PET 及以上）
    scoreOverall: number; // 综合分 0-100
    feedbackText: string; // LLM 中文反馈
    correctedText: string; // LLM 修正版本
    llmEngine: string; // LLM 引擎 / 模型名
    llmRawResponse: string; // 原始 LLM 响应（调试 / 审计）
  }
}

/** 查询练习单题作答分页 */
export function getPracticeAnswerPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPracticeAnswerApi.PracticeAnswer>>(
    '/english/practice-answer/page',
    { params },
  );
}

/** 查询练习单题作答详情 */
export function getPracticeAnswer(id: number) {
  return requestClient.get<EnglishPracticeAnswerApi.PracticeAnswer>(
    `/english/practice-answer/get?id=${id}`,
  );
}

/** 新增练习单题作答 */
export function createPracticeAnswer(data: EnglishPracticeAnswerApi.PracticeAnswer) {
  return requestClient.post('/english/practice-answer/create', data);
}

/** 修改练习单题作答 */
export function updatePracticeAnswer(data: EnglishPracticeAnswerApi.PracticeAnswer) {
  return requestClient.put('/english/practice-answer/update', data);
}

/** 删除练习单题作答 */
export function deletePracticeAnswer(id: number) {
  return requestClient.delete(`/english/practice-answer/delete?id=${id}`);
}

/** 批量删除练习单题作答 */
export function deletePracticeAnswerList(ids: number[]) {
  return requestClient.delete(
    `/english/practice-answer/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出练习单题作答 */
export function exportPracticeAnswer(params: any) {
  return requestClient.download('/english/practice-answer/export-excel', { params });
}

