"use client"

import { getTokenFromStorage } from "@/lib/auth/token"
import { isQuotaCode, quotaCodeLabel } from "@/lib/api/quota"
import { redirectToLoginWithCurrentPath } from "@/lib/auth/redirect"
import { useAuthStore } from "@/lib/stores/auth-store"

function defaultTenantId(): string {
  return process.env.NEXT_PUBLIC_TENANT_ID || "1"
}

export function pyFetch(
  input: RequestInfo | URL,
  init: RequestInit = {},
): Promise<Response> {
  const headers = new Headers(init.headers || {})
  const token = getTokenFromStorage()
  if (token && !headers.has("Authorization"))
    headers.set("Authorization", `Bearer ${token}`)
  if (!headers.has("tenant-id")) headers.set("tenant-id", defaultTenantId())
  return fetch(input, { ...init, headers })
}

export interface QuotaRejection {
  code: number
  msg: string
  label: string
}

export async function extractQuotaRejection(
  res: Response,
): Promise<QuotaRejection | null> {
  const ct = res.headers.get("content-type") || ""
  if (!ct.toLowerCase().startsWith("application/json")) return null
  const clone = res.clone()
  let body: Record<string, unknown>
  try {
    body = await clone.json()
  } catch {
    return null
  }
  if (!body || typeof body !== "object" || typeof body.code !== "number")
    return null
  if (!isQuotaCode(body.code)) return null
  const label = quotaCodeLabel(body.code, body.msg as string)
  return { code: body.code, msg: (body.msg as string) || label, label }
}

export async function handleQuotaRejection(res: Response): Promise<boolean> {
  const r = await extractQuotaRejection(res)
  if (!r) return false
  return true
}

export async function handleAuthRejection(res: Response): Promise<boolean> {
  let unauthorized = res.status === 401
  if (!unauthorized) {
    const ct = res.headers.get("content-type") || ""
    if (ct.toLowerCase().startsWith("application/json")) {
      try {
        const body = await res.clone().json()
        if (body && typeof body === "object" && body.code === 401)
          unauthorized = true
      } catch {
        /* ignore */
      }
    }
  }
  if (!unauthorized) return false
  useAuthStore.getState().clearToken()
  redirectToLoginWithCurrentPath()
  return true
}
