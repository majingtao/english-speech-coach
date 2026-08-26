"use client"

import { create } from "zustand"
import {
  clearTokenFromStorage,
  getTokenFromStorage,
  setAuthTokenToStorage,
  setTokenToStorage,
} from "@/lib/auth/token"
import type { LoginResult } from "@/lib/api/auth"

type AuthState = {
  token: string
  isHydrated: boolean
  isLoggedIn: boolean
  hydrate: () => void
  setToken: (token: string) => void
  setLoginResult: (result: LoginResult, rememberLogin: boolean) => void
  clearToken: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  token: "",
  isHydrated: false,
  isLoggedIn: false,
  hydrate: () => {
    try {
      const token = getTokenFromStorage()
      if (token) {
        setTokenToStorage(token)
      }
      set({
        token,
        isHydrated: true,
        isLoggedIn: !!token,
      })
    } catch {
      set({
        token: "",
        isHydrated: true,
        isLoggedIn: false,
      })
    }
  },
  setToken: (token) => {
    setTokenToStorage(token)
    set({
      token,
      isLoggedIn: !!token,
      isHydrated: true,
    })
  },
  setLoginResult: (result, rememberLogin) => {
    setAuthTokenToStorage({
      token: result.token,
      refreshToken: result.refreshToken,
      rememberLogin,
      storedAt: Date.now(),
    })
    set({
      token: result.token,
      isLoggedIn: !!result.token,
      isHydrated: true,
    })
  },
  clearToken: () => {
    clearTokenFromStorage()
    set({
      token: "",
      isLoggedIn: false,
      isHydrated: true,
    })
  },
}))
