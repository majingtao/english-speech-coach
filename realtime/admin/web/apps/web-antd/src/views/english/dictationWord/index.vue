<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { DictationWordApi } from '#/api/english/dictation/word';

import { ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';
import { isEmpty } from '@vben/utils';

import { Modal as AntModal, Tag, message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  batchCreateDictationWords,
  deleteDictationWord,
  getDictationWordPage,
} from '#/api/english/dictation/word';
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

function handleEdit(row: DictationWordApi.Word) {
  formModalApi.setData(row).open();
}

async function handleDelete(row: DictationWordApi.Word) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.en]),
    duration: 0,
  });
  try {
    await deleteDictationWord(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.en]));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

// 批量添加
const batchText = ref('');
function handleBatchAdd() {
  AntModal.confirm({
    title: '批量添加单词',
    content: () => {
      const el = document.createElement('div');
      el.innerHTML = '<textarea id="batchWordInput" rows="8" style="width:100%;padding:8px;font-size:14px;" placeholder="一行一个英文单词"></textarea>';
      return el;
    },
    async onOk() {
      const textarea = document.getElementById('batchWordInput') as HTMLTextAreaElement;
      if (!textarea?.value?.trim()) return;
      const words = textarea.value.split('\n').map((l: string) => l.trim()).filter((l: string) => l);
      const count = await batchCreateDictationWords(words);
      message.success(`成功添加 ${count} 个单词`);
      handleRefresh();
    },
  });
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: DictationWordApi.Word[];
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
          return await getDictationWordPage({
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
  } as VxeTableGridOptions<DictationWordApi.Word>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="听写单词管理">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['单词']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['english:dictation-word:create'],
              onClick: handleCreate,
            },
            {
              label: '批量添加',
              type: 'default',
              auth: ['english:dictation-word:create'],
              onClick: handleBatchAdd,
            },
          ]"
        />
      </template>
      <template #llmStatus="{ row }">
        <Tag :color="row.llmStatus === 1 ? 'green' : row.llmStatus === 2 ? 'red' : 'default'">
          {{ row.llmStatus === 1 ? '已完成' : row.llmStatus === 2 ? '失败' : '待处理' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['english:dictation-word:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['english:dictation-word:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.en]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
