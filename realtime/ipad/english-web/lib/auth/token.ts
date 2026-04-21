import { TOKEN_COOKIE_KEY, TOKEN_STORAGE_KEY } from "@/lib/auth/constants"

function canUseStorage() {
  return typeof window !== "undefined" && typeof window.localStorage !== "undefined"
}

export function getTokenFromStorage() {
  if (!canUseStorage()) {
    return ""
  }
  try {
    return window.localStorage.getItem(TOKEN_STORAGE_KEY) ?? ""
  } catch {
    return ""
  }
}

export function setTokenToStorage(token: string) {
  if (!canUseStorage()) {
    return
  }
  try {
    window.localStorage.setItem(TOKEN_STORAGE_KEY, token)
    const encoded = encodeURIComponent(token)
    document.cookie = `${TOKEN_COOKIE_KEY}=${encoded}; Path=/; SameSite=Lax`
  } catch {
    // Ignore storage failure in strict/private browser modes.
  }
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
