<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { showToast } from 'vant'
import avatarUrl from '@/assets/images/default-avatar.svg'
import type { QuotaMe } from '@/api/quota'
import { fetchMyQuota } from '@/api/quota'
import { isLogin } from '@/utils/auth'

interface AppItem {
  key: string
  label: string
  icon: string
  enabled: boolean
  to?: string
}

interface AppSection {
  key: string
  title: string
  help: string
  items: AppItem[]
}

const sections: AppSection[] = [
  {
    key: 'speaking',
    title: '口语考试练习',
    help: 'YLE（剑桥少儿）官方等级：Pre A1 Starters / A1 Movers / A2 Flyers',
    items: [
      {
        key: 'speaking-yle',
        label: 'YLE（剑桥少儿）',
        icon: 'comment-circle-o',
        enabled: true,
        to: '/speech-yle',
      },
      { key: 'speaking-ket', label: 'KET', icon: 'comment-circle-o', enabled: false },
      { key: 'speaking-pet', label: 'PET', icon: 'comment-circle-o', enabled: false },
      { key: 'speaking-fce', label: 'FCE', icon: 'comment-circle-o', enabled: false },
      { key: 'speaking-ielts', label: 'IELTS 雅思', icon: 'comment-circle-o', enabled: false },
      { key: 'speaking-toefl', label: 'TOEFL 托福', icon: 'comment-circle-o', enabled: false },
    ],
  },
  {
    key: 'speaking-practice',
    title: '口语练习',
    help: '按级别进行模拟考试对话',
    items: [
      { key: 'practice-primary', label: '小学', icon: 'volume-o', enabled: false },
      { key: 'practice-junior', label: '初中', icon: 'volume-o', enabled: false },
      { key: 'practice-senior', label: '高中', icon: 'volume-o', enabled: false },
      { key: 'practice-flyers', label: 'Flyers', icon: 'volume-o', enabled: false },
      { key: 'practice-ket', label: 'KET', icon: 'volume-o', enabled: false },
      { key: 'practice-pet', label: 'PET', icon: 'volume-o', enabled: false },
      { key: 'practice-fce', label: 'FCE', icon: 'volume-o', enabled: false },
      { key: 'practice-ielts', label: 'IELTS 雅思', icon: 'volume-o', enabled: false },
      { key: 'practice-toefl', label: 'TOEFL 托福', icon: 'volume-o', enabled: false },
    ],
  },
  {
    key: 'free-chat',
    title: '自由对话',
    help: '按级别自由对话',
    items: [
      { key: 'chat-flyers', label: 'Flyers', icon: 'chat-o', enabled: false },
      { key: 'chat-ket', label: 'KET', icon: 'chat-o', enabled: false },
      { key: 'chat-pet', label: 'PET', icon: 'chat-o', enabled: false },
      { key: 'chat-fce', label: 'FCE', icon: 'chat-o', enabled: false },
      { key: 'chat-ielts', label: 'IELTS 雅思', icon: 'chat-o', enabled: false },
      { key: 'chat-toefl', label: 'TOEFL 托福', icon: 'chat-o', enabled: false },
    ],
  },
  {
    key: 'dictation',
    title: '听写',
    help: '按教材进行听写或自定义听写题库',
    items: [
      { key: 'dictation-primary', label: '小学', icon: 'edit', enabled: true, to: '/dictation/primary' },
      { key: 'dictation-junior', label: '初中', icon: 'edit', enabled: false },
      { key: 'dictation-senior', label: '高中', icon: 'edit', enabled: false },
      { key: 'dictation-flyers', label: 'Flyers', icon: 'edit', enabled: false },
      { key: 'dictation-ket', label: 'KET', icon: 'edit', enabled: false },
      { key: 'dictation-pet', label: 'PET', icon: 'edit', enabled: false },
      { key: 'dictation-fce', label: 'FCE', icon: 'edit', enabled: false },
    ],
  },
]

const menuVisible = ref(false)
const quotaPopupVisible = ref(false)
const helpVisibleKey = ref('')
let helpAutoHideTimer: ReturnType<typeof setTimeout> | null = null
const userName = '学习用户'
const router = useRouter()
const userMenus = [
  { text: '修改密码' },
  { text: '修改信息' },
  { text: '套餐' },
  { text: '今日额度' },
  { text: 'Logout' },
]

