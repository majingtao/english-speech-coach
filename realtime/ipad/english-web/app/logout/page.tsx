"use client"

import { useEffect } from "react"
import { logout } from "@/lib/api/auth"
import { useAuthStore } from "@/lib/stores/auth-store"

export default function LogoutPage() {
  const { clearToken } = useAuthStore()

  useEffect(() => {
    async function doLogout() {
      try {
        await logout()
      } catch {
        // ignore
      } finally {
        clearToken()
        window.location.href = "/login"
      }
    }
    doLogout()
  }, [clearToken])

  return (
    <main className="flex min-h-dvh items-center justify-center">
      <div className="flex flex-col items-center gap-3 text-sm text-slate-500">
        <div className="loading-dot" />
        <span>正在退出...</span>
      </div>
    </main>
  )
}
