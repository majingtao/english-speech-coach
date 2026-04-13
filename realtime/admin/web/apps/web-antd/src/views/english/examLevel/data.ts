import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExamLevelApi } from '#/api/english/examLevel';

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
      label: '级别编码：flyers / ket / pet',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入级别编码：flyers / ket / pet',
      },
    },
    {
      fieldName: 'name',
      label: '显示名：Cambridge Flyers / KET / PET',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入显示名：Cambridge Flyers / KET / PET',
      },
    },
    {
      fieldName: 'cefr',
      label: 'CEFR 等级：A2 / B1 ...',
      component: 'Input',
      componentProps: {
        placeholder: '请输入CEFR 等级：A2 / B1 ...',
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
      label: '级别编码：flyers / ket / pet',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入级别编码：flyers / ket / pet',
      },
    },
    {
      fieldName: 'name',
      label: '显示名：Cambridge Flyers / KET / PET',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入显示名：Cambridge Flyers / KET / PET',
      },
    },
    {
      fieldName: 'cefr',
      label: 'CEFR 等级：A2 / B1 ...',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入CEFR 等级：A2 / B1 ...',
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
export function useGridColumns(): VxeTableGridOptions<EnglishExamLevelApi.ExamLevel>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'code',
      title: '级别编码：flyers / ket / pet',
      minWidth: 120,
    },
    {
      field: 'name',
      title: '显示名：Cambridge Flyers / KET / PET',
      minWidth: 120,
    },
    {
      field: 'cefr',
      title: 'CEFR 等级：A2 / B1 ...',
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
