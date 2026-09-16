<script lang="ts" setup>
import type { PersonalPracticeApi } from '#/api/english/personalPractice';

import { computed, reactive, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Button,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  RadioButton,
  RadioGroup,
  Select,
  SelectOption,
  Space,
} from 'ant-design-vue';

import {
  createPersonalPractice,
  getPersonalPractice,
  updatePersonalPractice,
} from '#/api/english/personalPractice';

type ReferenceLine = PersonalPracticeApi.ReferenceLine;

const emit = defineEmits(['success']);
const editingId = ref<number>();
const formState = reactive({
  practiceType: 'speaking' as PersonalPracticeApi.PracticeType,
  title: '',
  promptEn: '',
  promptCn: '',
  references: [{ en: '', cn: '' }] as ReferenceLine[],
  contentPoints: [''] as string[],
  minSentences: 2,
  minWords: 0,
  sort: 0,
  status: 1,
});
const title = computed(() =>
  editingId.value ? '编辑专属练习' : '新增专属练习',
);

function reset() {
  editingId.value = undefined;
  Object.assign(formState, {
    practiceType: 'speaking',
    title: '',
    promptEn: '',
    promptCn: '',
    references: [{ en: '', cn: '' }],
    contentPoints: [''],
    minSentences: 2,
    minWords: 0,
    sort: 0,
    status: 1,
  });
}

function parseArray<T>(value: string | undefined, fallback: T[]): T[] {
  try {
    const parsed = JSON.parse(value || '[]');
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : fallback;
  } catch {
    return fallback;
  }
}

function addReference() {
  formState.references.push({ en: '', cn: '' });
}

function addContentPoint() {
  formState.contentPoints.push('');
}

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const references = formState.references.filter((item) => item.en.trim());
    const contentPoints = formState.contentPoints
      .map((item) => item.trim())
      .filter(Boolean);
    if (!formState.title.trim() || !formState.promptEn.trim()) {
      message.error('请填写标题和英文题目');
      return;
    }
    if (references.length === 0) {
      message.error('请至少填写一句英文参考句');
      return;
    }
    const payload: PersonalPracticeApi.Practice = {
      id: editingId.value,
      practiceType: formState.practiceType,
      title: formState.title.trim(),
      promptEn: formState.promptEn.trim(),
      promptCn: formState.promptCn.trim(),
      referenceJson: JSON.stringify(references),
      contentPointsJson: JSON.stringify(contentPoints),
      minSentences: formState.minSentences,
      minWords: formState.practiceType === 'writing' ? formState.minWords : 0,
      sort: formState.sort,
      status: formState.status,
    };
    modalApi.lock();
    try {
      await (editingId.value
        ? updatePersonalPractice(payload)
        : createPersonalPractice(payload));
      await modalApi.close();
      emit('success');
      message.success('保存成功');
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(open: boolean) {
    if (!open) {
      reset();
      return;
    }
    const row = modalApi.getData<PersonalPracticeApi.Practice>();
    if (!row?.id) return;
    modalApi.lock();
    try {
      const data = await getPersonalPractice(row.id);
      editingId.value = data.id;
      Object.assign(formState, {
        practiceType: data.practiceType,
        title: data.title,
        promptEn: data.promptEn,
        promptCn: data.promptCn || '',
        references: parseArray<ReferenceLine>(data.referenceJson, [
          { en: '', cn: '' },
        ]),
        contentPoints: parseArray<string>(data.contentPointsJson, ['']),
        minSentences: data.minSentences || 1,
        minWords: data.minWords || 0,
        sort: data.sort || 0,
        status: data.status ?? 1,
      });
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="title" class="w-[760px]">
    <Form :model="formState" layout="vertical" class="px-4">
      <div class="grid grid-cols-2 gap-x-4">
        <FormItem label="练习类型" required>
          <RadioGroup
            v-model:value="formState.practiceType"
            button-style="solid"
          >
            <RadioButton value="speaking">专属口语</RadioButton>
            <RadioButton value="writing">专属写作</RadioButton>
          </RadioGroup>
        </FormItem>
        <FormItem label="标题" required>
          <Input v-model:value="formState.title" />
        </FormItem>
      </div>
      <FormItem label="英文题目" required>
        <Input.TextArea v-model:value="formState.promptEn" :rows="2" />
      </FormItem>
      <FormItem label="中文释义">
        <Input.TextArea v-model:value="formState.promptCn" :rows="2" />
      </FormItem>

      <FormItem label="参考句" required>
        <Space
          v-for="(line, index) in formState.references"
          :key="index"
          class="mb-2 flex"
          align="start"
        >
          <Input
            v-model:value="line.en"
            class="w-[330px]"
            :placeholder="`英文第 ${index + 1} 句`"
          />
          <Input
            v-model:value="line.cn"
            class="w-[250px]"
            placeholder="中文释义"
          />
          <Button
            type="text"
            danger
            :disabled="formState.references.length === 1"
            @click="formState.references.splice(index, 1)"
          >
            <span aria-hidden="true">×</span>
          </Button>
        </Space>
        <Button type="dashed" block @click="addReference">
          <span aria-hidden="true">+</span>新增参考句
        </Button>
      </FormItem>

      <FormItem label="KET 内容要点">
        <Space
          v-for="(_, index) in formState.contentPoints"
          :key="index"
          class="mb-2 flex"
        >
          <Input
            v-model:value="formState.contentPoints[index]"
            class="w-[590px]"
            placeholder="例如：说明放学后做什么，并给出一个原因"
          />
          <Button
            type="text"
            danger
            :disabled="formState.contentPoints.length === 1"
            @click="formState.contentPoints.splice(index, 1)"
          >
            <span aria-hidden="true">×</span>
          </Button>
        </Space>
        <Button type="dashed" block @click="addContentPoint">
          <span aria-hidden="true">+</span>新增内容要点
        </Button>
      </FormItem>

      <div class="grid grid-cols-4 gap-x-4">
        <FormItem label="最低句数">
          <InputNumber
            v-model:value="formState.minSentences"
            :min="1"
            :max="10"
          />
        </FormItem>
        <FormItem v-if="formState.practiceType === 'writing'" label="最低词数">
          <InputNumber v-model:value="formState.minWords" :min="0" :max="200" />
        </FormItem>
        <FormItem label="排序">
          <InputNumber v-model:value="formState.sort" :min="0" />
        </FormItem>
        <FormItem label="状态">
          <Select v-model:value="formState.status">
            <SelectOption :value="0">草稿</SelectOption>
            <SelectOption :value="1">发布</SelectOption>
            <SelectOption :value="2">归档</SelectOption>
          </Select>
        </FormItem>
      </div>
    </Form>
  </Modal>
</template>
