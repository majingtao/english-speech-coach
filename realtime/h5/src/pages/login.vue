<script setup lang="ts">
import { reactive, ref } from 'vue'
import { showNotify, showToast } from 'vant'
import {
  loginBySms,
  loginByEmail,
  loginByUsername,
  registerByEmail,
  registerByUsername,
  sendSmsCode,
  sendEmailCode,
  checkEmail,
  checkUsername,
  checkMobile,
} from '@/api/auth'
import { setToken } from '@/utils/auth'
import logoUrl from '@/assets/images/logo.png'

const router = useRouter()
const route = useRoute()

const mode = ref<'login' | 'register'>('login')
const channel = ref<'email' | 'account' | 'sms'>('email')
const loading = ref(false)
const emailLoginPwdVisible = ref(false)
const emailRegPwdVisible = ref(false)
const accountLoginPwdVisible = ref(false)
const accountRegPwdVisible = ref(false)

// -------- 表单 --------
const emailLoginForm = reactive({ email: '', password: '' })
const emailRegForm = reactive({ email: '', code: '', password: '' })
const accountLoginForm = reactive({ username: '', password: '' })
const accountRegForm = reactive({ username: '', password: '' })
const smsLoginForm = reactive({ mobile: '', code: '' })
const smsRegForm = reactive({ mobile: '', code: '' })

// -------- 倒计时 --------
const emailCountdown = ref(0)
const smsCountdown = ref(0)
let emailTimer: ReturnType<typeof setInterval> | null = null
let smsTimer: ReturnType<typeof setInterval> | null = null

function startCountdown(countdownRef: typeof emailCountdown, timerKey: 'email' | 'sms') {
  countdownRef.value = 60
  const t = setInterval(() => {
    countdownRef.value--
    if (countdownRef.value <= 0) {
      clearInterval(t)
      if (timerKey === 'email') emailTimer = null
      else smsTimer = null
    }
  }, 1000)
  if (timerKey === 'email') emailTimer = t
  else smsTimer = t
}

// -------- 格式校验 --------
function isValidEmail(v: string) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) }
function isValidMobile(v: string) { return /^1[3-9]\d{9}$/.test(v) }
function isValidUsername(v: string) { return /^[a-zA-Z0-9_]{4,20}$/.test(v) }

function stopCountdown(timerKey: 'email' | 'sms') {
  if (timerKey === 'email') {
    if (emailTimer) { clearInterval(emailTimer); emailTimer = null }
    emailCountdown.value = 0
  } else {
    if (smsTimer) { clearInterval(smsTimer); smsTimer = null }
    smsCountdown.value = 0
  }
}

// -------- 发送邮箱验证码（注册前先查重） --------
async function onSendEmailCode() {
  if (emailCountdown.value > 0) return
  const email = emailRegForm.email
  if (!isValidEmail(email)) { showToast('请输入正确的邮箱'); return }
  startCountdown(emailCountdown, 'email') // 点击即锁 60s，避免重复点击
  try {
    const exists = await checkEmail(email)
    if (exists) {
      stopCountdown('email')
      showNotify({ type: 'warning', message: '该邮箱已注册，请直接登录' })
      return
    }
    await sendEmailCode({ email, scene: 'register' })
    showToast('验证码已发送')
  } catch (e: any) {
    stopCountdown('email')
    showNotify({ type: 'danger', message: e?.msg || e?.message || '发送失败' })
  }
}

// -------- 发送短信验证码（注册前先查重） --------
async function onSendSmsCode() {
  if (smsCountdown.value > 0) return
  const mobile = mode.value === 'register' ? smsRegForm.mobile : smsLoginForm.mobile
  if (!isValidMobile(mobile)) { showToast('请输入正确的手机号（中国大陆11位）'); return }
  startCountdown(smsCountdown, 'sms')
  try {
    if (mode.value === 'register') {
      const exists = await checkMobile(mobile)
      if (exists) {
        stopCountdown('sms')
        showNotify({ type: 'warning', message: '该手机号已注册，请直接登录' })
        return
      }
    }
    // yudao sms-login 统一使用 scene=2(MEMBER_LOGIN)，会自动注册不存在的用户
    await sendSmsCode({ mobile, scene: 2 })
    showToast('验证码已发送')
  } catch (e: any) {
    stopCountdown('sms')
    showNotify({ type: 'danger', message: e?.msg || e?.message || '发送失败' })
  }
}

// -------- 登录成功 --------
function afterLoginSuccess(token: string) {
  setToken(token)
  showToast(mode.value === 'register' ? '注册成功' : '登录成功')
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
  router.replace(redirect)
}

