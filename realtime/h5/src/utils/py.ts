/**
 * Python 语音代理（/py/*）的统一 fetch 包装：
 * - 自动注入 H5 会员 Bearer token（供 server.py 透传给 yudao /esc/quota/consume）
 * - 自动带上 tenant-id
 * - 提供业务码（配额）检测工具
 *
 * 约定：server.py 在配额拒绝时会返回 HTTP 200 + JSON {code, msg, data}，其它
 * 端点正常走 SSE / audio / json。调用方拿到 Response 后按需判断业务码。
 */
import { showNotify } from 'vant'
import { clearToken, getToken } from '@/utils/auth'
import { isQuotaCode, quotaCodeLabel } from '@/api/quota'
import { redirectToLogin } from '@/utils/loginRedirect'

function defaultTenantId(): string {
  return (import.meta as any).env?.VITE_APP_TENANT_ID || '1'
}

export function pyFetch(input: RequestInfo | URL, init: RequestInit = {}): Promise<Response> {
  const headers = new Headers(init.headers || {})
  const token = getToken()
  if (token && !headers.has('Authorization'))
    headers.set('Authorization', `Bearer ${token}`)
  if (!headers.has('tenant-id'))
    headers.set('tenant-id', defaultTenantId())
  return fetch(input, { ...init, headers })
}

export interface QuotaRejection {
  code: number
  msg: string
  label: string
}

/**
 * 若响应是 yudao 风格的业务码 JSON（且 code 属于配额区间），返回拒绝信息；
 * 否则返回 null。会消费 response body（读 json 或读文本兜底）。
 *
 * 注意：仅当 Content-Type 开头是 application/json 时尝试解析，避免把 audio/wav
 * 的 ArrayBuffer 误读成文本。
 */
export async function extractQuotaRejection(res: Response): Promise<QuotaRejection | null> {
  const ct = res.headers.get('content-type') || ''
  if (!ct.toLowerCase().startsWith('application/json'))
    return null
  const clone = res.clone()
  let body: any
  try {
    body = await clone.json()
  }
  catch {
    return null
  }
  if (!body || typeof body !== 'object' || typeof body.code !== 'number')
    return null
  if (!isQuotaCode(body.code))
    return null
  const label = quotaCodeLabel(body.code, body.msg)
  return { code: body.code, msg: body.msg || label, label }
}

/** 便捷：若是配额拒绝则弹 toast 并返回 true，由调用方中止后续处理 */
export async function handleQuotaRejection(res: Response): Promise<boolean> {
  const r = await extractQuotaRejection(res)
  if (!r)
    return false
  showNotify({ type: 'warning', message: r.label })
  return true
}

/**
 * Python 代理透传的登录失效检测：
 * - HTTP 401 直接判定
 * - 200 + JSON {code: 401} （yudao 透传业务码）
 * 命中则清 token + 跳转登录页，并返回 true 让调用方中止。
 */
export async function handleAuthRejection(res: Response): Promise<boolean> {
  let unauthorized = res.status === 401
  if (!unauthorized) {
    const ct = res.headers.get('content-type') || ''
    if (ct.toLowerCase().startsWith('application/json')) {
      try {
        const body = await res.clone().json()
        if (body && typeof body === 'object' && body.code === 401)
          unauthorized = true
      }
      catch { /* ignore */ }
    }
  }
  if (!unauthorized)
    return false
  clearToken()
  showNotify({ type: 'danger', message: '登录已失效，请重新登录' })
  redirectToLogin()
  return true
}
