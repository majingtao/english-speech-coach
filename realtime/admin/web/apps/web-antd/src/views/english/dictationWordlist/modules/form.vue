<script lang="ts" setup>
import type { DictationWordlistApi } from '#/api/english/dictation/wordlist';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createDictationWordlist, getDictationWordlist, updateDictationWordlist } from '#/api/english/dictation/wordlist';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<DictationWordlistApi.Wordlist>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['词书'])
    : $t('ui.actionTitle.create', ['词书']);
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
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
    const data = (await formApi.getValues()) as DictationWordlistApi.Wordlist;
    try {
      await (formData.value?.id ? updateDictationWordlist(data) : createDictationWordlist(data));
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
    const data = modalApi.getData<DictationWordlistApi.Wordlist>();
    if (!data || !data.id) return;
    modalApi.lock();
    try {
      formData.value = await getDictationWordlist(data.id);
      await formApi.setValues(formData.value);
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
