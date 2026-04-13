import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishStudentApi } from '#/api/english/student';

import { getDictOptions } from '@vben/hooks';

import { getRangePickerDefaultProps } from '#/utils';

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'username',
      label: '登录账号（短账号）',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入登录账号（短账号）',
      },
    },
    {
      fieldName: 'password',
      label: '密码（BCrypt）',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入密码（BCrypt）',
      },
    },
    {
      fieldName: 'nickname',
      label: '昵称 / 真实姓名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入昵称 / 真实姓名',
      },
    },
    {
      fieldName: 'avatar',
      label: '头像 URL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入头像 URL',
      },
    },
    {
      fieldName: 'gender',
      label: '性别 1=男 2=女',
      component: 'Input',
      componentProps: {
        placeholder: '请输入性别 1=男 2=女',
      },
    },
    {
      fieldName: 'birthday',
      label: '生日',
      component: 'Input',
      componentProps: {
        placeholder: '请输入生日',
      },
    },
    {
      fieldName: 'classId',
      label: '所属班级 esc_class.id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入所属班级 esc_class.id',
      },
    },
    {
      fieldName: 'levelCode',
      label: '当前学习级别 flyers/ket/pet',
      component: 'Input',
      componentProps: {
        placeholder: '请输入当前学习级别 flyers/ket/pet',
      },
    },
    {
      fieldName: 'status',
      label: '0=启用 1=停用',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: [],
        buttonStyle: 'solid',
        optionType: 'button',
      },
    },
    {
      fieldName: 'lastLoginTime',
      label: '最近登录时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'lastLoginIp',
      label: '最近登录 IP',
      component: 'Input',
      componentProps: {
        placeholder: '请输入最近登录 IP',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'username',
      label: '登录账号（短账号）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入登录账号（短账号）',
      },
    },
    {
      fieldName: 'password',
      label: '密码（BCrypt）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入密码（BCrypt）',
      },
    },
    {
      fieldName: 'nickname',
      label: '昵称 / 真实姓名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入昵称 / 真实姓名',
      },
    },
    {
      fieldName: 'avatar',
      label: '头像 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入头像 URL',
      },
    },
    {
      fieldName: 'gender',
      label: '性别 1=男 2=女',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入性别 1=男 2=女',
      },
    },
    {
      fieldName: 'birthday',
      label: '生日',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入生日',
      },
    },
    {
      fieldName: 'classId',
      label: '所属班级 esc_class.id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入所属班级 esc_class.id',
      },
    },
    {
      fieldName: 'levelCode',
      label: '当前学习级别 flyers/ket/pet',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入当前学习级别 flyers/ket/pet',
      },
    },
    {
      fieldName: 'status',
      label: '0=启用 1=停用',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [],
        placeholder: '请选择0=启用 1=停用',
      },
    },
    {
      fieldName: 'lastLoginTime',
      label: '最近登录时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'lastLoginIp',
      label: '最近登录 IP',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入最近登录 IP',
      },
    },
    {
      fieldName: 'createTime',
      label: '添加时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<EnglishStudentApi.Student>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'username',
      title: '登录账号（短账号）',
      minWidth: 120,
    },
    {
      field: 'password',
      title: '密码（BCrypt）',
      minWidth: 120,
    },
    {
      field: 'nickname',
      title: '昵称 / 真实姓名',
      minWidth: 120,
    },
    {
      field: 'avatar',
      title: '头像 URL',
      minWidth: 120,
    },
    {
      field: 'gender',
      title: '性别 1=男 2=女',
      minWidth: 120,
    },
    {
      field: 'birthday',
      title: '生日',
      minWidth: 120,
    },
    {
      field: 'classId',
      title: '所属班级 esc_class.id',
      minWidth: 120,
    },
    {
      field: 'levelCode',
      title: '当前学习级别 flyers/ket/pet',
      minWidth: 120,
    },
    {
      field: 'status',
      title: '0=启用 1=停用',
      minWidth: 120,
    },
    {
      field: 'lastLoginTime',
      title: '最近登录时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'lastLoginIp',
      title: '最近登录 IP',
      minWidth: 120,
    },
    {
      field: 'createTime',
      title: '添加时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
