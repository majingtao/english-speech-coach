"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const HomePage = dynamic(
  () => import("@/components/home/home-page").then((m) => ({ default: m.HomePage })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function Home() {
  return (
    <AuthGate>
      <HomePage />
    </AuthGate>
  )
}
