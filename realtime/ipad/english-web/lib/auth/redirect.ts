export function buildLoginPath(redirect?: string) {
  if (!redirect) {
    return "/login"
  }
  const params = new URLSearchParams({ redirect })
  return `/login?${params.toString()}`
}

export function redirectToLoginWithCurrentPath() {
  if (typeof window === "undefined") {
    return
  }
  const path = `${window.location.pathname}${window.location.search}`
  window.location.href = buildLoginPath(path)
}

