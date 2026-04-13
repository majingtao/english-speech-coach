import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExamPartTypeApi } from '#/api/english/examPartType';

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
      fieldName: 'code',
      label: '题型编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入题型编码',
      },
    },
    {
      fieldName: 'name',
      label: '显示名',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入显示名',
      },
    },
    {
      fieldName: 'description',
      label: '说明',
      component: 'RichTextarea',
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
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'code',
      label: '题型编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入题型编码',
      },
    },
    {
      fieldName: 'name',
      label: '显示名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入显示名',
      },
    },
    {
      fieldName: 'description',
      label: '说明',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入说明',
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
export function useGridColumns(): VxeTableGridOptions<EnglishExamPartTypeApi.ExamPartType>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'code',
      title: '题型编码',
      minWidth: 120,
    },
    {
      field: 'name',
      title: '显示名',
      minWidth: 120,
    },
    {
      field: 'description',
      title: '说明',
      minWidth: 120,
    },
    {
      field: 'sort',
      title: '排序',
      minWidth: 120,
    },
    {
      field: 'status',
      title: '0=启用 1=停用',
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
