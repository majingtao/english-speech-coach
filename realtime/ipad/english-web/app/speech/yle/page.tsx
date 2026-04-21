"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const YleSelector = dynamic(
  () => import("@/components/speech/yle-selector").then((m) => ({ default: m.YleSelector })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function YlePage() {
  return (
    <AuthGate>
      <YleSelector />
    </AuthGate>
  )
}
