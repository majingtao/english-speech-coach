<script lang="ts" setup>
import type { EnglishSynonymApi } from '#/api/english/synonym';
import { computed, ref } from 'vue';
import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import { useVbenForm } from '#/adapter/form';
import { createSynonymPoint, getSynonymPoint, updateSynonymPoint } from '#/api/english/synonym';

const emit = defineEmits(['success']);
const editing = ref<EnglishSynonymApi.Point>();

const [Form, formApi] = useVbenForm({
  commonConfig: { componentProps: { class: 'w-full' }, formItemClass: 'col-span-2', labelWidth: 110 },
  showDefaultActions: false,
  schema: [
    { fieldName: 'id', component: 'Input', dependencies: { triggerFields: [''], show: () => false } },
    { fieldName: 'code', label: '编码', rules: 'required', component: 'Input' },
    { fieldName: 'mode', label: '类型', rules: 'required', component: 'RadioGroup', defaultValue: 'synonym', componentProps: { options: [{ label: '同义词', value: 'synonym' }, { label: '反义词', value: 'antonym' }] } },
    { fieldName: 'levelCode', label: '级别', rules: 'required', component: 'Input', defaultValue: 'ket' },
    { fieldName: 'source', label: '来源', rules: 'required', component: 'Input' },
    { fieldName: 'sectionName', label: '章节', rules: 'required', component: 'Input' },
    { fieldName: 'leftText', label: '题干', rules: 'required', component: 'Textarea', componentProps: { rows: 2 } },
    { fieldName: 'leftCn', label: '题干中文', component: 'Textarea', componentProps: { rows: 2 } },
    { fieldName: 'rightText', label: '正确答案', rules: 'required', component: 'Textarea', componentProps: { rows: 2 } },
    { fieldName: 'rightCn', label: '答案中文', component: 'Textarea', componentProps: { rows: 2 } },
    { fieldName: 'sort', label: '排序', component: 'InputNumber', defaultValue: 0 },
    { fieldName: 'status', label: '状态', rules: 'required', component: 'RadioGroup', defaultValue: 1, componentProps: { options: [{ label: '草稿', value: 0 }, { label: '发布', value: 1 }, { label: '停用', value: 2 }] } },
  ],
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) return;
    const data = await formApi.getValues() as EnglishSynonymApi.Point;
    modalApi.lock();
    try {
      await (editing.value?.id ? updateSynonymPoint(data) : createSynonymPoint(data));
      await modalApi.close();
      emit('success');
      message.success('保存成功');
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(open) {
    if (!open) { editing.value = undefined; return; }
    const row = modalApi.getData<EnglishSynonymApi.Point>();
    if (!row?.id) {
      await formApi.resetForm();
      await formApi.setValues({ code: `synonym-${Date.now()}`, mode: 'synonym', levelCode: 'ket', source: 'manual', sectionName: 'General', sort: 0, status: 1 });
      return;
    }
    modalApi.lock();
    try {
      editing.value = await getSynonymPoint(row.id);
      await formApi.setValues(editing.value);
    } finally {
      modalApi.unlock();
    }
  },
});

const title = computed(() => editing.value?.id ? '编辑同义替换' : '新增同义替换');
</script>

<template>
  <Modal :title="title" class="w-[760px]">
    <Form class="mx-4" />
  </Modal>
</template>
