<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { showNotify, showToast } from 'vant'
import { logout } from '@/api/auth'
import { clearToken } from '@/utils/auth'

const router = useRouter()
const loading = ref(true)

onMounted(async () => {
  try {
    await logout()
    clearToken()
    showToast('已退出登录')
  }
  catch (error: any) {
    showNotify({ type: 'danger', message: error?.message || '退出失败' })
    clearToken()
  }
  finally {
    loading.value = false
    router.replace('/login')
  }
})
</script>

<template>
  <div class="logout-page">
    <van-loading v-if="loading" size="24px" vertical>
      正在退出...
    </van-loading>
  </div>
</template>

<style scoped>
.logout-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
