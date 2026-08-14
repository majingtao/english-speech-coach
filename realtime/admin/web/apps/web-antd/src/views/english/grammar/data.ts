import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishGrammarApi } from '#/api/english/grammar';
import { getGrammarPoints } from '#/api/english/grammar';

export function useGridFormSchema(): VbenFormSchema[] { return [
  { fieldName: 'grammarPointId', label: '知识点', component: 'ApiSelect', componentProps: { api: getGrammarPoints, labelField: 'nameCn', valueField: 'id', allowClear: true } },
  { fieldName: 'difficulty', label: '难度', component: 'Select', componentProps: { allowClear: true, options: [{ label: 'L1 认识', value: 1 }, { label: 'L2 使用', value: 2 }] } },
  { fieldName: 'questionType', label: '题型', component: 'Select', componentProps: { allowClear: true, options: [{ label: '单选', value: 'single_choice' }, { label: '填空', value: 'text_input' }] } },
  { fieldName: 'status', label: '状态', component: 'Select', componentProps: { allowClear: true, options: [{ label: '草稿', value: 0 }, { label: '发布', value: 1 }, { label: '停用', value: 2 }] } },
]; }
export function useGridColumns(): VxeTableGridOptions<EnglishGrammarApi.Question>['columns'] { return [
  { field: 'id', title: 'ID', width: 70 }, { field: 'stem', title: '题干', minWidth: 300 },
  { field: 'questionType', title: '题型', width: 100 }, { field: 'difficulty', title: '难度', width: 80 },
  { field: 'source', title: '来源', width: 80 }, { field: 'status', title: '状态', width: 90, slots: { default: 'status' } },
  { title: '操作', width: 170, fixed: 'right', slots: { default: 'actions' } },
]; }
