<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishSynonymApi } from '#/api/english/synonym';
import { Page, useVbenModal } from '@vben/common-ui';
import { message, Tag } from 'ant-design-vue';
import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteSynonymPoint, getSynonymPointPage, updateSynonymPoint } from '#/api/english/synonym';
import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formApi] = useVbenModal({ connectedComponent: Form, destroyOnClose: true });
const refresh = () => gridApi.query();
const edit = (row: EnglishSynonymApi.Point) => formApi.setData(row).open();

async function remove(row: EnglishSynonymApi.Point) {
  await deleteSynonymPoint(row.id!);
  message.success('删除成功');
  refresh();
}

async function toggle(row: EnglishSynonymApi.Point) {
  await updateSynonymPoint({ ...row, status: row.status === 1 ? 2 : 1 });
  message.success(row.status === 1 ? '已停用' : '已发布');
  refresh();
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: { schema: useGridFormSchema() },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, values) =>
          getSynonymPointPage({ pageNo: page.currentPage, pageSize: page.pageSize, ...values }),
      },
    },
    rowConfig: { keyField: 'id', isHover: true },
    toolbarConfig: { refresh: true, search: true },
  } as VxeTableGridOptions<EnglishSynonymApi.Point>,
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="refresh" />
    <Grid table-title="同义替换题库">
      <template #toolbar-tools>
        <TableAction :actions="[{ label: '手工新增', type: 'primary', icon: ACTION_ICON.ADD, auth: ['english:synonym:create'], onClick: () => formApi.setData(null).open() }]" />
      </template>
      <template #mode="{ row }">
        <Tag v-if="row.mode === 'synonym'" color="blue">同义词</Tag>
        <Tag v-else color="orange">反义词</Tag>
      </template>
      <template #status="{ row }">
        <Tag v-if="row.status === 0">草稿</Tag>
        <Tag v-else-if="row.status === 1" color="green">发布</Tag>
        <Tag v-else color="red">停用</Tag>
      </template>
      <template #actions="{ row }">
        <TableAction :actions="[
          { label: '编辑', type: 'link', icon: ACTION_ICON.EDIT, auth: ['english:synonym:update'], onClick: edit.bind(null, row) },
          { label: row.status === 1 ? '停用' : '发布', type: 'link', auth: ['english:synonym:update'], onClick: toggle.bind(null, row) },
          { label: '删除', type: 'link', danger: true, icon: ACTION_ICON.DELETE, auth: ['english:synonym:delete'], popConfirm: { title: '确认删除？', confirm: remove.bind(null, row) } },
        ]" />
      </template>
    </Grid>
  </Page>
</template>
