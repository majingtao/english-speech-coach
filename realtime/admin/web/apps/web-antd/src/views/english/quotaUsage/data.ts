import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishQuotaApi } from '#/api/english/quota';

/** 搜索表单 */
export function useUsageGridFormSchema(): VbenFormSchema[] {
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
      fieldName: 'dateFrom',
      label: '起始日',
      component: 'DatePicker',
      componentProps: {
        allowClear: true,
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        class: 'w-full',
      },
    },
    {
      fieldName: 'dateTo',
      label: '截止日',
      component: 'DatePicker',
      componentProps: {
        allowClear: true,
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        class: 'w-full',
      },
    },
  ];
}

/** 列表字段 */
export function useUsageGridColumns(): VxeTableGridOptions<EnglishQuotaApi.UsageDaily>['columns'] {
  return [
    {
      field: 'statDate',
      title: '统计日',
      minWidth: 110,
    },
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
      field: 'llmUsed',
      title: 'LLM 次数',
      minWidth: 100,
    },
    {
      field: 'asrUsedSec',
      title: 'ASR 秒数',
      minWidth: 100,
    },
    {
      field: 'ttsUsedChars',
      title: 'TTS 字符',
      minWidth: 110,
    },
    {
      field: 'createTime',
      title: '归档时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
  ];
}
