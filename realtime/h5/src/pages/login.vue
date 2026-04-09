<script setup lang="ts">
import { reactive, ref } from 'vue'
import { showNotify, showToast } from 'vant'
import { loginByMock } from '@/api/auth'
import { setToken } from '@/utils/auth'
import logoUrl from '@/assets/images/logo.png'

const router = useRouter()
const route = useRoute()

const form = reactive({
  username: '',
  password: '',
})

const loading = ref(false)

async function onSubmit() {
  if (!form.username || !form.password) {
    showToast('请填写用户名和密码')
    return
  }
  loading.value = true
  try {
    const result = await loginByMock(form)
    setToken(result.token)
    showToast('登录成功')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    router.replace(redirect)
  }
  catch (error: any) {
    showNotify({ type: 'danger', message: error?.message || '登录失败' })
  }
  finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="logo-wrap">
        <img :src="logoUrl" alt="English Speech Coach Logo" class="logo-image">
      </div>
      <h1 class="site-title">
        English Speech Coach
      </h1>
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field
            v-model="form.username"
            name="username"
            label="账号"
            placeholder="请输入账号（demo）"
            autocomplete="username"
          />
          <van-field
            v-model="form.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码（123456）"
            autocomplete="current-password"
          />
        </van-cell-group>
        <div class="submit-wrap">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="loading"
          >
            登录
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  min-height: 100dvh;
  display: grid;
  place-items: center;
  padding: 20px;
  background: linear-gradient(180deg, #eef4ff 0%, #f7fbff 55%, #ffffff 100%);
  box-sizing: border-box;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgb(31 78 152 / 12%);
  padding: 24px 16px 20px;
}

.logo-wrap {
  width: 150px;
  height: 150px;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.site-title {
  margin: 0 0 18px;
  text-align: center;
  font-size: 22px;
  font-weight: 700;
  color: #1a3d74;
}

.submit-wrap {
  margin: 20px 16px 0;
}
</style>
