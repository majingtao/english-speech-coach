import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishSchoolClassApi } from '#/api/english/schoolClass';

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
      fieldName: 'name',
      label: '班级名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入班级名称',
      },
    },
    {
      fieldName: 'code',
      label: '班级编码（可选）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入班级编码（可选）',
      },
    },
    {
      fieldName: 'description',
      label: '描述',
      component: 'RichTextarea',
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
      fieldName: 'name',
      label: '班级名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入班级名称',
      },
    },
    {
      fieldName: 'code',
      label: '班级编码（可选）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入班级编码（可选）',
      },
    },
    {
      fieldName: 'description',
      label: '描述',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入描述',
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
export function useGridColumns(): VxeTableGridOptions<EnglishSchoolClassApi.SchoolClass>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'name',
      title: '班级名称',
      minWidth: 120,
    },
    {
      field: 'code',
      title: '班级编码（可选）',
      minWidth: 120,
    },
    {
      field: 'description',
      title: '描述',
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
