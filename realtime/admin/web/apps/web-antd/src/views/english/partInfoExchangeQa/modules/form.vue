<script lang="ts" setup>
import type { EnglishPartInfoExchangeQaApi } from '#/api/english/partInfoExchangeQa';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createPartInfoExchangeQa, getPartInfoExchangeQa, updatePartInfoExchangeQa } from '#/api/english/partInfoExchangeQa';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<EnglishPartInfoExchangeQaApi.PartInfoExchangeQa>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['问答条目'])
    : $t('ui.actionTitle.create', ['问答条目']);
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
    if (!valid) {
      return;
    }
    modalApi.lock();
    const data = (await formApi.getValues()) as EnglishPartInfoExchangeQaApi.PartInfoExchangeQa;
    try {
      await (formData.value?.id ? updatePartInfoExchangeQa(data) : createPartInfoExchangeQa(data));
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
    const data = modalApi.getData<EnglishPartInfoExchangeQaApi.PartInfoExchangeQa>();
    if (!data || !data.id) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = await getPartInfoExchangeQa(data.id);
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
