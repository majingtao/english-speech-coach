import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishAiCallLogApi {
  /** AI 调用日志信息 */
  export interface AiCallLog {
    id: number; // 主键
    studentId: number; // esc_student.id（学生发起时）
    userId: number; // system_users.id（管理员/老师发起时）
    serviceType?: string; // 服务类型 llm / asr / tts
    engine: string; // 具体引擎名
    endpoint: string; // 调用端点
    requestSize: number; // 请求字节数
    responseSize: number; // 响应字节数
    durationMs: number; // 耗时（毫秒）
    statusCode: number; // 响应码
    success?: number; // 是否成功 1=成功 0=失败
    errorMessage: string; // 错误信息
    clientIp: string; // 客户端 IP
    callDate?: string | Dayjs; // 调用日期（用于按天配额）
  }
}

/** 查询AI 调用日志分页 */
export function getAiCallLogPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishAiCallLogApi.AiCallLog>>(
    '/english/ai-call-log/page',
    { params },
  );
}

/** 查询AI 调用日志详情 */
export function getAiCallLog(id: number) {
  return requestClient.get<EnglishAiCallLogApi.AiCallLog>(
    `/english/ai-call-log/get?id=${id}`,
  );
}

/** 新增AI 调用日志 */
export function createAiCallLog(data: EnglishAiCallLogApi.AiCallLog) {
  return requestClient.post('/english/ai-call-log/create', data);
}

/** 修改AI 调用日志 */
export function updateAiCallLog(data: EnglishAiCallLogApi.AiCallLog) {
  return requestClient.put('/english/ai-call-log/update', data);
}

/** 删除AI 调用日志 */
export function deleteAiCallLog(id: number) {
  return requestClient.delete(`/english/ai-call-log/delete?id=${id}`);
}

/** 批量删除AI 调用日志 */
export function deleteAiCallLogList(ids: number[]) {
  return requestClient.delete(
    `/english/ai-call-log/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出AI 调用日志 */
export function exportAiCallLog(params: any) {
  return requestClient.download('/english/ai-call-log/export-excel', { params });
}

