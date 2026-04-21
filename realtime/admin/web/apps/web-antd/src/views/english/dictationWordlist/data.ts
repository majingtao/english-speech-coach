import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { DictationWordlistApi } from '#/api/english/dictation/wordlist';

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
      fieldName: 'name',
      label: '词书名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入词书名称',
      },
    },
    {
      fieldName: 'categoryType',
      label: '分类类型',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: [
          { label: '教材', value: 'SCHOOL_GRADE' },
          { label: '考试', value: 'EXAM' },
        ],
        placeholder: '请选择分类类型',
      },
    },
    {
      fieldName: 'schoolLevel',
      label: '学段',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '小学', value: 'primary' },
          { label: '初中', value: 'middle' },
          { label: '高中', value: 'high' },
        ],
        placeholder: '请选择学段',
      },
    },
    {
      fieldName: 'grade',
      label: '年级',
      component: 'InputNumber',
      componentProps: {
        min: 1,
        max: 12,
        placeholder: '1-12',
      },
    },
    {
      fieldName: 'semester',
      label: '学期',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '上学期', value: 1 },
          { label: '下学期', value: 2 },
        ],
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'edition',
      label: '教材版本',
      component: 'Input',
      componentProps: {
        placeholder: '如 人教版/PEP版/外研版',
      },
    },
    {
      fieldName: 'unitLabel',
      label: '单元标签',
      component: 'Input',
      componentProps: {
        placeholder: '如 Unit 1',
      },
    },
    {
      fieldName: 'examLevelCode',
      label: '考试级别',
      component: 'Input',
      componentProps: {
        placeholder: '如 flyers/ket/pet/cae',
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
      fieldName: 'sort',
      label: '排序',
      component: 'InputNumber',
      componentProps: {
        placeholder: '排序值',
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
      fieldName: 'name',
      label: '名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入词书名称',
      },
    },
    {
      fieldName: 'categoryType',
      label: '分类',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '教材', value: 'SCHOOL_GRADE' },
          { label: '考试', value: 'EXAM' },
        ],
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'schoolLevel',
      label: '学段',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '小学', value: 'primary' },
          { label: '初中', value: 'middle' },
          { label: '高中', value: 'high' },
        ],
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'grade',
      label: '年级',
      component: 'InputNumber',
      componentProps: {
        allowClear: true,
        min: 1,
        max: 12,
        placeholder: '年级',
      },
    },
    {
      fieldName: 'examLevelCode',
      label: '考试级别',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '如 flyers',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<DictationWordlistApi.Wordlist>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: 'ID',
      minWidth: 60,
    },
    {
      field: 'name',
      title: '词书名称',
      minWidth: 160,
    },
    {
      field: 'categoryType',
      title: '分类',
      minWidth: 80,
      slots: { default: 'categoryType' },
    },
    {
      field: 'schoolLevel',
      title: '学段',
      minWidth: 70,
      slots: { default: 'schoolLevel' },
    },
    {
      field: 'grade',
      title: '年级',
      minWidth: 60,
    },
    {
      field: 'examLevelCode',
      title: '考试级别',
      minWidth: 80,
    },
    {
      field: 'edition',
      title: '版本',
      minWidth: 80,
    },
    {
      field: 'wordCount',
      title: '单词数',
      minWidth: 70,
    },
    {
      field: 'status',
      title: '状态',
      minWidth: 70,
      slots: { default: 'status' },
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
