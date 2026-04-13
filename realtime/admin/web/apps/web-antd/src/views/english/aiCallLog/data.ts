import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishAiCallLogApi } from '#/api/english/aiCallLog';

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
      fieldName: 'studentId',
      label: 'esc_student.id（学生发起时）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入esc_student.id（学生发起时）',
      },
    },
    {
      fieldName: 'userId',
      label: 'system_users.id（管理员/老师发起时）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入system_users.id（管理员/老师发起时）',
      },
    },
    {
      fieldName: 'serviceType',
      label: '服务类型 llm / asr / tts',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: [],
        placeholder: '请选择服务类型 llm / asr / tts',
      },
    },
    {
      fieldName: 'engine',
      label: '具体引擎名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入具体引擎名',
      },
    },
    {
      fieldName: 'endpoint',
      label: '调用端点',
      component: 'Input',
      componentProps: {
        placeholder: '请输入调用端点',
      },
    },
    {
      fieldName: 'requestSize',
      label: '请求字节数',
      component: 'Input',
      componentProps: {
        placeholder: '请输入请求字节数',
      },
    },
    {
      fieldName: 'responseSize',
      label: '响应字节数',
      component: 'Input',
      componentProps: {
        placeholder: '请输入响应字节数',
      },
    },
    {
      fieldName: 'durationMs',
      label: '耗时（毫秒）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入耗时（毫秒）',
      },
    },
    {
      fieldName: 'statusCode',
      label: '响应码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入响应码',
      },
    },
    {
      fieldName: 'success',
      label: '是否成功 1=成功 0=失败',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入是否成功 1=成功 0=失败',
      },
    },
    {
      fieldName: 'errorMessage',
      label: '错误信息',
      component: 'Input',
      componentProps: {
        placeholder: '请输入错误信息',
      },
    },
    {
      fieldName: 'clientIp',
      label: '客户端 IP',
      component: 'Input',
      componentProps: {
        placeholder: '请输入客户端 IP',
      },
    },
    {
      fieldName: 'callDate',
      label: '调用日期（用于按天配额）',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'studentId',
      label: 'esc_student.id（学生发起时）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入esc_student.id（学生发起时）',
      },
    },
    {
      fieldName: 'userId',
      label: 'system_users.id（管理员/老师发起时）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入system_users.id（管理员/老师发起时）',
      },
    },
    {
      fieldName: 'serviceType',
      label: '服务类型 llm / asr / tts',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [],
        placeholder: '请选择服务类型 llm / asr / tts',
      },
    },
    {
      fieldName: 'engine',
      label: '具体引擎名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入具体引擎名',
      },
    },
    {
      fieldName: 'endpoint',
      label: '调用端点',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入调用端点',
      },
    },
    {
      fieldName: 'requestSize',
      label: '请求字节数',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入请求字节数',
      },
    },
    {
      fieldName: 'responseSize',
      label: '响应字节数',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入响应字节数',
      },
    },
    {
      fieldName: 'durationMs',
      label: '耗时（毫秒）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入耗时（毫秒）',
      },
    },
    {
      fieldName: 'statusCode',
      label: '响应码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入响应码',
      },
    },
    {
      fieldName: 'success',
      label: '是否成功 1=成功 0=失败',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入是否成功 1=成功 0=失败',
      },
    },
    {
      fieldName: 'errorMessage',
      label: '错误信息',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入错误信息',
      },
    },
    {
      fieldName: 'clientIp',
      label: '客户端 IP',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入客户端 IP',
      },
    },
    {
      fieldName: 'callDate',
      label: '调用日期（用于按天配额）',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
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
export function useGridColumns(): VxeTableGridOptions<EnglishAiCallLogApi.AiCallLog>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'studentId',
      title: 'esc_student.id（学生发起时）',
      minWidth: 120,
    },
    {
      field: 'userId',
      title: 'system_users.id（管理员/老师发起时）',
      minWidth: 120,
    },
    {
      field: 'serviceType',
      title: '服务类型 llm / asr / tts',
      minWidth: 120,
    },
    {
      field: 'engine',
      title: '具体引擎名',
      minWidth: 120,
    },
    {
      field: 'endpoint',
      title: '调用端点',
      minWidth: 120,
    },
    {
      field: 'requestSize',
      title: '请求字节数',
      minWidth: 120,
    },
    {
      field: 'responseSize',
      title: '响应字节数',
      minWidth: 120,
    },
    {
      field: 'durationMs',
      title: '耗时（毫秒）',
      minWidth: 120,
    },
    {
      field: 'statusCode',
      title: '响应码',
      minWidth: 120,
    },
    {
      field: 'success',
      title: '是否成功 1=成功 0=失败',
      minWidth: 120,
    },
    {
      field: 'errorMessage',
      title: '错误信息',
      minWidth: 120,
    },
    {
      field: 'clientIp',
      title: '客户端 IP',
      minWidth: 120,
    },
    {
      field: 'callDate',
      title: '调用日期（用于按天配额）',
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
