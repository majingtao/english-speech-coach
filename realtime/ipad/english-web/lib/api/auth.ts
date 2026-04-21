import { apiClient } from "@/lib/api/client"

type YudaoAuthResp = {
  accessToken: string
  refreshToken: string
  expiresTime: number
  userId: number
}

export type LoginResult = {
  token: string
  expiresIn: number
}

function toLoginResult(data: YudaoAuthResp): LoginResult {
  return {
    token: data.accessToken,
    expiresIn: Math.floor((data.expiresTime - Date.now()) / 1000),
  }
}

export async function loginBySms(params: { mobile: string; code: string }) {
  const data = await apiClient.post("/app-api/member/auth/sms-login", params) as unknown as YudaoAuthResp
  return toLoginResult(data)
}

export async function loginByEmail(params: { email: string; password: string }) {
  const data = await apiClient.post("/app-api/member/email-auth/email-login", params) as unknown as YudaoAuthResp
  return toLoginResult(data)
}

export async function loginByUsername(params: { username: string; password: string }) {
  const data = await apiClient.post("/app-api/member/email-auth/username-login", params) as unknown as YudaoAuthResp
  return toLoginResult(data)
}

export async function registerByEmail(params: { email: string; code: string; password: string }) {
  const data = await apiClient.post("/app-api/member/email-auth/email-register", params) as unknown as YudaoAuthResp
  return toLoginResult(data)
}

export async function registerByUsername(params: { username: string; password: string }) {
  const data = await apiClient.post("/app-api/member/email-auth/username-register", params) as unknown as YudaoAuthResp
  return toLoginResult(data)
}

export async function sendSmsCode(params: { mobile: string; scene?: number }) {
  await apiClient.post("/app-api/member/auth/send-sms-code", {
    mobile: params.mobile,
    scene: params.scene ?? 2,
  })
}

export async function sendEmailCode(params: { email: string; scene: string }) {
  await apiClient.post("/app-api/member/email-auth/send-email-code", params)
}

export async function checkEmail(email: string) {
  return apiClient.get("/app-api/member/email-auth/check-email", {
    params: { email },
  }) as unknown as boolean
}

export async function checkUsername(username: string) {
  return apiClient.get("/app-api/member/email-auth/check-username", {
    params: { username },
  }) as unknown as boolean
}

export async function checkMobile(mobile: string) {
  return apiClient.get("/app-api/member/email-auth/check-mobile", {
    params: { mobile },
  }) as unknown as boolean
}

export async function logout() {
  await apiClient.post("/app-api/member/auth/logout")
}
