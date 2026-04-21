<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { showFailToast } from 'vant'
import { fetchExamSeriesList, type ExamSeries } from '@/api/speech'

type LevelKey = 'starters' | 'movers' | 'flyers'

interface YleLevel {
  key: LevelKey
  title: string
}

const levels: YleLevel[] = [
  { key: 'starters', title: 'Pre A1 Starters' },
  { key: 'movers', title: 'A1 Movers' },
  { key: 'flyers', title: 'A2 Flyers' },
]

const router = useRouter()
const activeLevelIndex = ref(2)
const currentLevel = computed(() => levels[activeLevelIndex.value])

const allSeries = ref<ExamSeries[]>([])
const loading = ref(false)

const currentBooks = computed(() =>
  allSeries.value.filter((s) => s.levelCode === currentLevel.value.key),
)

function goHome() {
  router.push('/')
}

function openBook(series: ExamSeries) {
  router.push({
    path: '/speech-flyers',
    query: {
      examSeries: 'yle',
      levelCode: currentLevel.value.key,
      levelName: currentLevel.value.title,
      seriesCode: series.code,
      seriesName: series.name,
    },
  })
}

onMounted(async () => {
  loading.value = true
  try {
    allSeries.value = await fetchExamSeriesList()
  }
  catch (error) {
    console.error('[speech-yle] fetch series failed', error)
    showFailToast('加载真题集失败')
  }
  finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="yle-page">
    <van-nav-bar
      title="YLE（剑桥少儿）"
      left-text="首页"
      left-arrow
      @click-left="goHome"
    />

    <div class="selection-panel">
      <aside class="sidebar">
        <van-sidebar v-model="activeLevelIndex">
          <van-sidebar-item v-for="level in levels" :key="level.key" :title="level.title" />
        </van-sidebar>
      </aside>

      <section class="content">
        <h2 class="content-title">
          {{ currentLevel.title }} - 真题书
        </h2>
        <van-loading v-if="loading" class="loading" size="24px" vertical>
          加载中...
        </van-loading>
        <div v-else-if="currentBooks.length === 0" class="empty">
          暂无真题集
        </div>
        <div v-else class="book-grid">
          <button
            v-for="series in currentBooks"
            :key="series.code"
            class="book-card"
            type="button"
            @click="openBook(series)"
          >
            <div class="book-cover-wrap">
              <img v-if="series.coverUrl" :src="series.coverUrl" :alt="series.name" class="book-cover">
              <div v-else class="book-cover-placeholder">
                {{ series.name }}
              </div>
            </div>
            <div class="book-info">
              <div class="book-title">
                {{ series.name }}
              </div>
              <div class="book-subtitle">
                真题集
              </div>
            </div>
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.yle-page {
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
  width: 77px;
  border-right: 1px solid #e8edf5;
  background: #fff;
}

.content {
  flex: 1;
  padding: 12px 8px 16px;
}

.content-title {
  margin: 0 0 12px;
  color: #213b63;
  font-size: 16px;
}

.loading,
.empty {
  margin-top: 48px;
  text-align: center;
  color: #8b9ab0;
  font-size: 14px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.book-card {
  appearance: none;
  border: none;
  border-radius: 12px;
  background: transparent;
  text-align: left;
  padding: 0;
}

.book-cover-wrap {
  width: 100%;
  aspect-ratio: 3 / 4;
  border-radius: 10px;
  background: transparent;
  overflow: hidden;
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  text-align: center;
  color: #6f819d;
  font-size: 12px;
  background: #eaf0fb;
  border-radius: 10px;
}

.book-info {
  margin-top: 8px;
}

.book-title {
  color: #1f4d8f;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.3;
}

.book-subtitle {
  margin-top: 4px;
  color: #6f819d;
  font-size: 12px;
}
</style>
