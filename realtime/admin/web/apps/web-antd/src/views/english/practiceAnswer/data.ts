import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishPracticeAnswerApi } from '#/api/english/practiceAnswer';

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
      fieldName: 'sessionId',
      label: 'esc_practice_session.id',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入esc_practice_session.id',
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
      fieldName: 'itemRefTable',
      label: '引用题目所在子表名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入引用题目所在子表名',
      },
    },
    {
      fieldName: 'itemRefId',
      label: '引用题目主键',
      component: 'Input',
      componentProps: {
        placeholder: '请输入引用题目主键',
      },
    },
    {
      fieldName: 'seq',
      label: '该 part 内的顺序',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入该 part 内的顺序',
      },
    },
    {
      fieldName: 'questionSnapshot',
      label: '题目快照（防止题库后续变动）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入题目快照（防止题库后续变动）',
      },
    },
    {
      fieldName: 'audioUrl',
      label: '学生录音 URL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入学生录音 URL',
      },
    },
    {
      fieldName: 'asrText',
      label: 'ASR 转写文本',
      component: 'Input',
      componentProps: {
        placeholder: '请输入ASR 转写文本',
      },
    },
    {
      fieldName: 'asrEngine',
      label: 'ASR 引擎',
      component: 'Input',
      componentProps: {
        placeholder: '请输入ASR 引擎',
      },
    },
    {
      fieldName: 'asrDurationMs',
      label: 'ASR 处理耗时（毫秒）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入ASR 处理耗时（毫秒）',
      },
    },
    {
      fieldName: 'scoreGrammarVocab',
      label: '语法词汇 0-100',
      component: 'Input',
      componentProps: {
        placeholder: '请输入语法词汇 0-100',
      },
    },
    {
      fieldName: 'scorePronunciation',
      label: '发音 0-100',
      component: 'Input',
      componentProps: {
        placeholder: '请输入发音 0-100',
      },
    },
    {
      fieldName: 'scoreInteraction',
      label: '互动 0-100',
      component: 'Input',
      componentProps: {
        placeholder: '请输入互动 0-100',
      },
    },
    {
      fieldName: 'scoreDiscourse',
      label: '篇章组织 0-100（PET 及以上）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入篇章组织 0-100（PET 及以上）',
      },
    },
    {
      fieldName: 'scoreOverall',
      label: '综合分 0-100',
      component: 'Input',
      componentProps: {
        placeholder: '请输入综合分 0-100',
      },
    },
    {
      fieldName: 'feedbackText',
      label: 'LLM 中文反馈',
      component: 'Input',
      componentProps: {
        placeholder: '请输入LLM 中文反馈',
      },
    },
    {
      fieldName: 'correctedText',
      label: 'LLM 修正版本',
      component: 'Input',
      componentProps: {
        placeholder: '请输入LLM 修正版本',
      },
    },
    {
      fieldName: 'llmEngine',
      label: 'LLM 引擎 / 模型名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入LLM 引擎 / 模型名',
      },
    },
    {
      fieldName: 'llmRawResponse',
      label: '原始 LLM 响应（调试 / 审计）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入原始 LLM 响应（调试 / 审计）',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'sessionId',
      label: 'esc_practice_session.id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入esc_practice_session.id',
      },
    },
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
      fieldName: 'itemRefTable',
      label: '引用题目所在子表名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入引用题目所在子表名',
      },
    },
    {
      fieldName: 'itemRefId',
      label: '引用题目主键',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入引用题目主键',
      },
    },
    {
      fieldName: 'seq',
      label: '该 part 内的顺序',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入该 part 内的顺序',
      },
    },
    {
      fieldName: 'questionSnapshot',
      label: '题目快照（防止题库后续变动）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入题目快照（防止题库后续变动）',
      },
    },
    {
      fieldName: 'audioUrl',
      label: '学生录音 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入学生录音 URL',
      },
    },
    {
      fieldName: 'asrText',
      label: 'ASR 转写文本',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入ASR 转写文本',
      },
    },
    {
      fieldName: 'asrEngine',
      label: 'ASR 引擎',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入ASR 引擎',
      },
    },
    {
      fieldName: 'asrDurationMs',
      label: 'ASR 处理耗时（毫秒）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入ASR 处理耗时（毫秒）',
      },
    },
    {
      fieldName: 'scoreGrammarVocab',
      label: '语法词汇 0-100',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入语法词汇 0-100',
      },
    },
    {
      fieldName: 'scorePronunciation',
      label: '发音 0-100',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入发音 0-100',
      },
    },
    {
      fieldName: 'scoreInteraction',
      label: '互动 0-100',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入互动 0-100',
      },
    },
    {
      fieldName: 'scoreDiscourse',
      label: '篇章组织 0-100（PET 及以上）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入篇章组织 0-100（PET 及以上）',
      },
    },
    {
      fieldName: 'scoreOverall',
      label: '综合分 0-100',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入综合分 0-100',
      },
    },
    {
      fieldName: 'feedbackText',
      label: 'LLM 中文反馈',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入LLM 中文反馈',
      },
    },
    {
      fieldName: 'correctedText',
      label: 'LLM 修正版本',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入LLM 修正版本',
      },
    },
    {
      fieldName: 'llmEngine',
      label: 'LLM 引擎 / 模型名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入LLM 引擎 / 模型名',
      },
    },
    {
      fieldName: 'llmRawResponse',
      label: '原始 LLM 响应（调试 / 审计）',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入原始 LLM 响应（调试 / 审计）',
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
export function useGridColumns(): VxeTableGridOptions<EnglishPracticeAnswerApi.PracticeAnswer>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'sessionId',
      title: 'esc_practice_session.id',
      minWidth: 120,
    },
    {
      field: 'partId',
      title: 'esc_exam_part.id',
      minWidth: 120,
    },
    {
      field: 'itemRefTable',
      title: '引用题目所在子表名',
      minWidth: 120,
    },
    {
      field: 'itemRefId',
      title: '引用题目主键',
      minWidth: 120,
    },
    {
      field: 'seq',
      title: '该 part 内的顺序',
      minWidth: 120,
    },
    {
      field: 'questionSnapshot',
      title: '题目快照（防止题库后续变动）',
      minWidth: 120,
    },
    {
      field: 'audioUrl',
      title: '学生录音 URL',
      minWidth: 120,
    },
    {
      field: 'asrText',
      title: 'ASR 转写文本',
      minWidth: 120,
    },
    {
      field: 'asrEngine',
      title: 'ASR 引擎',
      minWidth: 120,
    },
    {
      field: 'asrDurationMs',
      title: 'ASR 处理耗时（毫秒）',
      minWidth: 120,
    },
    {
      field: 'scoreGrammarVocab',
      title: '语法词汇 0-100',
      minWidth: 120,
    },
    {
      field: 'scorePronunciation',
      title: '发音 0-100',
      minWidth: 120,
    },
    {
      field: 'scoreInteraction',
      title: '互动 0-100',
      minWidth: 120,
    },
    {
      field: 'scoreDiscourse',
      title: '篇章组织 0-100（PET 及以上）',
      minWidth: 120,
    },
    {
      field: 'scoreOverall',
      title: '综合分 0-100',
      minWidth: 120,
    },
    {
      field: 'feedbackText',
      title: 'LLM 中文反馈',
      minWidth: 120,
    },
    {
      field: 'correctedText',
      title: 'LLM 修正版本',
      minWidth: 120,
    },
    {
      field: 'llmEngine',
      title: 'LLM 引擎 / 模型名',
      minWidth: 120,
    },
    {
      field: 'llmRawResponse',
      title: '原始 LLM 响应（调试 / 审计）',
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
