<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { showToast } from 'vant'

interface GradeOption {
  key: string
  label: string
  editions: string[]
}

const router = useRouter()

const grades: GradeOption[] = [
  { key: 'grade1', label: '一年级', editions: ['人教版', 'PEP版', '北京版'] },
  { key: 'grade2', label: '二年级', editions: ['人教版', 'PEP版', '北京版'] },
  { key: 'grade3', label: '三年级', editions: ['人教版', 'PEP版', '北京版', '外研版'] },
  { key: 'grade4', label: '四年级', editions: ['人教版', 'PEP版', '北京版', '外研版'] },
  { key: 'grade5', label: '五年级', editions: ['人教版', 'PEP版', '北京版', '外研版'] },
  { key: 'grade6', label: '六年级', editions: ['人教版', 'PEP版', '北京版', '外研版'] },
]

const activeGradeIndex = ref(0)
const selectedEditionByGrade = reactive<Record<string, string>>({})
const selectedUnitByGrade = reactive<Record<string, string>>({})
const unitOptions = ['1单元', '2单元', '3单元', '4单元', '5单元', '全部单元']
const currentGrade = computed(() => grades[activeGradeIndex.value])
const currentSelectedEdition = computed(() => selectedEditionByGrade[currentGrade.value.key] || '')
const currentSelectedUnit = computed(() => selectedUnitByGrade[currentGrade.value.key] || '')
const canStart = computed(() => !!currentSelectedEdition.value && !!currentSelectedUnit.value)

function goHome() {
  router.push('/')
}

function chooseEdition(edition: string) {
  if (selectedEditionByGrade[currentGrade.value.key] !== edition)
    selectedUnitByGrade[currentGrade.value.key] = ''
  selectedEditionByGrade[currentGrade.value.key] = edition
}

function chooseUnit(unit: string) {
  if (!currentSelectedEdition.value)
    return
  selectedUnitByGrade[currentGrade.value.key] = unit
}

function startDictation() {
  if (!canStart.value) {
    showToast('请先选择教材版本和单元')
    return
  }
  showToast(`${currentGrade.value.label} ${currentSelectedEdition.value} ${currentSelectedUnit.value} 听写页面开发中`)
}
</script>

<template>
  <div class="dictation-page">
    <van-nav-bar
      title="听写 - 小学"
      left-text="首页"
      left-arrow
      @click-left="goHome"
    />

    <div class="selection-panel">
      <aside class="sidebar">
        <van-sidebar v-model="activeGradeIndex">
          <van-sidebar-item v-for="grade in grades" :key="grade.key" :title="grade.label" />
        </van-sidebar>
      </aside>

      <section class="content">
        <h2 class="content-title">
          {{ currentGrade.label }} - 教材版本
        </h2>
        <div class="edition-grid">
          <button
            v-for="edition in currentGrade.editions"
            :key="edition"
            class="edition-item"
            :class="{ active: currentSelectedEdition === edition }"
            type="button"
            @click="chooseEdition(edition)"
          >
            {{ edition }}
          </button>
        </div>

        <h2 class="content-title unit-title">
          {{ currentGrade.label }} - 单元
        </h2>
        <div class="unit-grid" :class="{ disabled: !currentSelectedEdition }">
          <button
            v-for="unit in unitOptions"
            :key="unit"
            class="unit-item"
            :class="{ active: currentSelectedUnit === unit }"
            type="button"
            :disabled="!currentSelectedEdition"
            @click="chooseUnit(unit)"
          >
            {{ unit }}
          </button>
        </div>

        <div class="footer-actions">
          <van-button block type="primary" :disabled="!canStart" @click="startDictation">
            开始听写
          </van-button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.dictation-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #f6f8fc;
}

.selection-panel {
  margin-top: 10px;
  display: flex;
  min-height: calc(100dvh - 56px - 10px);
}

.sidebar {
  width: 78px;
  border-right: 1px solid #e8edf5;
  background: #fff;
}

.content {
  flex: 1;
  padding: 14px 12px 18px;
}

.content-title {
  margin: 0 0 12px;
  color: #213b63;
  font-size: 16px;
}

.unit-title {
  margin-top: 16px;
}

.edition-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.edition-item {
  appearance: none;
  border: 1px solid #dbe5f5;
  border-radius: 12px;
  background: #fff;
  color: #35517c;
  font-size: 14px;
  min-height: 42px;
}

.edition-item.active {
  border-color: #2f6fcd;
  color: #2f6fcd;
  background: #eef4ff;
}

.unit-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.unit-grid.disabled {
  opacity: 0.6;
}

.unit-item {
  appearance: none;
  border: 1px solid #dbe5f5;
  border-radius: 12px;
  background: #fff;
  color: #35517c;
  font-size: 14px;
  min-height: 42px;
}

.unit-item:disabled {
  background: #f2f5fb;
  color: #9ca8bb;
}

.unit-item.active {
  border-color: #2f6fcd;
  color: #2f6fcd;
  background: #eef4ff;
}

.footer-actions {
  margin-top: 18px;
}
</style>
