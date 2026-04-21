import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExamSeriesApi } from '#/api/english/examSeries';

import { listAllSimpleExamLevel } from '#/api/english/examLevel';
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
      fieldName: 'code',
      label: '系列编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '如 go_flyers / flyers_1 / aep_1',
      },
    },
    {
      fieldName: 'levelCode',
      label: '所属级别',
      rules: 'required',
      component: 'ApiSelect',
      componentProps: {
        api: listAllSimpleExamLevel,
        labelField: 'name',
        valueField: 'code',
        placeholder: '请选择所属级别',
        allowClear: true,
        showSearch: true,
      },
    },
    {
      fieldName: 'name',
      label: '显示名',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '如 Go Flyers / Flyers 1 (2014)',
      },
    },
    {
      fieldName: 'coverUrl',
      label: '封面图',
      component: 'ImageUpload',
      componentProps: {
        maxSize: 5,
        accept: ['jpg', 'jpeg', 'png', 'webp', 'gif'],
      },
    },
    {
      fieldName: 'publisher',
      label: '出版方',
      component: 'Input',
      componentProps: {
        placeholder: '如 Cambridge / Macmillan',
      },
    },
    {
      fieldName: 'description',
      label: '说明',
      component: 'Textarea',
    },
    {
      fieldName: 'sort',
      label: '排序',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入排序',
        min: 0,
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: [
          { label: '启用', value: 0 },
          { label: '停用', value: 1 },
        ],
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
      fieldName: 'code',
      label: '系列编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入系列编码',
      },
    },
    {
      fieldName: 'levelCode',
      label: '所属级别',
      component: 'ApiSelect',
      componentProps: {
        api: listAllSimpleExamLevel,
        labelField: 'name',
        valueField: 'code',
        allowClear: true,
        showSearch: true,
        placeholder: '请选择所属级别',
      },
    },
    {
      fieldName: 'name',
      label: '显示名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入显示名',
      },
    },
    {
      fieldName: 'publisher',
      label: '出版方',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入出版方',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '启用', value: 0 },
          { label: '停用', value: 1 },
        ],
        placeholder: '请选择状态',
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
export function useGridColumns(): VxeTableGridOptions<EnglishExamSeriesApi.ExamSeries>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 80,
    },
    {
      field: 'code',
      title: '系列编码',
      minWidth: 140,
    },
    {
      field: 'levelCode',
      title: '所属级别',
      minWidth: 100,
    },
    {
      field: 'name',
      title: '显示名',
      minWidth: 180,
    },
    {
      field: 'coverUrl',
      title: '封面',
      minWidth: 80,
      slots: { default: 'coverUrl' },
    },
    {
      field: 'publisher',
      title: '出版方',
      minWidth: 140,
    },
    {
      field: 'sort',
      title: '排序',
      minWidth: 80,
    },
    {
      field: 'status',
      title: '状态',
      minWidth: 80,
    },
    {
      field: 'createTime',
      title: '添加时间',
      minWidth: 160,
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
