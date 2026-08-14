<script lang="ts" setup>
import type { EnglishGrammarApi } from '#/api/english/grammar';
import { computed, ref } from 'vue';
import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import { useVbenForm } from '#/adapter/form';
import { createGrammarQuestion, getGrammarPoints, getGrammarQuestion, updateGrammarQuestion } from '#/api/english/grammar';

const emit = defineEmits(['success']);
const editing = ref<EnglishGrammarApi.Question>();
const [Form, formApi] = useVbenForm({ commonConfig: { componentProps: { class: 'w-full' }, formItemClass: 'col-span-2', labelWidth: 110 }, showDefaultActions: false, schema: [
  { fieldName: 'id', component: 'Input', dependencies: { triggerFields: [''], show: () => false } },
  { fieldName: 'grammarPointId', label: '知识点', rules: 'required', component: 'ApiSelect', componentProps: { api: getGrammarPoints, labelField: 'nameCn', valueField: 'id' } },
  { fieldName: 'code', label: '编码', rules: 'required', component: 'Input' },
  { fieldName: 'questionType', label: '题型', rules: 'required', component: 'RadioGroup', defaultValue: 'single_choice', componentProps: { options: [{ label: '单选', value: 'single_choice' }, { label: '填空', value: 'text_input' }] } },
  { fieldName: 'difficulty', label: '难度', rules: 'required', component: 'RadioGroup', defaultValue: 1, componentProps: { options: [{ label: 'L1', value: 1 }, { label: 'L2', value: 2 }, { label: 'L3', value: 3 }, { label: 'L4', value: 4 }] } },
  { fieldName: 'instruction', label: '英文指令', component: 'Input' },
  { fieldName: 'stem', label: '题干', rules: 'required', component: 'Textarea', componentProps: { rows: 3 } },
  { fieldName: 'optionsJson', label: '选项 JSON', component: 'Textarea', componentProps: { rows: 4, placeholder: '["go","goes","going","went"]' } },
  { fieldName: 'answerJson', label: '答案 JSON', rules: 'required', component: 'Textarea', componentProps: { rows: 3, placeholder: '["goes"]' } },
  { fieldName: 'explanationZh', label: '中文解析', rules: 'required', component: 'Textarea', componentProps: { rows: 3 } },
  { fieldName: 'ruleText', label: '语法规则', component: 'Textarea', componentProps: { rows: 2 } },
  { fieldName: 'errorTagsJson', label: '错误提示 JSON', component: 'Textarea', componentProps: { rows: 4 } },
  { fieldName: 'mediaJson', label: '媒体 JSON', component: 'Textarea', defaultValue: 'null' },
  { fieldName: 'status', label: '状态', rules: 'required', component: 'RadioGroup', defaultValue: 1, componentProps: { options: [{ label: '草稿', value: 0 }, { label: '发布', value: 1 }, { label: '停用', value: 2 }] } },
] });
const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate(); if (!valid) return;
    const data = await formApi.getValues() as EnglishGrammarApi.Question;
    try { JSON.parse(data.optionsJson || '[]'); JSON.parse(data.answerJson); JSON.parse(data.errorTagsJson || '{}'); JSON.parse(data.mediaJson || 'null'); }
    catch { message.error('题目 JSON 配置格式不正确'); return; }
    modalApi.lock(); try { await (editing.value?.id ? updateGrammarQuestion(data) : createGrammarQuestion(data)); await modalApi.close(); emit('success'); message.success('保存成功'); } finally { modalApi.unlock(); }
  },
  async onOpenChange(open) {
    if (!open) { editing.value = undefined; return; }
    const row = modalApi.getData<EnglishGrammarApi.Question>();
    if (!row?.id) { await formApi.resetForm(); await formApi.setValues({ code: `grammar-${Date.now()}`, difficulty: 1, questionType: 'single_choice', optionsJson: '[]', answerJson: '[]', errorTagsJson: '{}', mediaJson: 'null', status: 1 }); return; }
    modalApi.lock(); try { editing.value = await getGrammarQuestion(row.id); await formApi.setValues(editing.value); } finally { modalApi.unlock(); }
  },
});
const title = computed(() => editing.value?.id ? '编辑语法题' : '新增语法题');
</script>
<template><Modal :title="title" class="w-[860px]"><Form class="mx-4" /></Modal></template>
