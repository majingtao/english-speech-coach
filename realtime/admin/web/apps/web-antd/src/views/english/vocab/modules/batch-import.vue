<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, message, Spin } from 'ant-design-vue';

import { batchImportVocab } from '#/api/english/vocab';

const emit = defineEmits(['success']);
const levelCode = ref('ket');
const jsonText = ref('');
const parseError = ref('');
const importing = ref(false);

const SAMPLE = `[
  {
    "word": "decide",
    "pos": "verb",
    "difficulty": 1,
    "mastery_level": 2,
    "cefr": "A2",
    "cefr_list": ["A2"],
    "themes": ["personal-feelings"],
    "status": 1,
    "content": {
      "definition_cn": "决定，作出选择",
      "definition_en": "to choose what to do after thinking",
      "ipa": "/dɪˈsaɪd/",
      "forms": {
        "base": "decide",
        "past": "decided",
        "past_participle": "decided",
        "ing": "deciding",
        "third_person": "decides"
      },
      "examples": [
        { "en": "I decide what to eat for lunch.", "cn": "我决定午餐吃什么。" },
        { "en": "She decided to learn English.",   "cn": "她决定学英语。" }
      ]
    }
  },
  {
    "word": "apple",
    "pos": "noun",
    "difficulty": 1,
    "mastery_level": 2,
    "themes": ["food-drink"],
    "content": {
      "definition_cn": "苹果",
      "definition_en": "a round fruit with red or green skin",
      "ipa": "/ˈæp.əl/",
      "forms": { "singular": "apple", "plural": "apples" },
      "examples": [
        { "en": "I eat an apple every day.", "cn": "我每天吃一个苹果。" },
        { "en": "These apples are very sweet.", "cn": "这些苹果很甜。" }
      ]
    }
  }
]`;

function validateJson(): any[] | null {
  try {
    const parsed = JSON.parse(jsonText.value);
    if (!Array.isArray(parsed)) {
      parseError.value = '顶层必须是数组';
      return null;
    }
    parseError.value = '';
    return parsed;
  } catch (e: any) {
    parseError.value = e.message;
    return null;
  }
}

async function handleImport() {
  if (!levelCode.value) {
    message.warning('请先选择级别');
    return;
  }
  const items = validateJson();
  if (!items) return;
  importing.value = true;
  try {
    const count = await batchImportVocab({ levelCode: levelCode.value, items });
    message.success(`成功导入 ${count} 条`);
    emit('success');
    await modalApi.close();
  } finally {
    importing.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    await handleImport();
  },
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      jsonText.value = '';
      parseError.value = '';
    }
  },
});
</script>

<template>
  <Modal title="批量导入词条（JSON 粘贴）" class="w-[720px]">
    <Spin :spinning="importing">
      <div class="mb-3">
        <label class="mb-1 block text-sm">批次默认级别</label>
        <a-select
          v-model:value="levelCode"
          style="width: 200px"
          :options="[
            { value: 'ket', label: 'KET' },
            { value: 'flyers', label: 'Flyers' },
            { value: 'pet', label: 'PET' },
          ]"
        />
        <span class="ml-2 text-xs text-gray-400">
          item 自带 "level" 字段时该项以 item 为准（支持混级别导入）
        </span>
      </div>
      <div class="mb-2 flex items-center justify-between">
        <span class="text-sm text-gray-500">
          JSON 数组（每项 word 必填；level / pos / difficulty / themes 可选）
        </span>
        <a-button size="small" @click="jsonText = SAMPLE">填入示例</a-button>
      </div>
      <textarea
        v-model="jsonText"
        class="w-full rounded border border-gray-300 p-3 font-mono text-sm"
        :style="{ minHeight: '320px', resize: 'vertical' }"
        spellcheck="false"
        placeholder="粘贴 JSON 数组……"
      />
      <Alert
        v-if="parseError"
        :message="'JSON 错误: ' + parseError"
        type="error"
        class="mt-2"
        show-icon
      />
      <Alert
        v-else
        message="重复单词（同级别）会自动跳过；themes 按 code 匹配，未知主题忽略；item.level 覆盖批次默认级别。"
        type="info"
        class="mt-2"
        show-icon
      />
    </Spin>
  </Modal>
</template>
