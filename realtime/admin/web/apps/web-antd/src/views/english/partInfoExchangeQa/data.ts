import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartInfoExchangeQaApi } from '#/api/english/partInfoExchangeQa';

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
      fieldName: 'cardId',
      label: '卡片ID',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入卡片ID',
      },
    },
    {
      fieldName: 'qaNo',
      label: '问答序号',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入问答序号',
      },
    },
    {
      fieldName: 'promptLabel',
      label: '提示项',
      component: 'Input',
      componentProps: {
        placeholder: '请输入提示项（如 Place / Date / Cost）',
      },
    },
    {
      fieldName: 'questionText',
      label: '完整问句',
      component: 'Input',
      componentProps: {
        placeholder: '请输入完整问句',
      },
    },
    {
      fieldName: 'answerText',
      label: '参考答案',
      component: 'Input',
      componentProps: {
        placeholder: '请输入参考答案',
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
      fieldName: 'cardId',
      label: '卡片ID',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入卡片ID',
      },
    },
    {
      fieldName: 'questionText',
      label: '完整问句',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入完整问句',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartInfoExchangeQaApi.PartInfoExchangeQa>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 80,
    },
    {
      field: 'cardId',
      title: '卡片ID',
      minWidth: 80,
    },
    {
      field: 'qaNo',
      title: '序号',
      minWidth: 60,
    },
    {
      field: 'promptLabel',
      title: '提示项',
      minWidth: 120,
    },
    {
      field: 'questionText',
      title: '完整问句',
      minWidth: 200,
    },
    {
      field: 'answerText',
      title: '参考答案',
      minWidth: 150,
    },
    {
      field: 'sort',
      title: '排序',
      minWidth: 60,
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
