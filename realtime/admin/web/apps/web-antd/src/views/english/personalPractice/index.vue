<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { PersonalPracticeApi } from '#/api/english/personalPractice';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deletePersonalPractice,
  getPersonalPracticePage,
} from '#/api/english/personalPractice';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import PracticeForm from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: PracticeForm,
  destroyOnClose: true,
});
const refresh = () => gridApi.query();
const create = () => formModalApi.setData(null).open();
const edit = (row: PersonalPracticeApi.Practice) =>
  formModalApi.setData(row).open();

async function remove(row: PersonalPracticeApi.Practice) {
  await deletePersonalPractice(row.id!);
  message.success('删除成功');
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
          getPersonalPracticePage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...values,
          }),
      },
    },
    rowConfig: { keyField: 'id', isHover: true },
    toolbarConfig: { refresh: true, search: true },
  } as VxeTableGridOptions<PersonalPracticeApi.Practice>,
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="refresh" />
    <Grid table-title="专属口语与写作">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新增练习',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['english:personal-practice:create'],
              onClick: create,
            },
          ]"
        />
      </template>
      <template #practiceType="{ row }">
        <Tag :color="row.practiceType === 'speaking' ? 'blue' : 'purple'">
          {{ row.practiceType === 'speaking' ? '专属口语' : '专属写作' }}
        </Tag>
      </template>
      <template #status="{ row }">
        <Tag v-if="row.status === 0">草稿</Tag>
        <Tag v-else-if="row.status === 1" color="green">发布</Tag>
        <Tag v-else color="red">归档</Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['english:personal-practice:update'],
              onClick: edit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['english:personal-practice:delete'],
              popConfirm: {
                title: '确认删除该练习？',
                confirm: remove.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
