<script lang="ts" setup>
import type { EnglishQuotaApi } from '#/api/english/quota';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { getUserQuota, saveUserQuota } from '#/api/english/quota';
import { $t } from '#/locales';

import { useUserFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<EnglishQuotaApi.UserQuota>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['用户配额'])
    : $t('ui.actionTitle.create', ['用户配额']);
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-2',
    labelWidth: 110,
  },
  layout: 'horizontal',
  schema: useUserFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    const data = (await formApi.getValues()) as EnglishQuotaApi.UserQuota;
    try {
      await saveUserQuota(data);
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
    const data = modalApi.getData<EnglishQuotaApi.UserQuota>();
    if (!data) {
      return;
    }
    if (!data.id && data.userId) {
      modalApi.lock();
      try {
        const fetched = await getUserQuota(data.userId);
        if (fetched) {
          formData.value = fetched;
          await formApi.setValues(fetched);
        } else {
          formData.value = { ...data };
          await formApi.setValues(data);
        }
      } finally {
        modalApi.unlock();
      }
      return;
    }
    if (!data.id) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = await getUserQuota(data.userId);
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
