import { createRouter, createWebHistory } from 'vue-router'
import { handleHotUpdate, routes } from 'vue-router/auto-routes'

import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { isLogin } from '@/utils/auth'

NProgress.configure({ showSpinner: true, parent: '#app' })

const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_APP_PUBLIC_PATH),
  routes,
})

// This will update routes at runtime without reloading the page
if (import.meta.hot)
  handleHotUpdate(router)

router.beforeEach((to) => {
  NProgress.start()
  const publicPages = ['/login', '/promo']
  if (!isLogin() && !publicPages.includes(to.path)) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (isLogin() && to.path === '/login') {
    return typeof to.query.redirect === 'string' ? to.query.redirect : '/'
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
