import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartCollabTaskApi } from '#/api/english/partCollabTask';

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
      fieldName: 'partId',
      label: 'esc_exam_part.id',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入esc_exam_part.id',
      },
    },
    {
      fieldName: 'scenario',
      label: '情境描述',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入情境描述',
      },
    },
    {
      fieldName: 'imageUrl',
      label: '辅助图片 URL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入辅助图片 URL',
      },
    },
    {
      fieldName: 'instruction',
      label: '任务指令',
      component: 'Input',
      componentProps: {
        placeholder: '请输入任务指令',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'partId',
      label: 'esc_exam_part.id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入esc_exam_part.id',
      },
    },
    {
      fieldName: 'scenario',
      label: '情境描述',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入情境描述',
      },
    },
    {
      fieldName: 'imageUrl',
      label: '辅助图片 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入辅助图片 URL',
      },
    },
    {
      fieldName: 'instruction',
      label: '任务指令',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入任务指令',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartCollabTaskApi.PartCollabTask>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'partId',
      title: 'esc_exam_part.id',
      minWidth: 120,
    },
    {
      field: 'scenario',
      title: '情境描述',
      minWidth: 120,
    },
    {
      field: 'imageUrl',
      title: '辅助图片 URL',
      minWidth: 120,
    },
    {
      field: 'instruction',
      title: '任务指令',
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
