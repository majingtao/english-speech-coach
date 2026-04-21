"use client"

import { useEffect, useState } from "react"
import type { ReactNode } from "react"
import { useAuthStore } from "@/lib/stores/auth-store"
import { buildLoginPath } from "@/lib/auth/redirect"

export function AuthGate({ children }: { children: ReactNode }) {
  const { isHydrated, isLoggedIn, hydrate } = useAuthStore()
  const [loginPath, setLoginPath] = useState("/login")

  useEffect(() => {
    const path = `${window.location.pathname}${window.location.search}`
    setLoginPath(buildLoginPath(path))
  }, [])

  useEffect(() => {
    if (!isHydrated) {
      hydrate()
    }
  }, [hydrate, isHydrated])

  useEffect(() => {
    if (!isHydrated || isLoggedIn) return
    window.location.href = loginPath
  }, [isHydrated, isLoggedIn, loginPath])

  if (!isHydrated || !isLoggedIn) {
    return (
      <main className="mx-auto flex min-h-dvh w-full max-w-6xl items-center justify-center p-6">
        <div className="flex flex-col items-center gap-3 text-sm text-slate-600">
          <div className="loading-dot" />
          <a className="text-blue-700 underline" href={loginPath}>
            正在跳转登录，点这里手动进入
          </a>
        </div>
      </main>
    )
  }

  return <>{children}</>
}
