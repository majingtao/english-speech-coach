<script setup lang="ts">
import { ref } from 'vue'
import { showToast } from 'vant'
import avatarUrl from '@/assets/images/default-avatar.svg'

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
    title: '口语考试',
    help: '严格按照真实的考试题库出题并答题',
    items: [
      { key: 'speaking-flyers', label: 'Flyers', icon: 'comment-circle-o', enabled: true, to: '/speech-flyers' },
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
const helpVisibleKey = ref('')
let helpAutoHideTimer: ReturnType<typeof setTimeout> | null = null
const userName = '学习用户'
const router = useRouter()
const userMenus = [
  { text: '修改密码' },
  { text: '修改信息' },
  { text: '套餐' },
  { text: 'Logout' },
]

function onUserMenuSelect(action: { text: string }) {
  if (action.text === 'Logout') {
    router.push('/logout')
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

    <section v-for="section in sections" :key="section.key" class="app-section">
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
</style>
