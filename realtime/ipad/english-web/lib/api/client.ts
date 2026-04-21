"use client"

import axios, { type AxiosError } from "axios"
import {
  DEFAULT_TENANT_ID,
  TENANT_ID_HEADER,
} from "@/lib/auth/constants"
import { redirectToLoginWithCurrentPath } from "@/lib/auth/redirect"
import { getTokenFromStorage } from "@/lib/auth/token"
import { ApiBusinessError, type YudaoResponse } from "@/lib/api/types"
import { useAuthStore } from "@/lib/stores/auth-store"

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL ?? "",
  timeout: 15000,
})

function kickToLogin() {
  useAuthStore.getState().clearToken()
  redirectToLoginWithCurrentPath()
}

apiClient.interceptors.request.use((config) => {
  const token = getTokenFromStorage()
  const headers = config.headers
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }
  if (!headers[TENANT_ID_HEADER]) {
    headers[TENANT_ID_HEADER] = process.env.NEXT_PUBLIC_TENANT_ID ?? DEFAULT_TENANT_ID
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => {
    const body = response.data as YudaoResponse<unknown>
    if (body && typeof body === "object" && "code" in body) {
      if (body.code === 0) {
        return body.data
      }
      if (body.code === 401) {
        kickToLogin()
      }
      throw new ApiBusinessError(body.msg || "业务异常", body.code)
    }
    return response.data
  },
  (error: AxiosError<YudaoResponse<unknown>>) => {
    const status = error.response?.status
    if (status === 401) {
      kickToLogin()
    }
    const msg =
      error.response?.data?.msg ||
      error.message ||
      "网络请求失败"
    throw new ApiBusinessError(msg)
  }
)

export { apiClient }

