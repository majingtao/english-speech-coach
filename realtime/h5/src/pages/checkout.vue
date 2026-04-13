<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { showNotify, showToast } from 'vant'
import { createMockOrder, payByMock } from '@/api/payment'

const router = useRouter()
const route = useRoute()

const planName = computed(() => (typeof route.query.plan === 'string' ? route.query.plan : '套餐'))
const period = computed(() => (typeof route.query.period === 'string' ? route.query.period : '1个月'))
const amount = computed(() => {
  const raw = typeof route.query.amount === 'string' ? Number(route.query.amount) : NaN
  return Number.isFinite(raw) && raw > 0 ? raw : 59
})

const orderId = ref('')
const selectedChannel = ref<'alipay' | 'wechat'>('alipay')
const creating = ref(false)
const paying = ref(false)

async function createOrder() {
  creating.value = true
  try {
    const order = await createMockOrder({
      planName: planName.value,
      period: period.value,
      amount: amount.value,
    })
    orderId.value = order.orderId
  }
  catch (error: any) {
    showNotify({ type: 'danger', message: error?.message || '创建订单失败' })
  }
  finally {
    creating.value = false
  }
}

async function submitPay() {
  if (!orderId.value) {
    showToast('订单未创建完成')
    return
  }
  paying.value = true
  try {
    const res = await payByMock(orderId.value, selectedChannel.value)
    if (res.success) {
      showToast(res.message)
      router.push('/')
      return
    }
    showNotify({ type: 'warning', message: res.message })
  }
  catch (error: any) {
    showNotify({ type: 'danger', message: error?.message || '支付失败' })
  }
  finally {
    paying.value = false
  }
}

onMounted(() => {
  createOrder()
})
</script>

<template>
  <div class="checkout-page">
    <van-nav-bar title="订单支付" left-text="返回" left-arrow @click-left="router.back()" />

    <div class="panel">
      <h2 class="panel-title">
        订单信息
      </h2>
      <van-cell-group inset>
        <van-cell title="套餐" :value="planName" />
        <van-cell title="时长" :value="period" />
        <van-cell title="金额" :value="`¥${amount}`" />
        <van-cell title="订单号" :value="orderId || (creating ? '创建中...' : '-') " />
      </van-cell-group>
    </div>

    <div class="panel">
      <h2 class="panel-title">
        支付方式（模拟）
      </h2>
      <van-radio-group v-model="selectedChannel">
        <van-cell clickable title="支付宝" @click="selectedChannel = 'alipay'">
          <template #right-icon>
            <van-radio name="alipay" />
          </template>
        </van-cell>
        <van-cell clickable title="微信支付" @click="selectedChannel = 'wechat'">
          <template #right-icon>
            <van-radio name="wechat" />
          </template>
        </van-cell>
      </van-radio-group>
      <p class="tip">
        当前为支付模拟流程，后续可替换为支付宝/微信官方 SDK 下单与回调验签。
      </p>
      <van-button
        class="pay-btn"
        type="primary"
        block
        :loading="paying"
        :disabled="creating || !orderId"
        @click="submitPay"
      >
        立即支付
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.checkout-page {
  min-height: 100vh;
  min-height: 100dvh;
  padding-bottom: 20px;
  background: #f5f8fd;
}

.panel {
  margin: 12px;
  border: 1px solid #e4ecf8;
  border-radius: 14px;
  background: #fff;
  padding: 12px 0;
}

.panel-title {
  margin: 0 12px 10px;
  font-size: 15px;
  color: #1f3f6e;
}

.tip {
  margin: 10px 12px 0;
  font-size: 12px;
  color: #6e829f;
  line-height: 1.6;
}

.pay-btn {
  margin: 12px;
}
</style>
