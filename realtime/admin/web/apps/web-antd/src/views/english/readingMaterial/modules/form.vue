<script lang="ts" setup>
import type { EnglishReadingMaterialApi } from '#/api/english/readingMaterial';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, Input, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createReadingMaterial,
  getReadingMaterial,
  updateReadingMaterial,
} from '#/api/english/readingMaterial';
import { $t } from '#/locales';

import {
  completeReadingMaterialWithAi,
  getStoredAiSettings,
  saveAiSettings,
} from '../ai-complete';
import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<EnglishReadingMaterialApi.Material>();
const aiLoading = ref(false);
const storedAiSettings = getStoredAiSettings();
const aiBaseUrl = ref(storedAiSettings.baseUrl);
const aiApiKey = ref(storedAiSettings.apiKey);
const aiModel = ref(storedAiSettings.model);

const title = computed(() =>
  formData.value?.id ? '编辑自由跟读素材' : '新增自由跟读素材',
);

function prettyJson(value?: string, fallback = '') {
  if (!value) return fallback;
  try {
    return JSON.stringify(JSON.parse(value), null, 2);
  } catch {
    return value;
  }
}

async function completeWithAi() {
  const values =
    (await formApi.getValues()) as EnglishReadingMaterialApi.Material;
  const textEn = values.textEn?.trim();
  if (!textEn) {
    message.warning('请先输入英文内容');
    return;
  }
  if (!aiBaseUrl.value || !aiApiKey.value || !aiModel.value) {
    message.warning('请先填写 AI Base URL、API Key 和模型');
    return;
  }
  const settings = {
    apiKey: aiApiKey.value,
    baseUrl: aiBaseUrl.value,
    model: aiModel.value,
  };
  saveAiSettings(settings);

  aiLoading.value = true;
  try {
    await formApi.setValues(
      await completeReadingMaterialWithAi({ textEn }, settings),
    );
    message.success('AI 已完善表单，请检查后保存');
  } catch (error) {
    message.error(error instanceof Error ? error.message : 'AI 完善失败');
  } finally {
    aiLoading.value = false;
  }
}

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: { class: 'w-full' },
    formItemClass: 'col-span-2',
    labelWidth: 100,
  },
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) return;
    const data =
      (await formApi.getValues()) as EnglishReadingMaterialApi.Material;
    try {
      JSON.parse(data.tagsJson || '[]');
      JSON.parse(data.examplesJson || '[]');
      JSON.parse(data.wordFormsJson || '{}');
    } catch {
      message.error('标签、例句或词形配置不是有效 JSON');
      return;
    }
    modalApi.lock();
    try {
      await (formData.value?.id
        ? updateReadingMaterial(data)
        : createReadingMaterial(data));
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(open: boolean) {
    if (!open) {
      formData.value = undefined;
      return;
    }
    const row = modalApi.getData<EnglishReadingMaterialApi.Material>();
    if (!row?.id) {
      await formApi.setValues({
        levelCode: 'ket',
        materialType: 'word',
        partOfSpeech: 'unknown',
        tagsJson: '[]',
        examplesJson: '[]',
        wordFormsJson: '{}',
        mustKnow: 0,
        highFrequency: 0,
        mustSpell: 0,
        sort: 0,
        status: 1,
      });
      return;
    }
    modalApi.lock();
    try {
      formData.value = await getReadingMaterial(row.id);
      await formApi.setValues({
        ...formData.value,
        tagsJson: prettyJson(formData.value.tagsJson, '[]'),
        examplesJson: prettyJson(formData.value.examplesJson, '[]'),
        wordFormsJson: prettyJson(formData.value.wordFormsJson, '{}'),
      });
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="title" class="w-[860px]">
    <div class="mx-4 mb-4 rounded border border-dashed border-gray-200 p-3">
      <div class="mb-2 flex items-center justify-between">
        <strong>前端 AI 完善</strong>
        <Button type="primary" :loading="aiLoading" @click="completeWithAi">
          AI 完善
        </Button>
      </div>
      <div class="grid grid-cols-3 gap-2">
        <Input
          v-model:value="aiBaseUrl"
          placeholder="Base URL，如 https://api.deepseek.com"
        />
        <Input.Password
          v-model:value="aiApiKey"
          placeholder="API Key，仅保存在本浏览器"
        />
        <Input
          v-model:value="aiModel"
          placeholder="模型，如 deepseek-v4-flash"
        />
      </div>
    </div>
    <Form class="mx-4" />
  </Modal>
</template>
