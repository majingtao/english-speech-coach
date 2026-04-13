import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPracticeSessionApi } from '#/api/english/practiceSession';

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
      fieldName: 'studentId',
      label: 'esc_student.id',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入esc_student.id',
      },
    },
    {
      fieldName: 'examId',
      label: 'esc_exam.id（具体版本）',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入esc_exam.id（具体版本）',
      },
    },
    {
      fieldName: 'examCode',
      label: '冗余 exam_code 便于跨版本统计',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入冗余 exam_code 便于跨版本统计',
      },
    },
    {
      fieldName: 'mode',
      label: '模式：exam / free_talk',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入模式：exam / free_talk',
      },
    },
    {
      fieldName: 'startTime',
      label: '开始时间',
      rules: 'required',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'endTime',
      label: '结束时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'durationSec',
      label: '总时长（秒）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入总时长（秒）',
      },
    },
    {
      fieldName: 'status',
      label: '0=进行中 1=已完成 2=已放弃',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: [],
        buttonStyle: 'solid',
        optionType: 'button',
      },
    },
    {
      fieldName: 'finalOverallScore',
      label: '整卷综合分 0-100',
      component: 'Input',
      componentProps: {
        placeholder: '请输入整卷综合分 0-100',
      },
    },
    {
      fieldName: 'finalComment',
      label: '整卷综合评语',
      component: 'Input',
      componentProps: {
        placeholder: '请输入整卷综合评语',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'studentId',
      label: 'esc_student.id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入esc_student.id',
      },
    },
    {
      fieldName: 'examId',
      label: 'esc_exam.id（具体版本）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入esc_exam.id（具体版本）',
      },
    },
    {
      fieldName: 'examCode',
      label: '冗余 exam_code 便于跨版本统计',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入冗余 exam_code 便于跨版本统计',
      },
    },
    {
      fieldName: 'mode',
      label: '模式：exam / free_talk',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入模式：exam / free_talk',
      },
    },
    {
      fieldName: 'startTime',
      label: '开始时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'endTime',
      label: '结束时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'durationSec',
      label: '总时长（秒）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入总时长（秒）',
      },
    },
    {
      fieldName: 'status',
      label: '0=进行中 1=已完成 2=已放弃',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [],
        placeholder: '请选择0=进行中 1=已完成 2=已放弃',
      },
    },
    {
      fieldName: 'finalOverallScore',
      label: '整卷综合分 0-100',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入整卷综合分 0-100',
      },
    },
    {
      fieldName: 'finalComment',
      label: '整卷综合评语',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入整卷综合评语',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPracticeSessionApi.PracticeSession>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'studentId',
      title: 'esc_student.id',
      minWidth: 120,
    },
    {
      field: 'examId',
      title: 'esc_exam.id（具体版本）',
      minWidth: 120,
    },
    {
      field: 'examCode',
      title: '冗余 exam_code 便于跨版本统计',
      minWidth: 120,
    },
    {
      field: 'mode',
      title: '模式：exam / free_talk',
      minWidth: 120,
    },
    {
      field: 'startTime',
      title: '开始时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'endTime',
      title: '结束时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'durationSec',
      title: '总时长（秒）',
      minWidth: 120,
    },
    {
      field: 'status',
      title: '0=进行中 1=已完成 2=已放弃',
      minWidth: 120,
    },
    {
      field: 'finalOverallScore',
      title: '整卷综合分 0-100',
      minWidth: 120,
    },
    {
      field: 'finalComment',
      title: '整卷综合评语',
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
