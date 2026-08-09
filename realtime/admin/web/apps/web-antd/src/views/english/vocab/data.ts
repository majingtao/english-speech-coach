import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishVocabApi } from '#/api/english/vocab';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { listAllSimpleExamLevel } from '#/api/english/examLevel';
import { listVocabThemeByLevel } from '#/api/english/vocabTheme';

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
      fieldName: 'word',
      label: '单词',
      rules: 'required',
      component: 'Input',
      componentProps: { placeholder: '如 apple' },
    },
    {
      fieldName: 'levelCode',
      label: '级别',
      rules: 'required',
      component: 'ApiSelect',
      componentProps: {
        api: listAllSimpleExamLevel,
        labelField: 'name',
        valueField: 'code',
        placeholder: '请选择级别',
        allowClear: true,
        showSearch: true,
      },
    },
    {
      fieldName: 'pos',
      label: '词性',
      component: 'Input',
      componentProps: { placeholder: '如 noun,verb' },
    },
    {
      fieldName: 'difficulty',
      label: '难度',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENGLISH_VOCAB_DIFFICULTY, 'number'),
        buttonStyle: 'solid',
        optionType: 'button',
      },
      defaultValue: 1,
    },
    {
      fieldName: 'masteryLevel',
      label: '能力要求',
      rules: 'required',
      help: '三会=只需认读；四会=必须会拼写（进听写题源）',
      component: 'RadioGroup',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENGLISH_VOCAB_MASTERY, 'number'),
        buttonStyle: 'solid',
        optionType: 'button',
      },
      defaultValue: 1,
    },
    {
      fieldName: 'formsJson',
      label: '词形变化',
      component: 'Textarea',
      componentProps: {
        rows: 6,
        placeholder:
          '可选；JSON 格式，例如 {"base":"decide","past":"decided","past_participle":"decided","ing":"deciding","third_person":"decides"}',
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'contentJson',
      label: '内容（释义/IPA/例句）',
      component: 'Textarea',
      componentProps: {
        rows: 14,
        placeholder:
          '可选；JSON 格式：{definition_cn, definition_en, ipa, forms, examples:[{en,cn},{en,cn}]}。\n通常由 LLM 生成或批量导入填入；可手动修订（保持有效 JSON）。',
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'themeIds',
      label: '主题',
      component: 'ApiSelect',
      componentProps: (values) => ({
        api: () => listVocabThemeByLevel(values.levelCode),
        labelField: 'nameCn',
        valueField: 'id',
        mode: 'multiple',
        allowClear: true,
        showSearch: true,
        placeholder: values.levelCode ? '请选择主题' : '请先选择级别',
        disabled: !values.levelCode,
      }),
      dependencies: { triggerFields: ['levelCode'] },
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
          { label: '草稿', value: 0 },
          { label: '发布', value: 1 },
          { label: '归档', value: 2 },
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
      fieldName: 'word',
      label: '单词',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '模糊匹配' },
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
      fieldName: 'themeId',
      label: '主题',
      component: 'ApiSelect',
      componentProps: (values) => ({
        api: () => listVocabThemeByLevel(values.levelCode),
        labelField: 'nameCn',
        valueField: 'id',
        allowClear: true,
        showSearch: true,
        disabled: !values.levelCode,
        placeholder: values.levelCode ? '请选择主题' : '请先选择级别',
      }),
      dependencies: { triggerFields: ['levelCode'] },
    },
    {
      fieldName: 'difficulty',
      label: '难度',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.ENGLISH_VOCAB_DIFFICULTY, 'number'),
      },
    },
    {
      fieldName: 'masteryLevel',
      label: '能力要求',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.ENGLISH_VOCAB_MASTERY, 'number'),
      },
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

export function useGridColumns(): VxeTableGridOptions<EnglishVocabApi.Vocab>['columns'] {
  return [
    { field: 'id', title: 'ID', minWidth: 60 },
    { field: 'word', title: '单词', minWidth: 120 },
    { field: 'levelCode', title: '级别', minWidth: 80 },
    { field: 'pos', title: '词性', minWidth: 100 },
    {
      field: 'difficulty',
      title: '难度',
      minWidth: 80,
      slots: { default: 'difficulty' },
    },
    {
      field: 'masteryLevel',
      title: '能力要求',
      minWidth: 100,
      slots: { default: 'masteryLevel' },
    },
    {
      field: 'status',
      title: '状态',
      minWidth: 80,
      slots: { default: 'status' },
    },
    {
      field: 'contentJson',
      title: '内容',
      minWidth: 80,
      slots: { default: 'hasContent' },
    },
    { field: 'sort', title: '排序', minWidth: 60 },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    { title: '操作', width: 280, fixed: 'right', slots: { default: 'actions' } },
  ];
}
