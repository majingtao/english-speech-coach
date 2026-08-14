<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EnglishGrammarApi } from '#/api/english/grammar';
import { Page, useVbenModal } from '@vben/common-ui';
import { message, Tag } from 'ant-design-vue';
import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteGrammarQuestion, getGrammarQuestionPage, updateGrammarQuestion } from '#/api/english/grammar';
import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue'; import Generate from './modules/generate.vue';
const [FormModal, formApi] = useVbenModal({ connectedComponent: Form, destroyOnClose: true });
const [GenerateModal, generateApi] = useVbenModal({ connectedComponent: Generate, destroyOnClose: true });
const refresh = () => gridApi.query(); const edit = (row: EnglishGrammarApi.Question) => formApi.setData(row).open();
async function remove(row: EnglishGrammarApi.Question) { await deleteGrammarQuestion(row.id!); message.success('删除成功'); refresh(); }
async function toggle(row: EnglishGrammarApi.Question) { await updateGrammarQuestion({ ...row, status: row.status === 1 ? 2 : 1 }); message.success(row.status === 1 ? '已停用' : '已发布'); refresh(); }
const [Grid, gridApi] = useVbenVxeGrid({ formOptions: { schema: useGridFormSchema() }, gridOptions: { columns: useGridColumns(), height: 'auto', keepSource: true, proxyConfig: { ajax: { query: async ({ page }, values) => getGrammarQuestionPage({ pageNo: page.currentPage, pageSize: page.pageSize, ...values }) } }, rowConfig: { keyField: 'id', isHover: true }, toolbarConfig: { refresh: true, search: true } } as VxeTableGridOptions<EnglishGrammarApi.Question> });
</script>
<template><Page auto-content-height><FormModal @success="refresh" /><GenerateModal @success="refresh" /><Grid table-title="KET 语法题库">
  <template #toolbar-tools><TableAction :actions="[{label:'AI批量生成',type:'primary',auth:['english:grammar:create'],onClick:()=>generateApi.open()},{label:'手工新增',type:'default',icon:ACTION_ICON.ADD,auth:['english:grammar:create'],onClick:()=>formApi.setData(null).open()}]" /></template>
  <template #status="{row}"><Tag v-if="row.status===0">草稿</Tag><Tag v-else-if="row.status===1" color="green">发布</Tag><Tag v-else color="red">停用</Tag></template>
  <template #actions="{row}"><TableAction :actions="[{label:'编辑',type:'link',icon:ACTION_ICON.EDIT,auth:['english:grammar:update'],onClick:edit.bind(null,row)},{label:row.status===1?'停用':'发布',type:'link',auth:['english:grammar:update'],onClick:toggle.bind(null,row)},{label:'删除',type:'link',danger:true,icon:ACTION_ICON.DELETE,auth:['english:grammar:delete'],popConfirm:{title:'确认删除？',confirm:remove.bind(null,row)}}]" /></template>
</Grid></Page></template>
