import type { NextRequest } from "next/server"
import { NextResponse } from "next/server"
import { TOKEN_COOKIE_KEY } from "@/lib/auth/constants"

function isPublicPath(pathname: string) {
  if (pathname === "/login") return true
  if (pathname === "/login-clean") return true
  if (pathname.startsWith("/_next")) return true
  if (pathname.startsWith("/favicon")) return true
  return false
}

export function middleware(request: NextRequest) {
  const { pathname, search } = request.nextUrl
  if (isPublicPath(pathname)) {
    // Always allow visiting login pages to avoid redirect loops caused by stale cookies.
    if (pathname === "/login-clean") {
      const to = request.nextUrl.clone()
      to.pathname = "/login"
      to.search = ""
      const res = NextResponse.redirect(to)
      res.cookies.delete(TOKEN_COOKIE_KEY)
      return res
    }
    return NextResponse.next()
  }

  const token = request.cookies.get(TOKEN_COOKIE_KEY)?.value
  if (!token) {
    const to = request.nextUrl.clone()
    to.pathname = "/login"
    const redirect = `${pathname}${search}`
    to.search = `?redirect=${encodeURIComponent(redirect)}`
    return NextResponse.redirect(to)
  }

  return NextResponse.next()
}

export const config = {
  matcher: ["/((?!api|py|.*\\..*).*)"],
}
