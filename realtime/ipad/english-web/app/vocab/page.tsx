"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const VocabDashboard = dynamic(
  () =>
    import("@/components/vocab/dashboard").then((m) => ({
      default: m.VocabDashboard,
    })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function VocabPage() {
  return (
    <AuthGate>
      <VocabDashboard />
    </AuthGate>
  )
}
