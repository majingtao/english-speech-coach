import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExamPartApi } from '#/api/english/examPart';

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
      fieldName: 'examId',
      label: '所属试卷 esc_exam.id',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入所属试卷 esc_exam.id',
      },
    },
    {
      fieldName: 'partNo',
      label: '题段序号 1/2/3/4',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入题段序号 1/2/3/4',
      },
    },
    {
      fieldName: 'partType',
      label: '题型编码，引用 esc_exam_part_type.code',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: [],
        placeholder: '请选择题型编码，引用 esc_exam_part_type.code',
      },
    },
    {
      fieldName: 'title',
      label: 'Part 显示标题',
      component: 'Input',
      componentProps: {
        placeholder: '请输入Part 显示标题',
      },
    },
    {
      fieldName: 'intro',
      label: 'Part 引导语 / 考官口述',
      component: 'Input',
      componentProps: {
        placeholder: '请输入Part 引导语 / 考官口述',
      },
    },
    {
      fieldName: 'timeLimitSec',
      label: '时间限制（秒），可为空',
      component: 'Input',
      componentProps: {
        placeholder: '请输入时间限制（秒），可为空',
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
      fieldName: 'examId',
      label: '所属试卷 esc_exam.id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入所属试卷 esc_exam.id',
      },
    },
    {
      fieldName: 'partNo',
      label: '题段序号 1/2/3/4',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入题段序号 1/2/3/4',
      },
    },
    {
      fieldName: 'partType',
      label: '题型编码，引用 esc_exam_part_type.code',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [],
        placeholder: '请选择题型编码，引用 esc_exam_part_type.code',
      },
    },
    {
      fieldName: 'title',
      label: 'Part 显示标题',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入Part 显示标题',
      },
    },
    {
      fieldName: 'intro',
      label: 'Part 引导语 / 考官口述',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入Part 引导语 / 考官口述',
      },
    },
    {
      fieldName: 'timeLimitSec',
      label: '时间限制（秒），可为空',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入时间限制（秒），可为空',
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
export function useGridColumns(): VxeTableGridOptions<EnglishExamPartApi.ExamPart>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'examId',
      title: '所属试卷 esc_exam.id',
      minWidth: 120,
    },
    {
      field: 'partNo',
      title: '题段序号 1/2/3/4',
      minWidth: 120,
    },
    {
      field: 'partType',
      title: '题型编码，引用 esc_exam_part_type.code',
      minWidth: 120,
    },
    {
      field: 'title',
      title: 'Part 显示标题',
      minWidth: 120,
    },
    {
      field: 'intro',
      title: 'Part 引导语 / 考官口述',
      minWidth: 120,
    },
    {
      field: 'timeLimitSec',
      title: '时间限制（秒），可为空',
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
