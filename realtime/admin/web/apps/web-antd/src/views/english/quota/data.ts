import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishQuotaApi } from '#/api/english/quota';

/** 全局默认配额表单 */
export function useDefaultFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'llmDaily',
      label: 'LLM 每日次数',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '每日可用 LLM 调用次数',
        class: 'w-full',
      },
    },
    {
      fieldName: 'asrDailySec',
      label: 'ASR 每日秒数',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '每日可用 ASR 识别秒数',
        class: 'w-full',
      },
    },
    {
      fieldName: 'ttsDailyChars',
      label: 'TTS 每日字符',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '每日可用 TTS 合成字符数',
        class: 'w-full',
      },
    },
  ];
}

/** 单用户配额新增/修改表单 */
export function useUserFormSchema(): VbenFormSchema[] {
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
      fieldName: 'userId',
      label: '用户 ID',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        min: 1,
        placeholder: '请输入会员用户 ID',
        class: 'w-full',
      },
    },
    {
      fieldName: 'enabled',
      label: '是否启用',
      rules: 'required',
      component: 'RadioGroup',
      defaultValue: true,
      componentProps: {
        options: [
          { label: '启用', value: true },
          { label: '冻结', value: false },
        ],
        buttonStyle: 'solid',
        optionType: 'button',
      },
    },
    {
      fieldName: 'llmDaily',
      label: 'LLM 每日次数',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '留空 = 使用默认',
        class: 'w-full',
      },
    },
    {
      fieldName: 'asrDailySec',
      label: 'ASR 每日秒数',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '留空 = 使用默认',
        class: 'w-full',
      },
    },
    {
      fieldName: 'ttsDailyChars',
      label: 'TTS 每日字符',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '留空 = 使用默认',
        class: 'w-full',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: {
        rows: 2,
        placeholder: '调整原因、申请人等',
      },
    },
  ];
}

/** 单用户列表搜索表单 */
export function useUserGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'userId',
      label: '用户 ID',
      component: 'InputNumber',
      componentProps: {
        allowClear: true,
        placeholder: '精确匹配',
        class: 'w-full',
      },
    },
    {
      fieldName: 'nickname',
      label: '昵称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '模糊搜索',
      },
    },
    {
      fieldName: 'enabled',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '启用', value: true },
          { label: '冻结', value: false },
        ],
        placeholder: '全部',
      },
    },
  ];
}

/** 单用户列表字段 */
export function useUserGridColumns(): VxeTableGridOptions<EnglishQuotaApi.UserQuota>['columns'] {
  return [
    {
      field: 'userId',
      title: '用户 ID',
      minWidth: 100,
    },
    {
      field: 'nickname',
      title: '昵称',
      minWidth: 140,
    },
    {
      field: 'enabled',
      title: '状态',
      minWidth: 80,
      slots: { default: 'enabled' },
    },
    {
      field: 'llmDaily',
      title: 'LLM/天',
      minWidth: 100,
      slots: { default: 'llmDaily' },
    },
    {
      field: 'asrDailySec',
      title: 'ASR 秒/天',
      minWidth: 110,
      slots: { default: 'asrDailySec' },
    },
    {
      field: 'ttsDailyChars',
      title: 'TTS 字符/天',
      minWidth: 120,
      slots: { default: 'ttsDailyChars' },
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 160,
    },
    {
      field: 'createTime',
      title: '添加时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
