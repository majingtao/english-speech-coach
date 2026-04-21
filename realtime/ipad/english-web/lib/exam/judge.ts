import { endpoints } from "./endpoints"
import { pyFetch, handleAuthRejection, handleQuotaRejection } from "@/lib/api/py"
import type { LlmModel } from "@/lib/types/speech"

export interface JudgeResult {
  ok: boolean
  fb?: string
  cn?: string
  ans?: string
  timeout?: boolean
  authRejected?: boolean
  quotaRejected?: boolean
}

export async function callJudge(
  question: string,
  expected: string,
  student: string,
  llm: LlmModel | null,
  useProxy: boolean,
): Promise<JudgeResult> {
  const ctrl = new AbortController()
  const timer = setTimeout(() => ctrl.abort(), 45000)
  try {
    const res = await pyFetch(endpoints.judge(), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        provider: llm?.provider,
        model: llm?.model,
        use_proxy: useProxy,
        question,
        expected,
        student,
      }),
      signal: ctrl.signal,
    })
    clearTimeout(timer)
    if (await handleAuthRejection(res))
      return { ok: false, authRejected: true, fb: "登录已失效", ans: expected }
    if (!res.ok)
      return { ok: false, fb: `Server error ${res.status}`, ans: expected }
    if (await handleQuotaRejection(res))
      return { ok: false, quotaRejected: true, fb: "配额受限", ans: expected }
    return await res.json()
  } catch (error: unknown) {
    clearTimeout(timer)
    if (error instanceof Error && error.name === "AbortError")
      return { ok: false, timeout: true, fb: "判题超时", ans: expected }
    return { ok: false, fb: error instanceof Error ? error.message : "Judge error", ans: expected }
  }
}
