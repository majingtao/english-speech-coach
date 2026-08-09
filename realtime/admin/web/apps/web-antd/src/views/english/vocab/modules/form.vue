<script lang="ts" setup>
import type { EnglishVocabApi } from '#/api/english/vocab';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createVocab, getVocab, updateVocab } from '#/api/english/vocab';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<EnglishVocabApi.Vocab>();

function prettifyJson(value?: string): string | undefined {
  if (!value) return value;
  try {
    return JSON.stringify(JSON.parse(value), null, 2);
  } catch {
    return value;
  }
}
const getTitle = computed(() =>
  formData.value?.id
    ? $t('ui.actionTitle.edit', ['词条'])
    : $t('ui.actionTitle.create', ['词条']),
);

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: { class: 'w-full' },
    formItemClass: 'col-span-2',
    labelWidth: 80,
  },
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) return;
    modalApi.lock();
    const data = (await formApi.getValues()) as EnglishVocabApi.Vocab;
    try {
      await (formData.value?.id ? updateVocab(data) : createVocab(data));
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      return;
    }
    const data = modalApi.getData<EnglishVocabApi.Vocab>();
    if (!data || !data.id) return;
    modalApi.lock();
    try {
      formData.value = await getVocab(data.id);
      // 把紧凑 JSON 美化展示（编辑后回交时再交给后端原样存储）
      const display: EnglishVocabApi.Vocab = { ...formData.value };
      display.contentJson = prettifyJson(display.contentJson);
      display.formsJson = prettifyJson(display.formsJson);
      await formApi.setValues(display);
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="getTitle">
    <Form class="mx-4" />
  </Modal>
</template>
