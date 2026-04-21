"use client"

import { create } from "zustand"
import {
  clearTokenFromStorage,
  getTokenFromStorage,
  setTokenToStorage,
} from "@/lib/auth/token"

type AuthState = {
  token: string
  isHydrated: boolean
  isLoggedIn: boolean
  hydrate: () => void
  setToken: (token: string) => void
  clearToken: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  token: "",
  isHydrated: false,
  isLoggedIn: false,
  hydrate: () => {
    try {
      const token = getTokenFromStorage()
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
  clearToken: () => {
    clearTokenFromStorage()
    set({
      token: "",
      isLoggedIn: false,
      isHydrated: true,
    })
  },
}))
