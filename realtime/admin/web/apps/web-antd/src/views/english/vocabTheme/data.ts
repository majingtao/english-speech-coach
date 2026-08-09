import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishVocabThemeApi } from '#/api/english/vocabTheme';

import { listAllSimpleExamLevel } from '#/api/english/examLevel';

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
      label: '编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '如 sports / food / school',
      },
    },
    {
      fieldName: 'nameCn',
      label: '中文名',
      rules: 'required',
      component: 'Input',
    },
    {
      fieldName: 'nameEn',
      label: '英文名',
      rules: 'required',
      component: 'Input',
    },
    {
      fieldName: 'levelCode',
      label: '所属级别',
      component: 'ApiSelect',
      componentProps: {
        api: listAllSimpleExamLevel,
        labelField: 'name',
        valueField: 'code',
        placeholder: '留空=通用',
        allowClear: true,
        showSearch: true,
      },
    },
    {
      fieldName: 'sort',
      label: '排序',
      component: 'InputNumber',
      componentProps: { min: 0 },
      defaultValue: 0,
    },
    {
      fieldName: 'status',
      label: '状态',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: [
          { label: '启用', value: 1 },
          { label: '禁用', value: 0 },
        ],
        buttonStyle: 'solid',
        optionType: 'button',
      },
      defaultValue: 1,
    },
  ];
}

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'code',
      label: '编码',
      component: 'Input',
      componentProps: { allowClear: true },
    },
    {
      fieldName: 'nameCn',
      label: '中文名',
      component: 'Input',
      componentProps: { allowClear: true },
    },
    {
      fieldName: 'levelCode',
      label: '级别',
      component: 'ApiSelect',
      componentProps: {
        api: listAllSimpleExamLevel,
        labelField: 'name',
        valueField: 'code',
        allowClear: true,
        showSearch: true,
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '启用', value: 1 },
          { label: '禁用', value: 0 },
        ],
      },
    },
  ];
}

export function useGridColumns(): VxeTableGridOptions<EnglishVocabThemeApi.VocabTheme>['columns'] {
  return [
    { field: 'id', title: 'ID', minWidth: 60 },
    { field: 'code', title: '编码', minWidth: 120 },
    { field: 'nameCn', title: '中文名', minWidth: 100 },
    { field: 'nameEn', title: '英文名', minWidth: 120 },
    { field: 'levelCode', title: '级别', minWidth: 80 },
    { field: 'sort', title: '排序', minWidth: 60 },
    {
      field: 'status',
      title: '状态',
      minWidth: 80,
      slots: { default: 'status' },
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    { title: '操作', width: 180, fixed: 'right', slots: { default: 'actions' } },
  ];
}
