import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { DictationWordApi } from '#/api/english/dictation/word';

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
      fieldName: 'en',
      label: '英文单词',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入英文单词',
      },
    },
    {
      fieldName: 'cn',
      label: '中文释义',
      component: 'Input',
      componentProps: {
        placeholder: '请输入中文释义',
      },
    },
    {
      fieldName: 'pos',
      label: '词性',
      component: 'Input',
      componentProps: {
        placeholder: '如 n./v./adj.',
      },
    },
    {
      fieldName: 'forms',
      label: '词形变化',
      component: 'Input',
      componentProps: {
        placeholder: '如 decide → decided → deciding',
      },
    },
    {
      fieldName: 'example',
      label: '例句',
      component: 'Textarea',
      componentProps: {
        placeholder: '英文例句\\n中文翻译（成对换行）',
        rows: 4,
      },
    },
    {
      fieldName: 'difficulty',
      label: '难度',
      component: 'InputNumber',
      componentProps: {
        min: 1,
        max: 5,
        placeholder: '1-5',
      },
    },
    {
      fieldName: 'llmStatus',
      label: 'LLM状态',
      component: 'Select',
      componentProps: {
        options: [
          { label: '待处理', value: 0 },
          { label: '已完成', value: 1 },
          { label: '失败', value: 2 },
        ],
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'en',
      label: '英文',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入英文单词',
      },
    },
    {
      fieldName: 'cn',
      label: '中文',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入中文释义',
      },
    },
    {
      fieldName: 'llmStatus',
      label: 'LLM状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '待处理', value: 0 },
          { label: '已完成', value: 1 },
          { label: '失败', value: 2 },
        ],
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<DictationWordApi.Word>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: 'ID',
      minWidth: 60,
    },
    {
      field: 'en',
      title: '英文',
      minWidth: 120,
    },
    {
      field: 'cn',
      title: '中文',
      minWidth: 120,
    },
    {
      field: 'pos',
      title: '词性',
      minWidth: 70,
    },
    {
      field: 'forms',
      title: '词形',
      minWidth: 150,
    },
    {
      field: 'difficulty',
      title: '难度',
      minWidth: 60,
    },
    {
      field: 'llmStatus',
      title: 'LLM',
      minWidth: 70,
      slots: { default: 'llmStatus' },
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
