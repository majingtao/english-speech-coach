import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishPartTellStoryFrameApi {
  /** 讲故事 - 单帧信息 */
  export interface PartTellStoryFrame {
    id: number; // 主键
    partId?: number; // esc_exam_part.id
    frameNo?: number; // 帧序号 1~5
    imageUrl: string; // 该帧图片 URL
    sentenceText: string; // 该帧参考句子
    hint: string; // 提示词 / 关键词
    sort?: number; // 排序
  }
}

/** 查询讲故事 - 单帧分页 */
export function getPartTellStoryFramePage(params: PageParam) {
  return requestClient.get<PageResult<EnglishPartTellStoryFrameApi.PartTellStoryFrame>>(
    '/english/part-tell-story-frame/page',
    { params },
  );
}

/** 查询讲故事 - 单帧详情 */
export function getPartTellStoryFrame(id: number) {
  return requestClient.get<EnglishPartTellStoryFrameApi.PartTellStoryFrame>(
    `/english/part-tell-story-frame/get?id=${id}`,
  );
}

/** 新增讲故事 - 单帧 */
export function createPartTellStoryFrame(data: EnglishPartTellStoryFrameApi.PartTellStoryFrame) {
  return requestClient.post('/english/part-tell-story-frame/create', data);
}

/** 修改讲故事 - 单帧 */
export function updatePartTellStoryFrame(data: EnglishPartTellStoryFrameApi.PartTellStoryFrame) {
  return requestClient.put('/english/part-tell-story-frame/update', data);
}

/** 删除讲故事 - 单帧 */
export function deletePartTellStoryFrame(id: number) {
  return requestClient.delete(`/english/part-tell-story-frame/delete?id=${id}`);
}

/** 批量删除讲故事 - 单帧 */
export function deletePartTellStoryFrameList(ids: number[]) {
  return requestClient.delete(
    `/english/part-tell-story-frame/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出讲故事 - 单帧 */
export function exportPartTellStoryFrame(params: any) {
  return requestClient.download('/english/part-tell-story-frame/export-excel', { params });
}

