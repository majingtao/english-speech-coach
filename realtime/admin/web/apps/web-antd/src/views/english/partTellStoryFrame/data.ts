import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPartTellStoryFrameApi } from '#/api/english/partTellStoryFrame';

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
      fieldName: 'frameNo',
      label: '帧序号 1~5',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入帧序号 1~5',
      },
    },
    {
      fieldName: 'imageUrl',
      label: '该帧图片 URL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入该帧图片 URL',
      },
    },
    {
      fieldName: 'sentenceText',
      label: '该帧参考句子',
      component: 'Input',
      componentProps: {
        placeholder: '请输入该帧参考句子',
      },
    },
    {
      fieldName: 'hint',
      label: '提示词 / 关键词',
      component: 'Input',
      componentProps: {
        placeholder: '请输入提示词 / 关键词',
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
      fieldName: 'frameNo',
      label: '帧序号 1~5',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入帧序号 1~5',
      },
    },
    {
      fieldName: 'imageUrl',
      label: '该帧图片 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入该帧图片 URL',
      },
    },
    {
      fieldName: 'sentenceText',
      label: '该帧参考句子',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入该帧参考句子',
      },
    },
    {
      fieldName: 'hint',
      label: '提示词 / 关键词',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入提示词 / 关键词',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPartTellStoryFrameApi.PartTellStoryFrame>['columns'] {
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
      field: 'frameNo',
      title: '帧序号 1~5',
      minWidth: 120,
    },
    {
      field: 'imageUrl',
      title: '该帧图片 URL',
      minWidth: 120,
    },
    {
      field: 'sentenceText',
      title: '该帧参考句子',
      minWidth: 120,
    },
    {
      field: 'hint',
      title: '提示词 / 关键词',
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
