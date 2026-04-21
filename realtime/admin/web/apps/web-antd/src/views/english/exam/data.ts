import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExamApi } from '#/api/english/exam';

import { listAllSimpleExamLevel } from '#/api/english/examLevel';
import { listAllSimpleExamSeries } from '#/api/english/examSeries';
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
      fieldName: 'examCode',
      label: '试卷编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入试卷编码，如 gf_1',
      },
    },
    {
      fieldName: 'version',
      label: '版本号',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入版本号',
        min: 1,
      },
    },
    {
      fieldName: 'isActive',
      label: '是否生效',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: [
          { label: '是', value: 1 },
          { label: '否', value: 0 },
        ],
        buttonStyle: 'solid',
        optionType: 'button',
      },
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
      fieldName: 'seriesCode',
      label: '系列',
      component: 'ApiSelect',
      componentProps: (values) => ({
        api: async () => {
          const list = await listAllSimpleExamSeries();
          return values.levelCode
            ? list.filter((s) => s.levelCode === values.levelCode)
            : list;
        },
        labelField: 'name',
        valueField: 'code',
        placeholder: values.levelCode ? '请选择系列' : '请先选择级别',
        allowClear: true,
        showSearch: true,
        disabled: !values.levelCode,
      }),
      dependencies: {
        triggerFields: ['levelCode'],
      },
    },
    {
      fieldName: 'label',
      label: '试卷名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入试卷显示名',
      },
    },
    {
      fieldName: 'source',
      label: '来源',
      component: 'Input',
      componentProps: {
        placeholder: '请输入来源',
      },
    },
    {
      fieldName: 'year',
      label: '出题年份',
      component: 'Input',
      componentProps: {
        placeholder: '请输入出题年份',
      },
    },
    {
      fieldName: 'description',
      label: '描述',
      component: 'Input',
      componentProps: {
        placeholder: '请输入描述',
      },
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
          { label: '下架', value: 2 },
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
      fieldName: 'examCode',
      label: '试卷编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入试卷编码',
      },
    },
    {
      fieldName: 'label',
      label: '试卷名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入试卷名称',
      },
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
        placeholder: '请选择级别',
      },
    },
    {
      fieldName: 'seriesCode',
      label: '系列',
      component: 'ApiSelect',
      componentProps: (values) => ({
        api: async () => {
          const list = await listAllSimpleExamSeries();
          return values.levelCode
            ? list.filter((s) => s.levelCode === values.levelCode)
            : list;
        },
        labelField: 'name',
        valueField: 'code',
        allowClear: true,
        showSearch: true,
        placeholder: '请选择系列',
      }),
      dependencies: {
        triggerFields: ['levelCode'],
      },
    },
    {
      fieldName: 'isActive',
      label: '是否生效',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '是', value: 1 },
          { label: '否', value: 0 },
        ],
        placeholder: '请选择',
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
export function useGridColumns(): VxeTableGridOptions<EnglishExamApi.Exam>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 60,
    },
    {
      field: 'examCode',
      title: '试卷编码',
      minWidth: 100,
    },
    {
      field: 'label',
      title: '试卷名称',
      minWidth: 200,
    },
    {
      field: 'levelCode',
      title: '级别',
      minWidth: 80,
    },
    {
      field: 'seriesCode',
      title: '系列',
      minWidth: 120,
    },
    {
      field: 'version',
      title: '版本',
      minWidth: 60,
    },
    {
      field: 'isActive',
      title: '生效',
      minWidth: 60,
      slots: { default: 'isActive' },
    },
    {
      field: 'status',
      title: '状态',
      minWidth: 80,
      slots: { default: 'status' },
    },
    {
      field: 'contentJson',
      title: 'JSON',
      minWidth: 80,
      slots: { default: 'hasJson' },
    },
    {
      field: 'createTime',
      title: '添加时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 280,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
