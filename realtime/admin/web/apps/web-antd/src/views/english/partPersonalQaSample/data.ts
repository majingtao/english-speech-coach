import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartPersonalQaSampleApi } from '#/api/english/partPersonalQaSample';

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
      fieldName: 'questionId',
      label: '问题ID',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入问题ID',
      },
    },
    {
      fieldName: 'sampleNo',
      label: '示例序号',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入示例序号',
      },
    },
    {
      fieldName: 'sampleText',
      label: '示例答案',
      component: 'Input',
      componentProps: {
        placeholder: '请输入示例答案',
      },
    },
    {
      fieldName: 'levelTag',
      label: '水平标签',
      component: 'Input',
      componentProps: {
        placeholder: '请输入水平标签（如 basic / good）',
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
      fieldName: 'questionId',
      label: '问题ID',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入问题ID',
      },
    },
    {
      fieldName: 'sampleText',
      label: '示例答案',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入示例答案',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartPersonalQaSampleApi.PartPersonalQaSample>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 80,
    },
    {
      field: 'questionId',
      title: '问题ID',
      minWidth: 80,
    },
    {
      field: 'sampleNo',
      title: '序号',
      minWidth: 60,
    },
    {
      field: 'sampleText',
      title: '示例答案',
      minWidth: 250,
    },
    {
      field: 'levelTag',
      title: '水平标签',
      minWidth: 100,
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
