import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishAiModelApi } from '#/api/english/aiModel';

import { getRangePickerDefaultProps } from '#/utils';

const TYPE_OPTIONS = [
  { label: 'LLM', value: 'llm' },
  { label: 'ASR', value: 'asr' },
  { label: 'TTS', value: 'tts' },
];

const STATUS_OPTIONS = [
  { label: '启用', value: 0 },
  { label: '停用', value: 1 },
];

const DEFAULT_OPTIONS = [
  { label: '否', value: 0 },
  { label: '是', value: 1 },
];

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
      fieldName: 'type',
      label: '类型',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: TYPE_OPTIONS,
        placeholder: '请选择类型',
      },
    },
    {
      fieldName: 'provider',
      label: '提供商',
      component: 'Input',
      componentProps: {
        placeholder: 'LLM: openai/claude/moonshot; ASR: offline/online',
      },
    },
    {
      fieldName: 'modelKey',
      label: '模型标识',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '如 gpt-4o-mini / sensevoice-small / edge',
      },
    },
    {
      fieldName: 'label',
      label: '显示名',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '前端下拉框显示的名称',
      },
    },
    {
      fieldName: 'configJson',
      label: '扩展配置',
      component: 'Input',
      componentProps: {
        placeholder: '如 {"use_proxy":true}',
      },
    },
    {
      fieldName: 'isDefault',
      label: '默认选中',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: DEFAULT_OPTIONS,
        buttonStyle: 'solid',
        optionType: 'button',
      },
    },
    {
      fieldName: 'sort',
      label: '排序',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        placeholder: '越小越前',
        min: 0,
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: STATUS_OPTIONS,
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
      fieldName: 'type',
      label: '类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: TYPE_OPTIONS,
        placeholder: '全部',
      },
    },
    {
      fieldName: 'provider',
      label: '提供商',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入提供商',
      },
    },
    {
      fieldName: 'label',
      label: '显示名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入显示名',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STATUS_OPTIONS,
        placeholder: '全部',
      },
    },
    {
      fieldName: 'createTime',
      label: '创建时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<EnglishAiModelApi.AiModel>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    { field: 'id', title: 'ID', width: 70 },
    {
      field: 'type',
      title: '类型',
      width: 80,
      formatter({ cellValue }) {
        const map: Record<string, string> = { llm: 'LLM', asr: 'ASR', tts: 'TTS' };
        return map[cellValue] || cellValue;
      },
    },
    { field: 'provider', title: '提供商', width: 110 },
    { field: 'modelKey', title: '模型标识', minWidth: 180 },
    { field: 'label', title: '显示名', minWidth: 160 },
    { field: 'configJson', title: '扩展配置', minWidth: 140 },
    {
      field: 'isDefault',
      title: '默认',
      width: 70,
      formatter({ cellValue }) {
        return cellValue === 1 ? '是' : '否';
      },
    },
    { field: 'sort', title: '排序', width: 70 },
    {
      field: 'status',
      title: '状态',
      width: 80,
      formatter({ cellValue }) {
        return cellValue === 0 ? '启用' : '停用';
      },
    },
    {
      field: 'createTime',
      title: '创建时间',
      width: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 160,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
