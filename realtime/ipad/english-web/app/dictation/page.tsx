"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const DictationContent = dynamic(
  () => import("@/components/dictation/dictation-content").then((m) => ({ default: m.DictationContent })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function DictationPage() {
  return (
    <AuthGate>
      <DictationContent />
    </AuthGate>
  )
}
