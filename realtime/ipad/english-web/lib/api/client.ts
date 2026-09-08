"use client"

import axios, { type AxiosError, type InternalAxiosRequestConfig } from "axios"
import {
  DEFAULT_TENANT_ID,
  TENANT_ID_HEADER,
} from "@/lib/auth/constants"
import { redirectToLoginWithCurrentPath } from "@/lib/auth/redirect"
import {
  getRefreshTokenFromStorage,
  getTokenFromStorage,
  isRememberLoginActive,
  updateTokenInStorage,
} from "@/lib/auth/token"
import { ApiBusinessError, type YudaoResponse } from "@/lib/api/types"
import { useAuthStore } from "@/lib/stores/auth-store"

type AuthRetryConfig = InternalAxiosRequestConfig & {
  skipAuthRefresh?: boolean
  _retryCount?: number
}

const apiClient = axios.create({
  // Always use the current page origin. This lets iPad clients reach the
  // backend through Next.js rewrites without mixed HTTP/HTTPS content or a
  // browser-local 127.0.0.1 address.
  baseURL: "",
  timeout: 15000,
})

// Mobile networks (wifi<->cellular handoff, weak signal, cold TLS handshake)
// frequently drop a single request without anything being wrong server-side.
// Retry idempotent (GET/HEAD) requests a couple of times with backoff before
// surfacing an error, instead of making the user tap "重试" themselves.
const MAX_RETRIES = 2
const RETRY_DELAYS_MS = [600, 1400]

function isIdempotent(method?: string) {
  const m = (method || "get").toLowerCase()
  return m === "get" || m === "head"
}

function isRetryableError(error: AxiosError) {
  if (!isIdempotent(error.config?.method)) return false
  // No response at all: network drop, DNS blip, or timeout.
  if (!error.response) return true
  // Transient upstream/gateway failures are worth one more try too.
  return [502, 503, 504].includes(error.response.status)
}

function delay(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

function kickToLogin() {
  useAuthStore.getState().clearToken()
  redirectToLoginWithCurrentPath()
}

let refreshPromise: Promise<string> | null = null

function requestWithAuthHeader(config: AuthRetryConfig, token: string) {
  config.headers.Authorization = `Bearer ${token}`
  return apiClient.request(config)
}

async function refreshStoredToken() {
  const refreshToken = getRefreshTokenFromStorage()
  if (!refreshToken || !isRememberLoginActive()) {
    return ""
  }
  if (!refreshPromise) {
    refreshPromise = apiClient.post("/app-api/member/auth/refresh-token", null, {
      params: { refreshToken },
      skipAuthRefresh: true,
    } as AuthRetryConfig).then((data) => {
      const auth = data as unknown as {
        accessToken: string
        refreshToken: string
      }
      if (!auth.accessToken || !auth.refreshToken) {
        return ""
      }
      updateTokenInStorage(auth.accessToken, auth.refreshToken)
      useAuthStore.setState({
        token: auth.accessToken,
        isLoggedIn: true,
        isHydrated: true,
      })
      return auth.accessToken
    }).catch(() => {
      return ""
    }).finally(() => {
      refreshPromise = null
    })
  }
  return refreshPromise
}

async function retryAfterRefresh(config: InternalAxiosRequestConfig | undefined) {
  const retryConfig = config as AuthRetryConfig | undefined
  if (!retryConfig || retryConfig.skipAuthRefresh) {
    return null
  }
  const token = await refreshStoredToken()
  if (!token) {
    return null
  }
  retryConfig.skipAuthRefresh = true
  return requestWithAuthHeader(retryConfig, token)
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
        return retryAfterRefresh(response.config).then((retry) => {
          if (retry) {
            return retry
          }
          kickToLogin()
          throw new ApiBusinessError(body.msg || "登录已过期", body.code)
        })
      }
      throw new ApiBusinessError(body.msg || "业务异常", body.code)
    }
    return response.data
  },
  async (error: AxiosError<YudaoResponse<unknown>>) => {
    const status = error.response?.status
    if (status === 401) {
      const retry = await retryAfterRefresh(error.config)
      if (retry) {
        return retry
      }
      kickToLogin()
    } else if (isRetryableError(error)) {
      const retryConfig = error.config as AuthRetryConfig | undefined
      const retryCount = retryConfig?._retryCount ?? 0
      if (retryConfig && retryCount < MAX_RETRIES) {
        retryConfig._retryCount = retryCount + 1
        await delay(RETRY_DELAYS_MS[retryCount] ?? 1400)
        return apiClient.request(retryConfig)
      }
    }
    const msg =
      error.response?.data?.msg ||
      error.message ||
      "网络请求失败"
    throw new ApiBusinessError(msg)
  }
)

export { apiClient }
