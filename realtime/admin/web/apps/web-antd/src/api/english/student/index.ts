import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace EnglishStudentApi {
  /** 学生信息 */
  export interface Student {
    id: number; // 主键
    username?: string; // 登录账号（短账号）
    password?: string; // 密码（BCrypt）
    nickname: string; // 昵称 / 真实姓名
    avatar: string; // 头像 URL
    gender: number; // 性别 1=男 2=女
    birthday: string | Dayjs; // 生日
    classId: number; // 所属班级 esc_class.id
    levelCode: string; // 当前学习级别 flyers/ket/pet
    status?: number; // 0=启用 1=停用
    lastLoginTime: string | Dayjs; // 最近登录时间
    lastLoginIp: string; // 最近登录 IP
  }
}

/** 查询学生分页 */
export function getStudentPage(params: PageParam) {
  return requestClient.get<PageResult<EnglishStudentApi.Student>>(
    '/english/student/page',
    { params },
  );
}

/** 查询学生详情 */
export function getStudent(id: number) {
  return requestClient.get<EnglishStudentApi.Student>(
    `/english/student/get?id=${id}`,
  );
}

/** 新增学生 */
export function createStudent(data: EnglishStudentApi.Student) {
  return requestClient.post('/english/student/create', data);
}

/** 修改学生 */
export function updateStudent(data: EnglishStudentApi.Student) {
  return requestClient.put('/english/student/update', data);
}

/** 删除学生 */
export function deleteStudent(id: number) {
  return requestClient.delete(`/english/student/delete?id=${id}`);
}

/** 批量删除学生 */
export function deleteStudentList(ids: number[]) {
  return requestClient.delete(
    `/english/student/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出学生 */
export function exportStudent(params: any) {
  return requestClient.download('/english/student/export-excel', { params });
}

