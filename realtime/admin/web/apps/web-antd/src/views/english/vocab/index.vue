<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishVocabApi } from '#/api/english/vocab';

import { Page, useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteVocab, getVocabPage } from '#/api/english/vocab';
import { DictTag } from '#/components/dict-tag';
import { $t } from '#/locales';

import BatchImport from './modules/batch-import.vue';
import ContentPreview from './modules/content-preview.vue';
import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});
const [PreviewModal, previewModalApi] = useVbenModal({
  connectedComponent: ContentPreview,
  destroyOnClose: true,
});
const [ImportModal, importModalApi] = useVbenModal({
  connectedComponent: BatchImport,
  destroyOnClose: true,
});

function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalApi.setData(null).open();
}

function handleEdit(row: EnglishVocabApi.Vocab) {
  formModalApi.setData(row).open();
}

function handlePreview(row: EnglishVocabApi.Vocab) {
  previewModalApi.setData({ id: row.id }).open();
}

function handleBatchImport() {
  importModalApi.open();
}

async function handleDelete(row: EnglishVocabApi.Vocab) {
  const hide = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    duration: 0,
  });
  try {
    await deleteVocab(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.id]));
    handleRefresh();
  } finally {
    hide();
  }
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: { schema: useGridFormSchema() },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) =>
          await getVocabPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          }),
      },
    },
    rowConfig: { keyField: 'id', isHover: true },
    toolbarConfig: { refresh: true, search: true },
  } as VxeTableGridOptions<EnglishVocabApi.Vocab>,
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <PreviewModal @success="handleRefresh" />
    <ImportModal @success="handleRefresh" />
    <Grid table-title="词汇管理">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['词条']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['english:vocab:create'],
              onClick: handleCreate,
            },
            {
              label: '批量导入',
              type: 'primary',
              auth: ['english:vocab:create'],
              onClick: handleBatchImport,
            },
          ]"
        />
      </template>
      <template #difficulty="{ row }">
        <DictTag
          :type="DICT_TYPE.ENGLISH_VOCAB_DIFFICULTY"
          :value="row.difficulty"
        />
      </template>
      <template #masteryLevel="{ row }">
        <DictTag
          :type="DICT_TYPE.ENGLISH_VOCAB_MASTERY"
          :value="row.masteryLevel"
        />
      </template>
      <template #status="{ row }">
        <Tag v-if="row.status === 0">草稿</Tag>
        <Tag v-else-if="row.status === 1" color="green">发布</Tag>
        <Tag v-else color="red">归档</Tag>
      </template>
      <template #hasContent="{ row }">
        <Tag v-if="row.contentJson" color="green">已生成</Tag>
        <Tag v-else color="default">未生成</Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '查看',
              type: 'link',
              auth: ['english:vocab:query'],
              onClick: handlePreview.bind(null, row),
            },
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['english:vocab:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['english:vocab:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.id]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
