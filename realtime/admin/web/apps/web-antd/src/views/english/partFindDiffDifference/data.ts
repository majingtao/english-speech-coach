import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartFindDiffDifferenceApi } from '#/api/english/partFindDiffDifference';

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
      fieldName: 'pairId',
      label: 'esc_part_find_diff_pair.id',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入esc_part_find_diff_pair.id',
      },
    },
    {
      fieldName: 'diffNo',
      label: '差异点序号',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入差异点序号',
      },
    },
    {
      fieldName: 'description',
      label: '差异描述（英文）',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入差异描述（英文）',
      },
    },
    {
      fieldName: 'keyword',
      label: '关键词，便于评分匹配',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关键词，便于评分匹配',
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
      fieldName: 'pairId',
      label: 'esc_part_find_diff_pair.id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入esc_part_find_diff_pair.id',
      },
    },
    {
      fieldName: 'diffNo',
      label: '差异点序号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入差异点序号',
      },
    },
    {
      fieldName: 'description',
      label: '差异描述（英文）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入差异描述（英文）',
      },
    },
    {
      fieldName: 'keyword',
      label: '关键词，便于评分匹配',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入关键词，便于评分匹配',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartFindDiffDifferenceApi.PartFindDiffDifference>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'pairId',
      title: 'esc_part_find_diff_pair.id',
      minWidth: 120,
    },
    {
      field: 'diffNo',
      title: '差异点序号',
      minWidth: 120,
    },
    {
      field: 'description',
      title: '差异描述（英文）',
      minWidth: 120,
    },
    {
      field: 'keyword',
      title: '关键词，便于评分匹配',
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
