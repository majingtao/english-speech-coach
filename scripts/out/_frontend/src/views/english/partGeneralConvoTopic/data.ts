import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartGeneralConvoTopicApi } from '#/api/english/partGeneralConvoTopic';

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
      fieldName: 'partId',
      label: 'esc_exam_part.id',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入esc_exam_part.id',
      },
    },
    {
      fieldName: 'topic',
      label: '对话主题',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入对话主题',
      },
    },
    {
      fieldName: 'intro',
      label: '主题引导',
      component: 'Input',
      componentProps: {
        placeholder: '请输入主题引导',
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
      fieldName: 'partId',
      label: 'esc_exam_part.id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入esc_exam_part.id',
      },
    },
    {
      fieldName: 'topic',
      label: '对话主题',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入对话主题',
      },
    },
    {
      fieldName: 'intro',
      label: '主题引导',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入主题引导',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartGeneralConvoTopicApi.PartGeneralConvoTopic>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'partId',
      title: 'esc_exam_part.id',
      minWidth: 120,
    },
    {
      field: 'topic',
      title: '对话主题',
      minWidth: 120,
    },
    {
      field: 'intro',
      title: '主题引导',
      minWidth: 120,
    },
    {
      field: 'sort',
      title: '排序',
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
