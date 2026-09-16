import type { Metadata } from "next"
import { ContactPage } from "@/components/contact/contact-page"

export const metadata: Metadata = {
  title: "联系我们 · English Speech Coach",
}

// 公开页面：未登录也能访问（proxy.ts 已放行 /contact）
export default function Contact() {
  return <ContactPage />
}