// -------- 提交 --------
async function onSubmit() {
  loading.value = true
  try {
    if (mode.value === 'login') {
      await handleLogin()
    } else {
      await handleRegister()
    }
  } catch (e: any) {
    showNotify({ type: 'danger', message: e?.msg || e?.message || '操作失败' })
  } finally {
    loading.value = false
  }
}

async function handleLogin() {
  if (channel.value === 'email') {
    if (!isValidEmail(emailLoginForm.email)) { showToast('请输入正确的邮箱'); return }
    if (!emailLoginForm.password) { showToast('请填写密码'); return }
    const r = await loginByEmail({ email: emailLoginForm.email, password: emailLoginForm.password })
    afterLoginSuccess(r.token)
  } else if (channel.value === 'account') {
    if (!accountLoginForm.username) { showToast('请填写账号'); return }
    if (!accountLoginForm.password) { showToast('请填写密码'); return }
    const r = await loginByUsername(accountLoginForm)
    afterLoginSuccess(r.token)
  } else {
    if (!isValidMobile(smsLoginForm.mobile)) { showToast('请输入正确的手机号（中国大陆11位）'); return }
    if (!smsLoginForm.code) { showToast('请填写验证码'); return }
    const r = await loginBySms(smsLoginForm)
    afterLoginSuccess(r.token)
  }
}

