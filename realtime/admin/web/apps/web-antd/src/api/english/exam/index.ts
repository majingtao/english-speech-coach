import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace EnglishExamApi {
  export interface Exam {
    id: number;
    examCode?: string;
    version?: number;
    isActive?: number;
    levelCode?: string;
    seriesCode?: string;
    label?: string;
    source?: string;
    year?: string;
    description?: string;
    status?: number;
    contentJson?: string;
    createTime?: string;
  }
}

/** 查询试卷分页 */
export function getExamPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishExamApi.Exam>>(
    '/english/exam/page',
    { params },
  );
}

/** 查询试卷详情 */
export function getExam(id: number) {
  return requestClient.get<EnglishExamApi.Exam>(
    `/english/exam/get?id=${id}`,
  );
}

/** 新增试卷 */
export function createExam(data: EnglishExamApi.Exam) {
  return requestClient.post('/english/exam/create', data);
}

/** 修改试卷 */
export function updateExam(data: EnglishExamApi.Exam) {
  return requestClient.put('/english/exam/update', data);
}

/** 删除试卷 */
export function deleteExam(id: number) {
  return requestClient.delete(`/english/exam/delete?id=${id}`);
}

/** 获取试卷 JSON 内容 */
export function getExamContent(id: number) {
  return requestClient.get<EnglishExamApi.Exam>(
    `/english/exam/content?id=${id}`,
  );
}

/** 保存试卷 JSON 内容 */
export function updateExamContent(id: number, contentJson: string) {
  return requestClient.put('/english/exam/content', { id, contentJson });
}

/** 一次性迁移：多表数据写入 content_json */
export function migrateToJson() {
  return requestClient.post<string>('/english/exam/migrate-to-json');
}
