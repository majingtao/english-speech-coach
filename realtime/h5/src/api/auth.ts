import request from '@/utils/request'

export interface LoginResult {
  token: string
  expiresIn: number
}

interface YudaoAuthResp {
  accessToken: string
  refreshToken: string
  expiresTime: number
  userId: number
}

function toLoginResult(data: YudaoAuthResp): LoginResult {
  return {
    token: data.accessToken,
    expiresIn: Math.floor((data.expiresTime - Date.now()) / 1000),
  }
}

/** 账号密码登录（yudao H5 会员） */
export async function loginByPassword(params: { username: string, password: string }): Promise<LoginResult> {
  const data = await request.post<YudaoAuthResp>('/app-api/member/auth/login', params)
  return toLoginResult(data)
}

/** 手机号+验证码登录 */
export async function loginBySms(params: { mobile: string, code: string }): Promise<LoginResult> {
  const data = await request.post<YudaoAuthResp>('/app-api/member/auth/sms-login', params)
  return toLoginResult(data)
}

/**
 * 发送短信验证码
 * scene: 1-会员注册 2-会员登录 3-重置密码（参考 yudao SmsSceneEnum）
 */
export async function sendSmsCode(params: { mobile: string, scene?: number }): Promise<void> {
  await request.post('/app-api/member/auth/send-sms-code', {
    mobile: params.mobile,
    scene: params.scene ?? 2,
  })
}

// ========== 唯一性检查（返回 true 表示已注册） ==========

export async function checkEmail(email: string): Promise<boolean> {
  return request.get<boolean>('/app-api/member/email-auth/check-email', { params: { email } })
}

export async function checkUsername(username: string): Promise<boolean> {
  return request.get<boolean>('/app-api/member/email-auth/check-username', { params: { username } })
}

export async function checkMobile(mobile: string): Promise<boolean> {
  return request.get<boolean>('/app-api/member/email-auth/check-mobile', { params: { mobile } })
}

// ========== 邮箱注册/登录 ==========

/** 发送邮箱验证码 */
export async function sendEmailCode(params: { email: string, scene: string }): Promise<void> {
  await request.post('/app-api/member/email-auth/send-email-code', params)
}

/** 邮箱注册（验证码 + 密码） */
export async function registerByEmail(params: {
  email: string
  code: string
  password: string
}): Promise<LoginResult> {
  const data = await request.post<YudaoAuthResp>('/app-api/member/email-auth/email-register', params)
  return toLoginResult(data)
}

/** 邮箱 + 密码登录 */
export async function loginByEmail(params: {
  email: string
  password: string
}): Promise<LoginResult> {
  const data = await request.post<YudaoAuthResp>('/app-api/member/email-auth/email-login', params)
  return toLoginResult(data)
}

/** 账号密码注册 */
export async function registerByUsername(params: { username: string, password: string }): Promise<LoginResult> {
  const data = await request.post<YudaoAuthResp>('/app-api/member/email-auth/username-register', params)
  return toLoginResult(data)
}

/** 账号密码登录 */
export async function loginByUsername(params: { username: string, password: string }): Promise<LoginResult> {
  const data = await request.post<YudaoAuthResp>('/app-api/member/email-auth/username-login', params)
  return toLoginResult(data)
}

/** 退出登录 */
export async function logout(): Promise<void> {
  await request.post('/app-api/member/auth/logout').catch(() => {})
}
