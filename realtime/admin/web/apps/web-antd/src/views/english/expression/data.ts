import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExpressionApi } from '#/api/english/expression';

import { getExpressionThemes } from '#/api/english/expression';

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'themeId',
      label: '主题',
      component: 'ApiSelect',
      componentProps: {
        api: () => getExpressionThemes('ket'),
        labelField: 'nameCn',
        valueField: 'id',
        allowClear: true,
      },
    },
    {
      fieldName: 'prompt',
      label: '问句',
      component: 'Input',
      componentProps: { allowClear: true },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '草稿', value: 0 },
          { label: '发布', value: 1 },
          { label: '归档', value: 2 },
        ],
      },
    },
  ];
}

export function useFormSchema(): VbenFormSchema[] {
  return [
    { fieldName: 'id', component: 'Input', dependencies: { triggerFields: [''], show: () => false } },
    {
      fieldName: 'themeId', label: '主题', rules: 'required', component: 'ApiSelect',
      componentProps: { api: () => getExpressionThemes('ket'), labelField: 'nameCn', valueField: 'id' },
    },
    { fieldName: 'code', label: '编码', rules: 'required', component: 'Input', componentProps: { placeholder: '如 food-favourite-meal' } },
    { fieldName: 'promptEn', label: '英文问句', rules: 'required', component: 'Textarea', componentProps: { rows: 2 } },
    { fieldName: 'promptCn', label: '中文提示', component: 'Input' },
    { fieldName: 'functionCode', label: '交际功能', component: 'Input', componentProps: { placeholder: '如 give-preference' } },
    {
      fieldName: 'practiceMode', label: '练习模式', rules: 'required', component: 'RadioGroup', defaultValue: 'both',
      componentProps: { optionType: 'button', buttonStyle: 'solid', options: [
        { label: '口语和写作', value: 'both' }, { label: '仅口语', value: 'speaking' }, { label: '仅写作', value: 'writing' },
      ] },
    },
    {
      fieldName: 'difficulty', label: '难度', rules: 'required', component: 'RadioGroup', defaultValue: 1,
      componentProps: { optionType: 'button', buttonStyle: 'solid', options: [
        { label: '基础', value: 1 }, { label: '扩展', value: 2 }, { label: '挑战', value: 3 },
      ] },
    },
    {
      fieldName: 'answerJson', label: '答案与句型 JSON', rules: 'required', component: 'Textarea', formItemClass: 'col-span-2',
      componentProps: { rows: 16, placeholder: '{"answers":{"basic":{"en":"...","cn":"..."}},"patterns":[],"keywords":[]}' },
    },
    {
      fieldName: 'imageUrlsJson', label: '图片 URL JSON', component: 'Textarea', formItemClass: 'col-span-2',
      componentProps: { rows: 3, placeholder: '["https://..."]，可留空' },
    },
    { fieldName: 'sort', label: '排序', component: 'InputNumber', defaultValue: 0, componentProps: { min: 0 } },
    {
      fieldName: 'status', label: '状态', rules: 'required', component: 'RadioGroup', defaultValue: 1,
      componentProps: { optionType: 'button', buttonStyle: 'solid', options: [
        { label: '草稿', value: 0 }, { label: '发布', value: 1 }, { label: '归档', value: 2 },
      ] },
    },
  ];
}

export function useGridColumns(): VxeTableGridOptions<EnglishExpressionApi.Item>['columns'] {
  return [
    { field: 'id', title: 'ID', width: 70 },
    { field: 'code', title: '编码', minWidth: 170 },
    { field: 'promptEn', title: '英文问句', minWidth: 280 },
    { field: 'functionCode', title: '交际功能', minWidth: 140 },
    { field: 'practiceMode', title: '模式', width: 110 },
    { field: 'difficulty', title: '难度', width: 80 },
    { field: 'status', title: '状态', width: 80, slots: { default: 'status' } },
    { field: 'sort', title: '排序', width: 70 },
    { title: '操作', width: 180, fixed: 'right', slots: { default: 'actions' } },
  ];
}
