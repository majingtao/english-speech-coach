import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { PersonalPracticeApi } from '#/api/english/personalPractice';

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'practiceType',
      label: '类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '专属口语', value: 'speaking' },
          { label: '专属写作', value: 'writing' },
        ],
      },
    },
    {
      fieldName: 'title',
      label: '标题',
      component: 'Input',
      componentProps: { allowClear: true },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '草稿', value: 0 },
          { label: '发布', value: 1 },
          { label: '归档', value: 2 },
        ],
      },
    },
  ];
}

export function useGridColumns(): VxeTableGridOptions<PersonalPracticeApi.Practice>['columns'] {
  return [
    { field: 'id', title: 'ID', width: 70 },
    {
      field: 'practiceType',
      title: '类型',
      width: 110,
      slots: { default: 'practiceType' },
    },
    { field: 'title', title: '标题', minWidth: 170 },
    { field: 'promptEn', title: '英文题目', minWidth: 300 },
    { field: 'minSentences', title: '最低句数', width: 95 },
    { field: 'minWords', title: '最低词数', width: 95 },
    { field: 'status', title: '状态', width: 80, slots: { default: 'status' } },
    { field: 'sort', title: '排序', width: 70 },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