function onUserMenuSelect(action: { text: string }) {
  if (action.text === 'Logout') {
    router.push('/logout')
    return
  }
  if (action.text === '套餐' || action.text === '今日额度') {
    quotaPopupVisible.value = true
    if (!quota.value)
      loadQuota()
    return
  }
  showToast(`${action.text} 功能即将开放`)
}

function toggleHelp(key: string, visible: boolean) {
  if (helpAutoHideTimer) {
    clearTimeout(helpAutoHideTimer)
    helpAutoHideTimer = null
  }
  helpVisibleKey.value = visible ? key : ''
  if (visible) {
    helpAutoHideTimer = setTimeout(() => {
      helpVisibleKey.value = ''
      helpAutoHideTimer = null
    }, 5000)
  }
}

onBeforeUnmount(() => {
  if (helpAutoHideTimer)
    clearTimeout(helpAutoHideTimer)
})

// ---------- 额度小部件 ----------
const quota = ref<QuotaMe | null>(null)
const quotaLoading = ref(false)
const quotaError = ref('')

async function loadQuota() {
  if (!isLogin())
    return
  quotaLoading.value = true
  quotaError.value = ''
  try {
    quota.value = await fetchMyQuota()
  }
  catch (e: any) {
    quotaError.value = e?.msg || e?.message || '额度加载失败'
  }
  finally {
    quotaLoading.value = false
  }
}

const quotaRows = computed(() => {
  if (!quota.value)
    return []
  const q = quota.value
  return [
    { key: 'llm', label: '对话 (LLM)', used: q.llmUsed, total: q.llmDaily, remaining: q.llmRemaining, unit: '次' },
    { key: 'asr', label: '语音识别 (ASR)', used: q.asrUsedSec, total: q.asrDailySec, remaining: q.asrRemainingSec, unit: '秒' },
    { key: 'tts', label: '语音合成 (TTS)', used: q.ttsUsedChars, total: q.ttsDailyChars, remaining: q.ttsRemainingChars, unit: '字符' },
  ]
})

function pct(used: number, total: number) {
  if (!total || total <= 0)
    return 0
  return Math.min(100, Math.round((used / total) * 100))
}

onMounted(() => {
  loadQuota()
})

const visibleSections = computed(() => sections.filter(section => section.key !== 'speaking-practice'))
</script>

<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-user">
        <van-popover
          v-model:show="menuVisible"
          placement="bottom-end"
          :actions="userMenus"
          @select="onUserMenuSelect"
        >
          <template #reference>
            <button class="user-entry" type="button">
              <img :src="avatarUrl" alt="user avatar" class="user-avatar">
              <span class="user-name">{{ userName }}</span>
              <van-icon name="arrow-down" />
            </button>
          </template>
        </van-popover>
      </div>
      <h1 class="hero-title">
        English Speech Coach
      </h1>
      <div class="hero-meta">
        <p class="hero-subtitle">
          选择应用开始学习
        </p>
      </div>
    </section>

    <van-popup v-model:show="quotaPopupVisible" round position="bottom" :style="{ maxHeight: '72vh' }">
      <div class="quota-popup-body">
        <section class="quota-card">
          <div class="quota-head">
            <h3 class="quota-title">
              今日额度
            </h3>
            <button class="quota-refresh" type="button" :disabled="quotaLoading" @click="loadQuota">
              <van-icon name="replay" />
              {{ quotaLoading ? '加载中' : '刷新' }}
            </button>
          </div>
          <div v-if="quota && quota.enabled === false" class="quota-frozen">
            账号已被冻结，请联系管理员
          </div>
          <div v-else-if="quotaError" class="quota-error">
            {{ quotaError }}
          </div>
          <div v-else-if="quota" class="quota-grid">
            <div v-for="row in quotaRows" :key="row.key" class="quota-row">
              <div class="quota-row-head">
                <span class="quota-row-label">{{ row.label }}</span>
                <span class="quota-row-num">
                  {{ row.used }} / {{ row.total }} {{ row.unit }}
                </span>
              </div>
              <van-progress
                :percentage="pct(row.used, row.total)"
                :color="pct(row.used, row.total) >= 90 ? '#ee0a24' : '#1f4d8f'"
                :show-pivot="false"
                stroke-width="6"
              />
              <div class="quota-row-remaining">
                剩余 {{ row.remaining }} {{ row.unit }}
              </div>
            </div>
          </div>
          <div v-else class="quota-placeholder">
            加载中...
          </div>
        </section>
      </div>
    </van-popup>

    <section v-for="section in visibleSections" :key="section.key" class="app-section">
      <div class="section-head">
        <h2 class="section-title">
          {{ section.title }}
        </h2>
        <van-popover
          :show="helpVisibleKey === section.key"
          trigger="click"
          placement="top-start"
          @update:show="(visible) => toggleHelp(section.key, visible)"
        >
          <div class="help-content">
            {{ section.help }}
          </div>
          <template #reference>
            <button class="help-btn" type="button" aria-label="help info">
              i
            </button>
          </template>
        </van-popover>
      </div>
      <div class="app-grid">
        <component
          :is="item.enabled ? 'router-link' : 'div'"
          v-for="item in section.items"
          :key="item.key"
          :to="item.to"
          class="app-card"
          :class="{ 'is-disabled': !item.enabled }"
        >
          <van-icon :name="item.icon" size="28" />
          <span class="app-label">{{ item.label }}</span>
          <span v-if="!item.enabled" class="app-status">即将开放</span>
        </component>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  min-height: 100dvh;
  padding: 12px 16px 24px;
  box-sizing: border-box;
  background: linear-gradient(180deg, #f2f7ff 0%, #f7fbff 35%, #ffffff 100%);
}

