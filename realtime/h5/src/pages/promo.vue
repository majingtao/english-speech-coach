<script setup lang="ts">
import { isLogin } from '@/utils/auth'

const router = useRouter()
const route = useRoute()

const highlights = [
  {
    title: '口语考试',
    desc: '基于公开考试能力框架与自建训练题库，支持分步骤练习与反馈。',
  },
  {
    title: '口语练习',
    desc: '按级别进行模拟考试对话训练，覆盖小学到雅思/托福阶段。',
  },
  {
    title: '自由对话',
    desc: '按级别自由交流，提升真实语境下的表达流畅度。',
  },
  {
    title: '听写训练',
    desc: '按教材进行听写，也支持逐步扩展到自定义听写题库。',
  },
]

const plans = [
  {
    name: '标准版',
    period: '1个月',
    price: '59',
    points: ['基础口语训练', '听写基础功能', '适合日常练习'],
    badge: '',
  },
  {
    name: '冲刺版',
    period: '1个月',
    price: '99',
    points: ['强化训练内容', '模拟考试流程', '适合考前30天'],
    badge: '热门',
  },
  {
    name: '冲刺版',
    period: '3个月',
    price: '259',
    points: ['完整备考周期', '月均更划算', '适合长期稳定提分'],
    badge: '推荐',
  },
]

function buyPlan(planName: string, period: string, amount: string) {
  const target = router.resolve({
    path: '/checkout',
    query: { plan: planName, period, amount },
  }).fullPath
  if (!isLogin()) {
    router.push({ path: '/login', query: { redirect: target } })
    return
  }
  router.push(target)
}

function goLogin() {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
  router.push({ path: '/login', query: { redirect } })
}
</script>

<template>
  <div class="promo-page">
    <section class="hero">
      <h1 class="hero-title">
        English Speech Coach
      </h1>
      <p class="hero-subtitle">
        英语口语考试与训练平台
      </p>
      <div class="hero-actions">
        <van-button type="primary" round @click="goLogin">
          立即登录体验
        </van-button>
      </div>
    </section>

    <section class="block">
      <h2 class="block-title">
        服务内容
      </h2>
      <div class="highlight-grid">
        <div v-for="item in highlights" :key="item.title" class="highlight-card">
          <h3 class="highlight-title">
            {{ item.title }}
          </h3>
          <p class="highlight-desc">
            {{ item.desc }}
          </p>
        </div>
      </div>
    </section>

    <section class="block">
      <h2 class="block-title">
        套餐价格
      </h2>
      <div class="plan-list">
        <div v-for="plan in plans" :key="`${plan.name}-${plan.period}`" class="plan-card">
          <div class="plan-head">
            <span class="plan-name">{{ plan.name }}</span>
            <span class="plan-period">{{ plan.period }}</span>
            <van-tag v-if="plan.badge" type="primary" round>
              {{ plan.badge }}
            </van-tag>
          </div>
          <div class="plan-price">
            <span class="currency">¥</span>{{ plan.price }}
          </div>
          <ul class="plan-points">
            <li v-for="point in plan.points" :key="point">
              {{ point }}
            </li>
          </ul>
          <div class="plan-actions">
            <van-button
              size="small"
              type="primary"
              round
              @click="buyPlan(plan.name, plan.period, plan.price)"
            >
              购买
            </van-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.promo-page {
  min-height: 100vh;
  min-height: 100dvh;
  padding: 16px;
  box-sizing: border-box;
  background: linear-gradient(180deg, #f1f7ff 0%, #ffffff 40%);
}

.hero {
  padding: 22px 18px;
  border-radius: 18px;
  color: #fff;
  background: linear-gradient(135deg, #1954a6 0%, #2b7fe8 100%);
  box-shadow: 0 10px 24px rgb(30 80 150 / 18%);
}

.hero-title {
  margin: 0;
  font-size: 24px;
  line-height: 1.2;
}

.hero-subtitle {
  margin: 8px 0 0;
  font-size: 13px;
  line-height: 1.6;
  opacity: 0.95;
}

.hero-actions {
  margin-top: 14px;
}

.block {
  margin-top: 14px;
  padding: 14px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #e3ebf7;
}

.block-title {
  margin: 0 0 10px;
  font-size: 16px;
  color: #1b3559;
}

.highlight-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.highlight-card {
  padding: 12px;
  border-radius: 12px;
  background: #f7faff;
  border: 1px solid #e0ebff;
}

.highlight-title {
  margin: 0;
  font-size: 14px;
  color: #1f4d8f;
}

.highlight-desc {
  margin: 6px 0 0;
  font-size: 12px;
  color: #567097;
  line-height: 1.6;
}

.plan-list {
  display: grid;
  gap: 10px;
}

.plan-card {
  border-radius: 12px;
  border: 1px solid #dbe8fb;
  background: #fdfefe;
  padding: 12px;
}

.plan-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.plan-name {
  font-size: 14px;
  font-weight: 700;
  color: #1f4d8f;
}

.plan-period {
  font-size: 12px;
  color: #7589a8;
}

.plan-price {
  margin-top: 8px;
  font-size: 28px;
  color: #1c5db6;
  font-weight: 800;
}

.currency {
  font-size: 14px;
  margin-right: 2px;
}

.plan-points {
  margin: 8px 0 0;
  padding-left: 18px;
  color: #516985;
  font-size: 12px;
  line-height: 1.6;
}

.plan-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
</style>
