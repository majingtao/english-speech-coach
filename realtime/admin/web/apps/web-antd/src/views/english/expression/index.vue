<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishExpressionApi } from '#/api/english/expression';

import { Page, useVbenModal } from '@vben/common-ui';
import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteExpressionItem, getExpressionItemPage } from '#/api/english/expression';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';
import ThemeManager from './modules/theme-manager.vue';

const [FormModal, formModalApi] = useVbenModal({ connectedComponent: Form, destroyOnClose: true });
const [ThemeModal, themeModalApi] = useVbenModal({ connectedComponent: ThemeManager, destroyOnClose: true });
const refresh = () => gridApi.query();
const create = () => formModalApi.setData(null).open();
const edit = (row: EnglishExpressionApi.Item) => formModalApi.setData(row).open();

async function remove(row: EnglishExpressionApi.Item) {
  await deleteExpressionItem(row.id!);
  message.success('删除成功');
  refresh();
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: { schema: useGridFormSchema() },
  gridOptions: {
    columns: useGridColumns(), height: 'auto', keepSource: true,
    proxyConfig: { ajax: { query: async ({ page }, values) => getExpressionItemPage({
      pageNo: page.currentPage, pageSize: page.pageSize, ...values,
    }) } },
    rowConfig: { keyField: 'id', isHover: true },
    toolbarConfig: { refresh: true, search: true },
  } as VxeTableGridOptions<EnglishExpressionApi.Item>,
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="refresh" />
    <ThemeModal @success="refresh" />
    <Grid table-title="表达练习管理">
      <template #toolbar-tools>
        <TableAction :actions="[
          { label: '主题管理', type: 'default', auth: ['english:expression:query'], onClick: () => themeModalApi.open() },
          { label: '新增练习', type: 'primary', icon: ACTION_ICON.ADD, auth: ['english:expression:create'], onClick: create },
        ]" />
      </template>
      <template #status="{ row }">
        <Tag v-if="row.status === 0">草稿</Tag>
        <Tag v-else-if="row.status === 1" color="green">发布</Tag>
        <Tag v-else color="red">归档</Tag>
      </template>
      <template #actions="{ row }">
        <TableAction :actions="[
          { label: $t('common.edit'), type: 'link', icon: ACTION_ICON.EDIT, auth: ['english:expression:update'], onClick: edit.bind(null, row) },
          { label: $t('common.delete'), type: 'link', danger: true, icon: ACTION_ICON.DELETE, auth: ['english:expression:delete'], popConfirm: { title: '确认删除该练习？', confirm: remove.bind(null, row) } },
        ]" />
      </template>
    </Grid>
  </Page>
</template>
