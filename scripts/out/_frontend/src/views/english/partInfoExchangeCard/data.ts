import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartInfoExchangeCardApi } from '#/api/english/partInfoExchangeCard';

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
      fieldName: 'phase',
      label: '阶段：A=学生提问 B=学生回答',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入阶段：A=学生提问 B=学生回答',
      },
    },
    {
      fieldName: 'cardTitle',
      label: '卡片标题，如 Football Match',
      component: 'Input',
      componentProps: {
        placeholder: '请输入卡片标题，如 Football Match',
      },
    },
    {
      fieldName: 'cardImageUrl',
      label: '卡片图片 URL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入卡片图片 URL',
      },
    },
    {
      fieldName: 'intro',
      label: '卡片说明',
      component: 'Input',
      componentProps: {
        placeholder: '请输入卡片说明',
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
      fieldName: 'phase',
      label: '阶段：A=学生提问 B=学生回答',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入阶段：A=学生提问 B=学生回答',
      },
    },
    {
      fieldName: 'cardTitle',
      label: '卡片标题，如 Football Match',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入卡片标题，如 Football Match',
      },
    },
    {
      fieldName: 'cardImageUrl',
      label: '卡片图片 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入卡片图片 URL',
      },
    },
    {
      fieldName: 'intro',
      label: '卡片说明',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入卡片说明',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartInfoExchangeCardApi.PartInfoExchangeCard>['columns'] {
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
      field: 'phase',
      title: '阶段：A=学生提问 B=学生回答',
      minWidth: 120,
    },
    {
      field: 'cardTitle',
      title: '卡片标题，如 Football Match',
      minWidth: 120,
    },
    {
      field: 'cardImageUrl',
      title: '卡片图片 URL',
      minWidth: 120,
    },
    {
      field: 'intro',
      title: '卡片说明',
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
