import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishSchoolClassApi {
  /** 班级信息 */
  export interface SchoolClass {
    id: number; // 主键
    name?: string; // 班级名称
    code: string; // 班级编码（可选）
    description: string; // 描述
    status?: number; // 0=启用 1=停用
  }
}

/** 查询班级分页 */
export function getSchoolClassPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishSchoolClassApi.SchoolClass>>(
    '/english/school-class/page',
    { params },
  );
}

/** 查询班级详情 */
export function getSchoolClass(id: number) {
  return requestClient.get<EnglishSchoolClassApi.SchoolClass>(
    `/english/school-class/get?id=${id}`,
  );
}

/** 新增班级 */
export function createSchoolClass(data: EnglishSchoolClassApi.SchoolClass) {
  return requestClient.post('/english/school-class/create', data);
}

/** 修改班级 */
export function updateSchoolClass(data: EnglishSchoolClassApi.SchoolClass) {
  return requestClient.put('/english/school-class/update', data);
}

/** 删除班级 */
export function deleteSchoolClass(id: number) {
  return requestClient.delete(`/english/school-class/delete?id=${id}`);
}

/** 批量删除班级 */
export function deleteSchoolClassList(ids: number[]) {
  return requestClient.delete(
    `/english/school-class/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出班级 */
export function exportSchoolClass(params: any) {
  return requestClient.download('/english/school-class/export-excel', { params });
}