.user-entry {
  appearance: none;
  border: 1px solid #d8e5fb;
  border-radius: 999px;
  background: #fff;
  color: #355f99;
  height: 38px;
  padding: 0 10px 0 6px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  font-size: 13px;
}

.hero {
  margin-bottom: 18px;
  padding: 20px 16px;
  border-radius: 16px;
  background: #1f4d8f;
  color: #fff;
}

.hero-user {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

.hero-title {
  margin: 0;
  font-size: 20px;
  line-height: 1.2;
}

.hero-subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 13px;
}

.hero-meta {
  margin-top: 10px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
}

.app-section {
  margin-top: 14px;
}

.section-head {
  margin: 0 0 10px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 6px;
}

.section-title {
  margin: 0;
  font-size: 17px;
  color: #1c2b43;
}

.help-btn {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 1px solid #c4d2ea;
  background: #fff;
  color: #3d5f95;
  font-size: 10px;
  font-weight: 700;
  line-height: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.help-content {
  max-width: 220px;
  padding: 2px 5px;
  font-size: 12px;
  line-height: 1.5;
  text-align: right;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.app-card {
  min-height: 92px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #dbe8fb;
  color: #1f4d8f;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.app-label {
  font-size: 14px;
  font-weight: 600;
}

.app-status {
  font-size: 11px;
  color: #98a6bc;
}

.is-disabled {
  color: #8f9db1;
  border-color: #e3e9f3;
  background: #f6f8fc;
}

.quota-card {
  margin: 0;
  padding: 14px 16px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #dbe8fb;
  color: #1c2b43;
}

.quota-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.quota-title {
  margin: 0;
  font-size: 15px;
  color: #1c2b43;
}

.quota-refresh {
  appearance: none;
  border: 1px solid #dbe8fb;
  background: #f6faff;
  color: #1f4d8f;
  border-radius: 999px;
  padding: 3px 10px;
  font-size: 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.quota-refresh:disabled {
  opacity: 0.6;
}

.quota-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quota-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.quota-row-head {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.quota-row-label {
  color: #355f99;
}

.quota-row-num {
  color: #1c2b43;
  font-variant-numeric: tabular-nums;
}

.quota-row-remaining {
  font-size: 11px;
  color: #8f9db1;
}

.quota-frozen {
  color: #ee0a24;
  font-size: 13px;
  text-align: center;
  padding: 8px 0;
}

.quota-error {
  color: #ee0a24;
  font-size: 12px;
}

.quota-placeholder {
  color: #8f9db1;
  font-size: 12px;
}

.quota-popup-body {
  padding: 14px 12px calc(12px + env(safe-area-inset-bottom));
  background: #f7fbff;
}
</style>