async function handleRegister() {
  if (channel.value === 'email') {
    if (!isValidEmail(emailRegForm.email)) { showToast('请输入正确的邮箱'); return }
    if (!emailRegForm.code) { showToast('请填写验证码'); return }
    if (!emailRegForm.password || emailRegForm.password.length < 6) { showToast('密码至少 6 位'); return }
    const r = await registerByEmail(emailRegForm)
    afterLoginSuccess(r.token)
  } else if (channel.value === 'account') {
    if (!isValidUsername(accountRegForm.username)) {
      showToast('账号 4-20 位，仅字母/数字/下划线')
      return
    }
    if (!accountRegForm.password || accountRegForm.password.length < 6) { showToast('密码至少 6 位'); return }
    // 先查重
    const exists = await checkUsername(accountRegForm.username)
    if (exists) { showNotify({ type: 'warning', message: '该账号已被注册' }); return }
    const r = await registerByUsername(accountRegForm)
    afterLoginSuccess(r.token)
  } else {
    if (!isValidMobile(smsRegForm.mobile)) { showToast('请输入正确的手机号（中国大陆11位）'); return }
    if (!smsRegForm.code) { showToast('请填写验证码'); return }
    const r = await loginBySms(smsRegForm)
    afterLoginSuccess(r.token)
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

      <!-- 第一层：登录 / 注册 -->
      <div class="mode-switch">
        <span :class="{ active: mode === 'login' }" @click="mode = 'login'">登录</span>
        <span class="sep">|</span>
        <span :class="{ active: mode === 'register' }" @click="mode = 'register'">注册</span>
      </div>

      <!-- 第二层：邮箱 / 账号 / 手机 -->
      <van-tabs v-model:active="channel" shrink animated>
        <van-tab title="邮箱" name="email" />
        <van-tab title="账号" name="account" />
        <van-tab title="手机" name="sms" />
      </van-tabs>

      <van-form @submit="onSubmit">
        <!-- ====== 邮箱 - 登录 ====== -->
        <van-cell-group v-if="channel === 'email' && mode === 'login'" inset>
          <van-field v-model="emailLoginForm.email" label="邮箱" type="email" placeholder="请输入邮箱" autocomplete="email" />
          <van-field
            v-model="emailLoginForm.password"
            :type="emailLoginPwdVisible ? 'text' : 'password'"
            label="密码"
            placeholder="请输入密码"
            :right-icon="emailLoginPwdVisible ? 'eye-o' : 'closed-eye'"
            autocomplete="current-password"
            @click-right-icon="emailLoginPwdVisible = !emailLoginPwdVisible"
          />
        </van-cell-group>

        <!-- ====== 邮箱 - 注册 ====== -->
        <van-cell-group v-else-if="channel === 'email' && mode === 'register'" inset>
          <van-field v-model="emailRegForm.email" label="邮箱" type="email" placeholder="请输入邮箱" autocomplete="email" />
          <van-field v-model="emailRegForm.code" label="验证码" maxlength="6" placeholder="请输入验证码" autocomplete="one-time-code">
            <template #button>
              <van-button size="small" type="primary" plain :disabled="emailCountdown > 0" @click.prevent="onSendEmailCode">
                {{ emailCountdown > 0 ? `${emailCountdown}s` : '发送验证码' }}
              </van-button>
            </template>
          </van-field>
          <van-field
            v-model="emailRegForm.password"
            :type="emailRegPwdVisible ? 'text' : 'password'"
            label="密码"
            placeholder="请设置密码（6 位以上）"
            :right-icon="emailRegPwdVisible ? 'eye-o' : 'closed-eye'"
            autocomplete="new-password"
            @click-right-icon="emailRegPwdVisible = !emailRegPwdVisible"
          />
        </van-cell-group>

        <!-- ====== 账号 - 登录 ====== -->
        <van-cell-group v-else-if="channel === 'account' && mode === 'login'" inset>
          <van-field v-model="accountLoginForm.username" label="账号" placeholder="请输入账号" autocomplete="username" />
          <van-field
            v-model="accountLoginForm.password"
            :type="accountLoginPwdVisible ? 'text' : 'password'"
            label="密码"
            placeholder="请输入密码"
            :right-icon="accountLoginPwdVisible ? 'eye-o' : 'closed-eye'"
            autocomplete="current-password"
            @click-right-icon="accountLoginPwdVisible = !accountLoginPwdVisible"
          />
        </van-cell-group>

        <!-- ====== 账号 - 注册 ====== -->
        <van-cell-group v-else-if="channel === 'account' && mode === 'register'" inset>
          <van-field
            v-model="accountRegForm.username"
            label="账号"
            placeholder="4-20位 字母/数字/下划线"
            autocomplete="username"
            maxlength="20"
          />
          <van-field
            v-model="accountRegForm.password"
            :type="accountRegPwdVisible ? 'text' : 'password'"
            label="密码"
            placeholder="请设置密码（6 位以上）"
            :right-icon="accountRegPwdVisible ? 'eye-o' : 'closed-eye'"
            autocomplete="new-password"
            @click-right-icon="accountRegPwdVisible = !accountRegPwdVisible"
          />
        </van-cell-group>

        <!-- ====== 手机 - 登录 ====== -->
        <van-cell-group v-else-if="channel === 'sms' && mode === 'login'" inset>
          <van-field v-model="smsLoginForm.mobile" label="手机号" type="tel" maxlength="11" placeholder="请输入中国大陆手机号" autocomplete="tel" />
          <van-field v-model="smsLoginForm.code" label="验证码" maxlength="6" placeholder="请输入验证码" autocomplete="one-time-code">
            <template #button>
              <van-button size="small" type="primary" plain :disabled="smsCountdown > 0" @click.prevent="onSendSmsCode">
                {{ smsCountdown > 0 ? `${smsCountdown}s` : '发送验证码' }}
              </van-button>
            </template>
          </van-field>
        </van-cell-group>

        <!-- ====== 手机 - 注册 ====== -->
        <van-cell-group v-else inset>
          <van-field v-model="smsRegForm.mobile" label="手机号" type="tel" maxlength="11" placeholder="请输入中国大陆手机号" autocomplete="tel" />
          <van-field v-model="smsRegForm.code" label="验证码" maxlength="6" placeholder="请输入验证码" autocomplete="one-time-code">
            <template #button>
              <van-button size="small" type="primary" plain :disabled="smsCountdown > 0" @click.prevent="onSendSmsCode">
                {{ smsCountdown > 0 ? `${smsCountdown}s` : '发送验证码' }}
              </van-button>
            </template>
          </van-field>
        </van-cell-group>

        <div class="submit-wrap">
          <van-button round block type="primary" native-type="submit" :loading="loading">
            {{ mode === 'register' ? '注册' : '登录' }}
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
  width: 100%;
  display: grid;
  place-items: center;
  padding: 16px;
  background: linear-gradient(180deg, #eef4ff 0%, #f7fbff 55%, #ffffff 100%);
  box-sizing: border-box;
  overflow-x: hidden;
}

.login-card {
  width: min(100%, 420px);
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgb(31 78 152 / 12%);
  padding: 24px 16px 20px;
  overflow: hidden;
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

.mode-switch {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 0 0 14px;
  font-size: 16px;
  color: #999;
}

.mode-switch span:not(.sep) {
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 16px;
  transition: all 0.2s;
}

.mode-switch span.active {
  color: #fff;
  background: var(--van-primary-color, #1989fa);
  font-weight: 600;
}

.mode-switch .sep {
  color: #ddd;
  line-height: 32px;
}

.submit-wrap {
  margin: 20px 0 0;
}

.login-card :deep(.van-cell-group--inset) {
  margin: 0;
}
</style>
