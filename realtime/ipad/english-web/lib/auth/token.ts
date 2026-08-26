import {
  DEFAULT_LOGIN_DAYS,
  REMEMBER_LOGIN_DAYS,
  TOKEN_COOKIE_KEY,
  TOKEN_STORAGE_KEY,
} from "@/lib/auth/constants"

export type StoredAuthToken = {
  token: string
  refreshToken: string
  rememberLogin: boolean
  storedAt: number
}

function canUseStorage() {
  return typeof window !== "undefined" && typeof window.localStorage !== "undefined"
}

const REMEMBER_LOGIN_MAX_AGE_SECONDS = 60 * 60 * 24 * REMEMBER_LOGIN_DAYS
const DEFAULT_LOGIN_MAX_AGE_SECONDS = 60 * 60 * 24 * DEFAULT_LOGIN_DAYS

function setTokenCookie(token: string, rememberLogin: boolean) {
  const encoded = encodeURIComponent(token)
  const maxAge = rememberLogin ? REMEMBER_LOGIN_MAX_AGE_SECONDS : DEFAULT_LOGIN_MAX_AGE_SECONDS
  document.cookie = `${TOKEN_COOKIE_KEY}=${encoded}; Path=/; Max-Age=${maxAge}; SameSite=Lax`
}

function parseStoredAuthToken(value: string | null): StoredAuthToken | null {
  if (!value) {
    return null
  }
  try {
    const parsed = JSON.parse(value) as Partial<StoredAuthToken>
    if (typeof parsed.token !== "string" || !parsed.token) {
      return null
    }
    return {
      token: parsed.token,
      refreshToken: typeof parsed.refreshToken === "string" ? parsed.refreshToken : "",
      rememberLogin: Boolean(parsed.rememberLogin),
      storedAt: typeof parsed.storedAt === "number" ? parsed.storedAt : Date.now(),
    }
  } catch {
    return {
      token: value,
      refreshToken: "",
      rememberLogin: false,
      storedAt: Date.now(),
    }
  }
}

export function getAuthTokenFromStorage() {
  if (!canUseStorage()) {
    return null
  }
  try {
    return parseStoredAuthToken(window.localStorage.getItem(TOKEN_STORAGE_KEY))
  } catch {
    return null
  }
}

export function getTokenFromStorage() {
  return getAuthTokenFromStorage()?.token ?? ""
}

export function getRefreshTokenFromStorage() {
  return getAuthTokenFromStorage()?.refreshToken ?? ""
}

export function setAuthTokenToStorage(auth: StoredAuthToken) {
  if (!canUseStorage()) {
    return
  }
  try {
    window.localStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify(auth))
    setTokenCookie(auth.token, auth.rememberLogin)
  } catch {
    // Ignore storage failure in strict/private browser modes.
  }
}

export function updateTokenInStorage(token: string, refreshToken: string) {
  if (!canUseStorage()) {
    return
  }
  try {
    const current = getAuthTokenFromStorage()
    setAuthTokenToStorage({
      token,
      refreshToken,
      rememberLogin: current?.rememberLogin ?? true,
      storedAt: current?.storedAt ?? Date.now(),
    })
  } catch {
    // Ignore storage failure in strict/private browser modes.
  }
}

export function setTokenToStorage(token: string) {
  setAuthTokenToStorage({
    token,
    refreshToken: "",
    rememberLogin: false,
    storedAt: Date.now(),
  })
}

export function isRememberLoginActive() {
  const auth = getAuthTokenFromStorage()
  if (!auth?.rememberLogin) {
    return false
  }
  if (Date.now() - auth.storedAt > REMEMBER_LOGIN_MAX_AGE_SECONDS * 1000) {
    clearTokenFromStorage()
    return false
  }
  return true
}

export function clearTokenFromStorage() {
  if (!canUseStorage()) {
    return
  }
  try {
    window.localStorage.removeItem(TOKEN_STORAGE_KEY)
    document.cookie = `${TOKEN_COOKIE_KEY}=; Path=/; Max-Age=0; SameSite=Lax`
  } catch {
    // Ignore storage failure in strict/private browser modes.
  }
}
