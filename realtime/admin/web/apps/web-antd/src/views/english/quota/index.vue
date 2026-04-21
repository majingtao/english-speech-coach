<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishQuotaApi } from '#/api/english/quota';

import { nextTick, onMounted, ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';

import { Card, message, Tag } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteUserQuota,
  getQuotaDefault,
  getUserQuotaPage,
  resetUserUsage,
  updateQuotaDefault,
} from '#/api/english/quota';
import { $t } from '#/locales';

import {
  useDefaultFormSchema,
  useUserGridColumns,
  useUserGridFormSchema,
} from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

// ========== 默认配置 ==========

const defaultLoading = ref(false);
const defaultSaving = ref(false);

const [DefaultForm, defaultFormApi] = useVbenForm({
  commonConfig: {
    componentProps: { class: 'w-full' },
    formItemClass: 'col-span-2 md:col-span-1',
    labelWidth: 130,
  },
  layout: 'horizontal',
  schema: useDefaultFormSchema(),
  showDefaultActions: false,
});

async function loadDefault() {
  defaultLoading.value = true;
  try {
    const d = await getQuotaDefault();
    if (d) {
      await nextTick();
      await defaultFormApi.setValues(d);
    }
  } finally {
    defaultLoading.value = false;
  }
}

async function handleSaveDefault() {
  const { valid } = await defaultFormApi.validate();
  if (!valid) return;
  defaultSaving.value = true;
  try {
    const data =
      (await defaultFormApi.getValues()) as EnglishQuotaApi.QuotaDefault;
    await updateQuotaDefault(data);
    message.success($t('ui.actionMessage.operationSuccess'));
  } finally {
    defaultSaving.value = false;
  }
}

onMounted(() => {
  loadDefault();
});

// ========== 单用户覆盖 ==========

function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalApi.setData(null).open();
}

function handleEdit(row: EnglishQuotaApi.UserQuota) {
  formModalApi.setData(row).open();
}

async function handleDelete(row: EnglishQuotaApi.UserQuota) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.userId]),
    duration: 0,
  });
  try {
    await deleteUserQuota(row.userId);
    message.success($t('ui.actionMessage.deleteSuccess', [row.userId]));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

async function handleResetUsage(row: EnglishQuotaApi.UserQuota) {
  const hideLoading = message.loading({
    content: `正在重置用户 ${row.userId} 的今日用量...`,
    duration: 0,
  });
  try {
    await resetUserUsage(row.userId);
    message.success('今日用量已清零');
  } finally {
    hideLoading();
  }
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useUserGridFormSchema(),
  },
  gridOptions: {
    columns: useUserGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getUserQuotaPage({
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
  } as VxeTableGridOptions<EnglishQuotaApi.UserQuota>,
});

function fmtLimit(v?: null | number) {
  return v == null ? '默认' : String(v);
}
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />

    <Card class="mb-4" title="全局默认配额">
      <template #extra>
        <a-button
          :loading="defaultSaving"
          :disabled="defaultLoading"
          type="primary"
          @click="handleSaveDefault"
        >
          保存默认配额
        </a-button>
      </template>
      <DefaultForm />
      <div class="mt-2 text-xs text-gray-500">
        默认配额适用于所有未做单独覆盖的用户。单用户覆盖中留空的字段会回落到这里的默认值。
      </div>
    </Card>

    <Grid table-title="单用户配额覆盖">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['用户配额']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['esc:quota:update'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #enabled="{ row }">
        <Tag :color="row.enabled ? 'green' : 'red'">
          {{ row.enabled ? '启用' : '冻结' }}
        </Tag>
      </template>
      <template #llmDaily="{ row }">{{ fmtLimit(row.llmDaily) }}</template>
      <template #asrDailySec="{ row }">{{ fmtLimit(row.asrDailySec) }}</template>
      <template #ttsDailyChars="{ row }">
        {{ fmtLimit(row.ttsDailyChars) }}
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['esc:quota:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: '清零用量',
              type: 'link',
              auth: ['esc:quota:update'],
              popConfirm: {
                title: `确认将用户 ${row.userId} 今日 LLM/ASR/TTS 已用量清零？`,
                confirm: handleResetUsage.bind(null, row),
              },
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['esc:quota:update'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.userId]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
