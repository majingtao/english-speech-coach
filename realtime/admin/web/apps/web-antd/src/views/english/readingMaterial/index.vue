<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishReadingMaterialApi } from '#/api/english/readingMaterial';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteReadingMaterial,
  getReadingMaterial,
  getReadingMaterialPage,
  updateReadingMaterial,
} from '#/api/english/readingMaterial';
import { $t } from '#/locales';

import {
  completeReadingMaterialWithAi,
  getStoredAiSettings,
} from './ai-complete';
import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});
const refresh = () => gridApi.query();
const create = () => formModalApi.setData(null).open();
const edit = (row: EnglishReadingMaterialApi.Material) =>
  formModalApi.setData(row).open();

function parseTags(value?: string) {
  try {
    const tags = JSON.parse(value || '[]');
    return Array.isArray(tags) ? tags : [];
  } catch {
    return [];
  }
}

async function remove(row: EnglishReadingMaterialApi.Material) {
  await deleteReadingMaterial(row.id!);
  message.success('删除成功');
  refresh();
}

async function completeRowWithAi(row: EnglishReadingMaterialApi.Material) {
  const settings = getStoredAiSettings();
  if (!settings.apiKey || !settings.baseUrl || !settings.model) {
    message.warning('请先在新增或编辑弹窗里填写 AI 配置');
    return;
  }
  const hide = message.loading(`AI 正在完善：${row.textEn}`, 0);
  try {
    const material = await getReadingMaterial(row.id!);
    const patch = await completeReadingMaterialWithAi(material, settings);
    await updateReadingMaterial({
      ...material,
      ...patch,
    });
    message.success('AI 已完善并保存');
    refresh();
  } catch (error) {
    message.error(error instanceof Error ? error.message : 'AI 完善失败');
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
        query: async ({ page }, values) =>
          getReadingMaterialPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...values,
          }),
      },
    },
    rowConfig: { keyField: 'id', isHover: true },
    toolbarConfig: { refresh: true, search: true },
  } as VxeTableGridOptions<EnglishReadingMaterialApi.Material>,
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="refresh" />
    <Grid table-title="自由跟读素材">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新增素材',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['english:reading-material:create'],
              onClick: create,
            },
          ]"
        />
      </template>
      <template #materialType="{ row }">
        <Tag v-if="row.materialType === 'word'" color="blue">单词</Tag>
        <Tag v-else-if="row.materialType === 'phrase'" color="purple">短语</Tag>
        <Tag v-else color="orange">句子</Tag>
      </template>
      <template #tags="{ row }">
        <Tag v-for="tag in parseTags(row.tagsJson)" :key="tag">{{ tag }}</Tag>
      </template>
      <template #priority="{ row }">
        <Tag v-if="row.mustKnow === 1" color="red">必会</Tag>
        <Tag v-if="row.highFrequency === 1" color="gold">高频</Tag>
        <Tag v-if="row.mustSpell === 1" color="magenta">必默</Tag>
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
              auth: ['english:reading-material:update'],
              onClick: edit.bind(null, row),
            },
            {
              label: 'AI完善',
              type: 'link',
              auth: ['english:reading-material:update'],
              onClick: completeRowWithAi.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['english:reading-material:delete'],
              popConfirm: {
                title: '确认删除该素材？',
                confirm: remove.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
