/**
 * 跳转到登录页（带 redirect 参数）。
 * 提取到独立模块是为了避免 request.ts 直接 import 'router'，
 * 防止 utils → router → pages → utils 的循环依赖。
 */
let redirecting = false

export function redirectToLogin(): void {
  if (redirecting)
    return
  redirecting = true
  // 给 toast/notify 一点显示时间，并避免在路由跳转中再次触发
  setTimeout(() => {
    try {
      const cur = window.location.pathname + window.location.search + window.location.hash
      if (cur.startsWith('/login')) {
        redirecting = false
        return
      }
      const redirect = encodeURIComponent(cur || '/')
      window.location.replace(`/login?redirect=${redirect}`)
    }
    finally {
      // 由于 location.replace 会刷新页面，这里 finally 实际上不会执行；
      // 兜底 reset，以防被某些环境（如 SSR/测试）拦截。
      setTimeout(() => { redirecting = false }, 1000)
    }
  }, 300)
}
