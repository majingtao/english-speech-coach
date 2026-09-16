import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishSynonymApi } from '#/api/english/synonym';

export function useGridFormSchema(): VbenFormSchema[] {
  return [
    { fieldName: 'levelCode', label: '级别', component: 'Input', defaultValue: 'ket' },
    { fieldName: 'mode', label: '类型', component: 'Select', componentProps: { allowClear: true, options: [{ label: '同义词', value: 'synonym' }, { label: '反义词', value: 'antonym' }] } },
    { fieldName: 'source', label: '来源', component: 'Input' },
    { fieldName: 'status', label: '状态', component: 'Select', componentProps: { allowClear: true, options: [{ label: '草稿', value: 0 }, { label: '发布', value: 1 }, { label: '停用', value: 2 }] } },
  ];
}

export function useGridColumns(): VxeTableGridOptions<EnglishSynonymApi.Point>['columns'] {
  return [
    { field: 'id', title: 'ID', width: 70 },
    { field: 'mode', title: '类型', width: 90, slots: { default: 'mode' } },
    { field: 'leftText', title: '题干', minWidth: 220 },
    { field: 'leftCn', title: '题干中文', minWidth: 160 },
    { field: 'rightText', title: '答案', minWidth: 200 },
    { field: 'rightCn', title: '答案中文', minWidth: 140 },
    { field: 'source', title: '来源', width: 120 },
    { field: 'sectionName', title: '章节', minWidth: 170 },
    { field: 'status', title: '状态', width: 90, slots: { default: 'status' } },
    { title: '操作', width: 170, fixed: 'right', slots: { default: 'actions' } },
  ];
}
