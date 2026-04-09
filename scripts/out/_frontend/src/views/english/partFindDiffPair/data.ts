import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartFindDiffPairApi } from '#/api/english/partFindDiffPair';

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
      fieldName: 'pairNo',
      label: '图对序号',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入图对序号',
      },
    },
    {
      fieldName: 'imageAUrl',
      label: '左图 URL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入左图 URL',
      },
    },
    {
      fieldName: 'imageBUrl',
      label: '右图 URL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入右图 URL',
      },
    },
    {
      fieldName: 'topic',
      label: '主题，如 Two pictures of a kitchen',
      component: 'Input',
      componentProps: {
        placeholder: '请输入主题，如 Two pictures of a kitchen',
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
      fieldName: 'pairNo',
      label: '图对序号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入图对序号',
      },
    },
    {
      fieldName: 'imageAUrl',
      label: '左图 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入左图 URL',
      },
    },
    {
      fieldName: 'imageBUrl',
      label: '右图 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入右图 URL',
      },
    },
    {
      fieldName: 'topic',
      label: '主题，如 Two pictures of a kitchen',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入主题，如 Two pictures of a kitchen',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartFindDiffPairApi.PartFindDiffPair>['columns'] {
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
      field: 'pairNo',
      title: '图对序号',
      minWidth: 120,
    },
    {
      field: 'imageAUrl',
      title: '左图 URL',
      minWidth: 120,
    },
    {
      field: 'imageBUrl',
      title: '右图 URL',
      minWidth: 120,
    },
    {
      field: 'topic',
      title: '主题，如 Two pictures of a kitchen',
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
