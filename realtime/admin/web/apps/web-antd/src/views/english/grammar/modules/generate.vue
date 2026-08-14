<script lang="ts" setup>
import type { EnglishGrammarApi } from '#/api/english/grammar';
import { ref } from 'vue';
import { useVbenModal } from '@vben/common-ui';
import { Button, Checkbox, Input, InputNumber, message, Select, Switch } from 'ant-design-vue';
import { getGrammarPoints, importGeneratedGrammarQuestions } from '#/api/english/grammar';
import { generateGrammarQuestions, getGrammarAiSettings, saveGrammarAiSettings } from '../ai-generate';

const emit = defineEmits(['success']);
const points = ref<EnglishGrammarApi.Point[]>([]); const pointId = ref<number>(); const difficulty = ref(1); const count = ref(10);
const types = ref(['single_choice']); const scene = ref('school, family, hobbies, travel'); const autoPublish = ref(true); const loading = ref(false);
const settings = ref(getGrammarAiSettings());
async function generate() {
  const point = points.value.find((v) => v.id === pointId.value);
  if (!point || !settings.value.baseUrl || !settings.value.apiKey || !settings.value.model || types.value.length === 0) { message.warning('请完整填写知识点、题型和 AI 配置'); return; }
  saveGrammarAiSettings(settings.value); loading.value = true;
  try {
    const questions = await generateGrammarQuestions({ point, difficulty: difficulty.value, count: count.value, types: types.value, scene: scene.value, settings: settings.value });
    const job = await importGeneratedGrammarQuestions({ grammarPointId: point.id, difficulty: difficulty.value, questionTypes: types.value.join(','), autoPublish: autoPublish.value, model: settings.value.model, settingsJson: JSON.stringify({ scene: scene.value }), questions });
    message.success(`生成并保存 ${job.acceptedCount || questions.length} 道题${autoPublish.value ? '，已直接发布' : '，等待审核'}`); emit('success'); modalApi.close();
  } catch (error) { message.error(error instanceof Error ? error.message : '生成失败'); } finally { loading.value = false; }
}
const [Modal, modalApi] = useVbenModal({ async onOpenChange(open) { if (open && points.value.length === 0) { points.value = await getGrammarPoints(); pointId.value = points.value[0]?.id; } } });
</script>
<template><Modal title="AI 批量生成语法题" class="w-[760px]" :footer="false">
  <div class="mx-4 grid gap-4 pb-4">
    <div class="grid grid-cols-3 gap-3"><Select v-model:value="pointId" placeholder="知识点" :options="points.map(p => ({label: `${p.nameCn} / ${p.nameEn}`, value: p.id}))" /><Select v-model:value="difficulty" :options="[{label:'L1 认识规则',value:1},{label:'L2 基础使用',value:2}]" /><InputNumber v-model:value="count" :min="1" :max="20" addon-after="题" /></div>
    <div><div class="mb-2 font-medium">题型</div><Checkbox.Group v-model:value="types" :options="[{label:'单项选择',value:'single_choice'},{label:'文本填空',value:'text_input'}]" /></div>
    <Input v-model:value="scene" addon-before="场景" />
    <div class="grid grid-cols-3 gap-2"><Input v-model:value="settings.baseUrl" placeholder="AI Base URL" /><Input.Password v-model:value="settings.apiKey" placeholder="API Key（仅本浏览器）" /><Input v-model:value="settings.model" placeholder="模型" /></div>
    <div class="flex items-center justify-between rounded border p-3"><div><strong>通过校验后直接发布</strong><div class="text-xs text-gray-500">关闭后进入草稿，保留人工确认流程</div></div><Switch v-model:checked="autoPublish" /></div>
    <Button type="primary" size="large" :loading="loading" @click="generate">生成并保存题库</Button>
  </div>
</Modal></template>
