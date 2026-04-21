<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExamApi } from '#/api/english/exam';

import { ref } from 'vue';

import { confirm, Page, useVbenModal } from '@vben/common-ui';
import { isEmpty } from '@vben/utils';

import { Tag, message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteExam,
  getExamPage,
  migrateToJson,
} from '#/api/english/exam';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';
import JsonEditor from './modules/json-editor.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

const [JsonEditorModal, jsonEditorModalApi] = useVbenModal({
  connectedComponent: JsonEditor,
  destroyOnClose: true,
});

function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalApi.setData(null).open();
}

function handleEdit(row: EnglishExamApi.Exam) {
  formModalApi.setData(row).open();
}

function handleEditJson(row: EnglishExamApi.Exam) {
  jsonEditorModalApi.setData({ id: row.id, examCode: row.examCode }).open();
}

async function handleDelete(row: EnglishExamApi.Exam) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.examCode]),
    duration: 0,
  });
  try {
    await deleteExam(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.examCode]));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

async function handleDeleteBatch() {
  await confirm($t('ui.actionMessage.deleteBatchConfirm'));
  // batch delete not implemented for exam
}

async function handleMigrate() {
  await confirm('确认将所有试卷的多表数据迁移到 content_json？');
  const hideLoading = message.loading({
    content: '正在迁移...',
    duration: 0,
  });
  try {
    const result = await migrateToJson();
    message.success(result || '迁移完成');
    handleRefresh();
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: EnglishExamApi.Exam[];
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
          return await getExamPage({
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
  } as VxeTableGridOptions<EnglishExamApi.Exam>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <JsonEditorModal @success="handleRefresh" />
    <Grid table-title="试卷管理">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['试卷']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['english:exam:create'],
              onClick: handleCreate,
            },
            {
              label: '迁移到JSON',
              type: 'default',
              onClick: handleMigrate,
              auth: ['english:exam:update'],
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['english:exam:delete'],
              disabled: isEmpty(checkedIds),
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #isActive="{ row }">
        <Tag :color="row.isActive === 1 ? 'green' : 'default'">
          {{ row.isActive === 1 ? '是' : '否' }}
        </Tag>
      </template>
      <template #status="{ row }">
        <Tag :color="row.status === 1 ? 'green' : row.status === 2 ? 'red' : 'default'">
          {{ row.status === 1 ? '发布' : row.status === 2 ? '下架' : '草稿' }}
        </Tag>
      </template>
      <template #hasJson="{ row }">
        <Tag :color="row.contentJson ? 'green' : 'default'">
          {{ row.contentJson ? '有' : '无' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '编辑JSON',
              type: 'link',
              auth: ['english:exam:update'],
              onClick: handleEditJson.bind(null, row),
            },
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['english:exam:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['english:exam:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.examCode]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
