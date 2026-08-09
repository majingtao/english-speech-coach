<script lang="ts" setup>
import type { EnglishExpressionApi } from '#/api/english/expression';

import { reactive, ref } from 'vue';
import { useVbenModal } from '@vben/common-ui';
import { Button, Form, FormItem, Input, InputNumber, message, Popconfirm, Select, Switch, Table } from 'ant-design-vue';

import {
  createExpressionTheme,
  deleteExpressionTheme,
  getExpressionThemes,
  updateExpressionTheme,
} from '#/api/english/expression';

const emit = defineEmits(['success']);
const themes = ref<EnglishExpressionApi.Theme[]>([]);
const saving = ref(false);
const draft = reactive<EnglishExpressionApi.Theme>({
  code: '', nameCn: '', nameEn: '', description: '', levelCode: 'ket', sort: 0, status: 1,
});
const columns = [
  { title: '编码', dataIndex: 'code', width: 150 },
  { title: '中文名', dataIndex: 'nameCn', width: 130 },
  { title: '英文名', dataIndex: 'nameEn' },
  { title: '状态', dataIndex: 'status', width: 70 },
  { title: '操作', key: 'actions', width: 130 },
];

async function load() { themes.value = await getExpressionThemes('ket'); }
function reset() { Object.assign(draft, { id: undefined, code: '', nameCn: '', nameEn: '', description: '', levelCode: 'ket', sort: 0, status: 1 }); }
function edit(row: EnglishExpressionApi.Theme) { Object.assign(draft, row); }

async function save() {
  if (!draft.code?.trim() || !draft.nameCn?.trim() || !draft.nameEn?.trim()) {
    message.warning('编码、中文名和英文名不能为空');
    return;
  }
  saving.value = true;
  try {
    await (draft.id ? updateExpressionTheme({ ...draft }) : createExpressionTheme({ ...draft }));
    message.success('主题已保存');
    reset();
    await load();
    emit('success');
  } finally { saving.value = false; }
}

async function remove(id: number) {
  await deleteExpressionTheme(id);
  message.success('主题已删除');
  await load();
  emit('success');
}

const [Modal] = useVbenModal({
  showConfirmButton: false,
  async onOpenChange(open: boolean) { if (open) { reset(); await load(); } },
});
</script>

<template>
  <Modal title="表达主题管理" class="w-[900px]">
    <Form layout="vertical" class="px-4">
      <div class="grid grid-cols-3 gap-x-3">
        <FormItem label="主题编码" required><Input v-model:value="draft.code" :disabled="!!draft.id" placeholder="food-drink" /></FormItem>
        <FormItem label="中文名" required><Input v-model:value="draft.nameCn" /></FormItem>
        <FormItem label="英文名" required><Input v-model:value="draft.nameEn" /></FormItem>
        <FormItem label="级别"><Select v-model:value="draft.levelCode" :options="[{ label: 'KET', value: 'ket' }]" /></FormItem>
        <FormItem label="排序"><InputNumber v-model:value="draft.sort" :min="0" class="w-full" /></FormItem>
        <FormItem label="启用"><Switch v-model:checked="draft.status" :checked-value="1" :un-checked-value="0" /></FormItem>
      </div>
      <FormItem label="主题说明"><Input v-model:value="draft.description" /></FormItem>
      <div class="mb-4 flex gap-2">
        <Button type="primary" :loading="saving" @click="save">{{ draft.id ? '保存修改' : '新增主题' }}</Button>
        <Button v-if="draft.id" @click="reset">取消编辑</Button>
      </div>
      <Table :columns="columns" :data-source="themes" row-key="id" size="small" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">{{ record.status === 1 ? '启用' : '停用' }}</template>
          <template v-else-if="column.key === 'actions'">
            <Button type="link" size="small" @click="edit(record)">编辑</Button>
            <Popconfirm title="仅空主题可以删除，确认继续？" @confirm="remove(record.id)">
              <Button type="link" danger size="small">删除</Button>
            </Popconfirm>
          </template>
        </template>
      </Table>
    </Form>
  </Modal>
</template>
