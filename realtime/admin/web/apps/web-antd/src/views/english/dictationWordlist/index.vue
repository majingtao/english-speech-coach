<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { DictationWordlistApi } from '#/api/english/dictation/wordlist';

import { ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';

import { Tag, message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteDictationWordlist,
  getDictationWordlistPage,
} from '#/api/english/dictation/wordlist';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalApi.setData(null).open();
}

function handleEdit(row: DictationWordlistApi.Wordlist) {
  formModalApi.setData(row).open();
}

async function handleDelete(row: DictationWordlistApi.Wordlist) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.name]),
    duration: 0,
  });
  try {
    await deleteDictationWordlist(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.name]));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: DictationWordlistApi.Wordlist[];
}) {
  checkedIds.value = records.map((item) => item.id!);
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getDictationWordlistPage({
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
  } as VxeTableGridOptions<DictationWordlistApi.Wordlist>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="听写词书管理">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['词书']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['english:dictation-wordlist:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #categoryType="{ row }">
        <Tag :color="row.categoryType === 'EXAM' ? 'blue' : 'green'">
          {{ row.categoryType === 'EXAM' ? '考试' : '教材' }}
        </Tag>
      </template>
      <template #schoolLevel="{ row }">
        {{ row.schoolLevel === 'primary' ? '小学' : row.schoolLevel === 'middle' ? '初中' : row.schoolLevel === 'high' ? '高中' : '' }}
      </template>
      <template #status="{ row }">
        <Tag :color="row.status === 1 ? 'green' : row.status === 2 ? 'red' : 'default'">
          {{ row.status === 1 ? '发布' : row.status === 2 ? '下架' : '草稿' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['english:dictation-wordlist:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['english:dictation-wordlist:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.name]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
