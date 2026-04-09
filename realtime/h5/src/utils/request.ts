import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import { showNotify } from 'vant'
import { clearToken, getToken } from '@/utils/auth'

// 创建 axios 实例：默认指向 yudao /admin-api
// Python 语音代理（/llm /asr /tts）由 index.vue 内部 fetch 直连，不走这里
const request = axios.create({
  baseURL: import.meta.env.VITE_APP_API_BASE_URL || '/admin-api',
  timeout: 15000,
})

export interface YudaoResult<T = any> {
  code: number
  data: T
  msg: string
}

export type RequestError = AxiosError<YudaoResult>

function errorHandler(error: RequestError): Promise<any> {
  if (error.response) {
    const { data, status, statusText } = error.response
    if (status === 401) {
      clearToken()
      showNotify({ type: 'danger', message: '登录已失效，请重新登录' })
    }
    else if (status === 403) {
      showNotify({ type: 'danger', message: data?.msg || statusText || '无权访问' })
    }
    else {
      showNotify({ type: 'danger', message: data?.msg || statusText || '请求失败' })
    }
  }
  return Promise.reject(error)
}

function requestHandler(config: InternalAxiosRequestConfig): InternalAxiosRequestConfig {
  const token = getToken()
  if (token)
    config.headers.Authorization = `Bearer ${token}`
  // yudao 多租户：H5 默认走默认租户，必要时可改成读取 store
  if (!config.headers['tenant-id'])
    config.headers['tenant-id'] = import.meta.env.VITE_APP_TENANT_ID || '1'
  return config
}

request.interceptors.request.use(requestHandler, errorHandler)

// yudao 标准响应：{ code, data, msg }；code === 0 视为成功，直接返回 data
function responseHandler(response: AxiosResponse<YudaoResult>) {
  const body = response.data
  if (body && typeof body === 'object' && 'code' in body) {
    if (body.code === 0)
      return body.data
    showNotify({ type: 'danger', message: body.msg || '业务异常' })
    return Promise.reject(body)
  }
  return body
}

request.interceptors.response.use(responseHandler, errorHandler)

interface RequestInstance extends AxiosInstance {
  <T = any>(url: string, config?: AxiosRequestConfig): Promise<T>
  <T = any>(config: AxiosRequestConfig): Promise<T>
  get: <T = any>(url: string, config?: AxiosRequestConfig) => Promise<T>
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig) => Promise<T>
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig) => Promise<T>
  delete: <T = any>(url: string, config?: AxiosRequestConfig) => Promise<T>
}

export default request as unknown as RequestInstance
