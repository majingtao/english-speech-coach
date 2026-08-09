<script lang="ts" setup>
import type { EnglishExpressionApi } from '#/api/english/expression';

import { computed, ref } from 'vue';
import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createExpressionItem,
  getExpressionItem,
  updateExpressionItem,
} from '#/api/english/expression';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<EnglishExpressionApi.Item>();
const title = computed(() => formData.value?.id ? '编辑表达练习' : '新增表达练习');

function prettyJson(value?: string) {
  if (!value) return value;
  try { return JSON.stringify(JSON.parse(value), null, 2); } catch { return value; }
}

const [Form, formApi] = useVbenForm({
  commonConfig: { componentProps: { class: 'w-full' }, formItemClass: 'col-span-2', labelWidth: 110 },
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) return;
    const data = (await formApi.getValues()) as EnglishExpressionApi.Item;
    try {
      JSON.parse(data.answerJson || '');
      if (data.imageUrlsJson) JSON.parse(data.imageUrlsJson);
    } catch {
      message.error('答案或图片配置不是有效 JSON');
      return;
    }
    modalApi.lock();
    try {
      await (formData.value?.id ? updateExpressionItem(data) : createExpressionItem(data));
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally { modalApi.unlock(); }
  },
  async onOpenChange(open: boolean) {
    if (!open) { formData.value = undefined; return; }
    const row = modalApi.getData<EnglishExpressionApi.Item>();
    if (!row?.id) return;
    modalApi.lock();
    try {
      formData.value = await getExpressionItem(row.id);
      await formApi.setValues({
        ...formData.value,
        answerJson: prettyJson(formData.value.answerJson),
        imageUrlsJson: prettyJson(formData.value.imageUrlsJson),
      });
    } finally { modalApi.unlock(); }
  },
});
</script>

<template><Modal :title="title"><Form class="mx-4" /></Modal></template>
