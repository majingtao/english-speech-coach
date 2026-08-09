"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const KetSelector = dynamic(
  () => import("@/components/speech/ket-selector").then((m) => ({ default: m.KetSelector })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function KetPage() {
  return (
    <AuthGate>
      <KetSelector />
    </AuthGate>
  )
}
