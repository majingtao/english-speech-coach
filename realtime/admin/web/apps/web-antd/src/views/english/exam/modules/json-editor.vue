<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, message, Spin } from 'ant-design-vue';

import { getExamContent, updateExamContent } from '#/api/english/exam';

const emit = defineEmits(['success']);
const examId = ref<number>();
const examCode = ref('');
const jsonText = ref('');
const loading = ref(false);
const jsonError = ref('');

function validateJson(text: string): boolean {
  try {
    JSON.parse(text);
    jsonError.value = '';
    return true;
  } catch (e: any) {
    jsonError.value = e.message;
    return false;
  }
}

function handleInput(e: Event) {
  const target = e.target as HTMLTextAreaElement;
  jsonText.value = target.value;
  if (target.value.trim()) {
    validateJson(target.value);
  } else {
    jsonError.value = '';
  }
}

function handleFormat() {
  if (!jsonText.value.trim()) return;
  try {
    const parsed = JSON.parse(jsonText.value);
    jsonText.value = JSON.stringify(parsed, null, 2);
    jsonError.value = '';
  } catch (e: any) {
    jsonError.value = e.message;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    if (!examId.value) return;
    if (!jsonText.value.trim()) {
      message.warning('JSON 内容不能为空');
      return;
    }
    if (!validateJson(jsonText.value)) {
      message.error('JSON 格式错误，请修正后再保存');
      return;
    }
    modalApi.lock();
    try {
      await updateExamContent(examId.value, jsonText.value);
      await modalApi.close();
      emit('success');
      message.success('JSON 保存成功');
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      examId.value = undefined;
      examCode.value = '';
      jsonText.value = '';
      jsonError.value = '';
      return;
    }
    const data = modalApi.getData<{ id: number; examCode: string }>();
    if (!data || !data.id) return;
    examId.value = data.id;
    examCode.value = data.examCode || '';
    loading.value = true;
    try {
      const exam = await getExamContent(data.id);
      jsonText.value = exam.contentJson
        ? JSON.stringify(JSON.parse(exam.contentJson), null, 2)
        : '';
    } catch {
      jsonText.value = '';
    } finally {
      loading.value = false;
    }
  },
});
</script>

<template>
  <Modal :title="`编辑 JSON - ${examCode}`" :fullscreen-button="true" class="w-[900px]">
    <Spin :spinning="loading">
      <div class="mb-2 flex items-center justify-between">
        <span class="text-sm text-gray-500">
          试卷 JSON 内容（H5 题库格式）
        </span>
        <a-button size="small" @click="handleFormat">格式化</a-button>
      </div>
      <textarea
        :value="jsonText"
        class="w-full rounded border border-gray-300 p-3 font-mono text-sm"
        :style="{ minHeight: '500px', resize: 'vertical' }"
        spellcheck="false"
        @input="handleInput"
      />
      <Alert
        v-if="jsonError"
        :message="'JSON 格式错误: ' + jsonError"
        type="error"
        class="mt-2"
        show-icon
      />
      <Alert
        v-else-if="jsonText.trim() && !jsonError"
        message="JSON 格式正确"
        type="success"
        class="mt-2"
        show-icon
      />
    </Spin>
  </Modal>
</template>
