import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishReadingMaterialApi } from '#/api/english/readingMaterial';

export const materialTypeOptions = [
  { label: '单词', value: 'word' },
  { label: '短语', value: 'phrase' },
  { label: '句子', value: 'sentence' },
];

export const partOfSpeechOptions = [
  { label: '名词', value: 'noun' },
  { label: '动词', value: 'verb' },
  { label: '形容词', value: 'adjective' },
  { label: '副词', value: 'adverb' },
  { label: '短语', value: 'phrase' },
  { label: '句子', value: 'sentence' },
  { label: '未知', value: 'unknown' },
];

export const priorityOptions = [
  { label: '必会', value: 'mustKnow' },
  { label: '高频', value: 'highFrequency' },
  { label: '必默', value: 'mustSpell' },
];

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'text',
      label: '英文',
      component: 'Input',
      componentProps: { allowClear: true },
    },
    {
      fieldName: 'materialType',
      label: '类型',
      component: 'Select',
      componentProps: { allowClear: true, options: materialTypeOptions },
    },
    {
      fieldName: 'partOfSpeech',
      label: '词性',
      component: 'Select',
      componentProps: { allowClear: true, options: partOfSpeechOptions },
    },
    {
      fieldName: 'tag',
      label: '标签',
      component: 'Input',
      componentProps: { allowClear: true },
    },
    {
      fieldName: 'priority',
      label: '重点',
      component: 'Select',
      componentProps: { allowClear: true, options: priorityOptions },
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
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: { triggerFields: [''], show: () => false },
    },
    {
      fieldName: 'textEn',
      label: '英文内容',
      rules: 'required',
      component: 'Textarea',
      componentProps: { rows: 2, placeholder: '输入单词、短语或句子' },
    },
    { fieldName: 'textCn', label: '中文翻译', component: 'Input' },
    {
      fieldName: 'description',
      label: '描述',
      component: 'Textarea',
      componentProps: {
        rows: 2,
        placeholder: '可选：给孩子看的提示或老师备注',
      },
    },
    {
      fieldName: 'materialType',
      label: '类型',
      rules: 'required',
      component: 'RadioGroup',
      defaultValue: 'word',
      componentProps: {
        optionType: 'button',
        buttonStyle: 'solid',
        options: materialTypeOptions,
      },
    },
    {
      fieldName: 'partOfSpeech',
      label: '词性',
      component: 'Select',
      defaultValue: 'unknown',
      componentProps: { options: partOfSpeechOptions },
    },
    {
      fieldName: 'levelCode',
      label: '级别',
      component: 'Input',
      defaultValue: 'ket',
    },
    {
      fieldName: 'tagsJson',
      label: '标签 JSON',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      defaultValue: '[]',
      componentProps: { rows: 3, placeholder: '["daily","ket"]' },
    },
    {
      fieldName: 'wordFormsJson',
      label: '词形 JSON',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      defaultValue: '{}',
      componentProps: {
        rows: 5,
        placeholder: '{"base":"go","pastTense":"went"}',
      },
    },
    {
      fieldName: 'examplesJson',
      label: '例句 JSON',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      defaultValue: '[]',
      componentProps: {
        rows: 8,
        placeholder: '[{"en":"I go to school.","cn":"我去上学。"}]',
      },
    },
    {
      fieldName: 'mustKnow',
      label: '必会',
      component: 'RadioGroup',
      defaultValue: 0,
      componentProps: {
        optionType: 'button',
        buttonStyle: 'solid',
        options: [
          { label: '否', value: 0 },
          { label: '是', value: 1 },
        ],
      },
    },
    {
      fieldName: 'highFrequency',
      label: '高频',
      component: 'RadioGroup',
      defaultValue: 0,
      componentProps: {
        optionType: 'button',
        buttonStyle: 'solid',
        options: [
          { label: '否', value: 0 },
          { label: '是', value: 1 },
        ],
      },
    },
    {
      fieldName: 'mustSpell',
      label: '必默',
      component: 'RadioGroup',
      defaultValue: 0,
      componentProps: {
        optionType: 'button',
        buttonStyle: 'solid',
        options: [
          { label: '否', value: 0 },
          { label: '是', value: 1 },
        ],
      },
    },
    {
      fieldName: 'sort',
      label: '排序',
      component: 'InputNumber',
      defaultValue: 0,
      componentProps: { min: 0 },
    },
    {
      fieldName: 'status',
      label: '状态',
      rules: 'required',
      component: 'RadioGroup',
      defaultValue: 1,
      componentProps: {
        optionType: 'button',
        buttonStyle: 'solid',
        options: [
          { label: '草稿', value: 0 },
          { label: '发布', value: 1 },
          { label: '归档', value: 2 },
        ],
      },
    },
  ];
}

export function useGridColumns(): VxeTableGridOptions<EnglishReadingMaterialApi.Material>['columns'] {
  return [
    { field: 'id', title: 'ID', width: 70 },
    { field: 'textEn', title: '英文', minWidth: 220 },
    { field: 'textCn', title: '中文', minWidth: 160 },
    { field: 'description', title: '描述', minWidth: 180 },
    {
      field: 'materialType',
      title: '类型',
      width: 90,
      slots: { default: 'materialType' },
    },
    { field: 'partOfSpeech', title: '词性', width: 100 },
    {
      field: 'priority',
      title: '重点',
      width: 120,
      slots: { default: 'priority' },
    },
    {
      field: 'tagsJson',
      title: '标签',
      minWidth: 180,
      slots: { default: 'tags' },
    },
    { field: 'status', title: '状态', width: 80, slots: { default: 'status' } },
    { field: 'sort', title: '排序', width: 70 },
    {
      title: '操作',
      width: 160,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
