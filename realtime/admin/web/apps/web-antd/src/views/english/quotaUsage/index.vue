<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishQuotaApi } from '#/api/english/quota';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { DatePicker, message, Modal, Space } from 'ant-design-vue';
import dayjs, { type Dayjs } from 'dayjs';
import { h } from 'vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { archiveUsage, getUsagePage } from '#/api/english/quota';

import { useUsageGridColumns, useUsageGridFormSchema } from './data';

const archiving = ref(false);

function handleRefresh() {
  gridApi.query();
}

async function runArchive(date?: string, label?: string) {
  archiving.value = true;
  try {
    const n = await archiveUsage(date);
    message.success(`已归档 ${n} 名用户（${label || date || '昨日'}）`);
    handleRefresh();
  } finally {
    archiving.value = false;
  }
}

function handleArchiveYesterday() {
  return runArchive(undefined, '昨日');
}

function handleArchiveToday() {
  return runArchive(dayjs().format('YYYY-MM-DD'), '今日');
}

function handleArchivePickDate() {
  const picked = ref<Dayjs>(dayjs().subtract(1, 'day'));
  Modal.confirm({
    title: '选择归档日期',
    content: () =>
      h(Space, { direction: 'vertical' }, () => [
        h('div', '将扫描该日 Redis 计数 (esc:used:*:*:yyyyMMdd) 并 UPSERT 到归档表。'),
        h(DatePicker, {
          value: picked.value,
          'onUpdate:value': (v: Dayjs) => (picked.value = v),
          allowClear: false,
          format: 'YYYY-MM-DD',
        }),
      ]),
    async onOk() {
      const d = picked.value?.format('YYYY-MM-DD');
      if (!d) return;
      await runArchive(d, d);
    },
  });
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useUsageGridFormSchema(),
  },
  gridOptions: {
    columns: useUsageGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getUsagePage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
  } as VxeTableGridOptions<EnglishQuotaApi.UsageDaily>,
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="每日用量归档">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: archiving ? '归档中...' : '归档今日',
              type: 'primary',
              icon: ACTION_ICON.REFRESH,
              auth: ['esc:quota:update'],
              disabled: archiving,
              onClick: handleArchiveToday,
            },
            {
              label: '归档昨日',
              auth: ['esc:quota:update'],
              disabled: archiving,
              onClick: handleArchiveYesterday,
            },
            {
              label: '指定日期…',
              auth: ['esc:quota:update'],
              disabled: archiving,
              onClick: handleArchivePickDate,
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
