import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartLongTurnPhotoApi } from '#/api/english/partLongTurnPhoto';

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
      fieldName: 'candidateRole',
      label: '面向的考生 A / B',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入面向的考生 A / B',
      },
    },
    {
      fieldName: 'topic',
      label: '主题',
      component: 'Input',
      componentProps: {
        placeholder: '请输入主题',
      },
    },
    {
      fieldName: 'imageUrl',
      label: '图片 URL',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入图片 URL',
      },
    },
    {
      fieldName: 'instruction',
      label: '考官指令',
      component: 'Input',
      componentProps: {
        placeholder: '请输入考官指令',
      },
    },
    {
      fieldName: 'sort',
      label: '排序',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排序',
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
      fieldName: 'candidateRole',
      label: '面向的考生 A / B',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入面向的考生 A / B',
      },
    },
    {
      fieldName: 'topic',
      label: '主题',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入主题',
      },
    },
    {
      fieldName: 'imageUrl',
      label: '图片 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入图片 URL',
      },
    },
    {
      fieldName: 'instruction',
      label: '考官指令',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入考官指令',
      },
    },
    {
      fieldName: 'sort',
      label: '排序',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排序',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartLongTurnPhotoApi.PartLongTurnPhoto>['columns'] {
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
      field: 'candidateRole',
      title: '面向的考生 A / B',
      minWidth: 120,
    },
    {
      field: 'topic',
      title: '主题',
      minWidth: 120,
    },
    {
      field: 'imageUrl',
      title: '图片 URL',
      minWidth: 120,
    },
    {
      field: 'instruction',
      title: '考官指令',
      minWidth: 120,
    },
    {
      field: 'sort',
      title: '排序',
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
