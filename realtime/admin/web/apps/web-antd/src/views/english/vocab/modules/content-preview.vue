<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, message, Spin } from 'ant-design-vue';

import { getVocab, regenVocabContent } from '#/api/english/vocab';

const emit = defineEmits(['success']);
const vocabId = ref<number>();
const word = ref('');
const jsonText = ref('');
const loading = ref(false);
const regenerating = ref(false);

async function loadContent(id: number) {
  loading.value = true;
  try {
    const vocab = await getVocab(id);
    word.value = vocab.word || '';
    jsonText.value = vocab.contentJson
      ? JSON.stringify(JSON.parse(vocab.contentJson), null, 2)
      : '';
  } catch {
    jsonText.value = '';
  } finally {
    loading.value = false;
  }
}

async function handleRegen() {
  if (!vocabId.value) return;
  regenerating.value = true;
  try {
    await regenVocabContent(vocabId.value);
    message.success('已重新生成');
    await loadContent(vocabId.value);
    emit('success');
  } catch (e: any) {
    message.error(`生成失败: ${e?.message || '未知错误'}`);
  } finally {
    regenerating.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  showConfirmButton: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      vocabId.value = undefined;
      word.value = '';
      jsonText.value = '';
      return;
    }
    const data = modalApi.getData<{ id: number }>();
    if (!data?.id) return;
    vocabId.value = data.id;
    await loadContent(data.id);
  },
});
</script>

<template>
  <Modal :title="`查看词条内容 - ${word}`" :fullscreen-button="true" class="w-[800px]">
    <Spin :spinning="loading">
      <div class="mb-2 flex items-center justify-between">
        <span class="text-sm text-gray-500">LLM + IPA 缓存内容（JSON）</span>
        <Button
          type="primary"
          size="small"
          :loading="regenerating"
          @click="handleRegen"
        >
          {{ jsonText ? '重新生成' : '生成内容' }}
        </Button>
      </div>
      <Alert
        v-if="!jsonText && !loading"
        message="该词条还未生成内容。点击上方按钮可调用 LLM 生成释义/音标/例句。"
        type="info"
        class="mb-2"
        show-icon
      />
      <textarea
        v-model="jsonText"
        readonly
        class="w-full rounded border border-gray-200 bg-gray-50 p-3 font-mono text-sm"
        :style="{ minHeight: '420px', resize: 'vertical' }"
        spellcheck="false"
      />
    </Spin>
  </Modal>
</template>
