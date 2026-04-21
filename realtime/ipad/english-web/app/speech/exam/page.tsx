"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const ExamPage = dynamic(
  () => import("@/components/speech/exam-page").then((m) => ({ default: m.ExamPage })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function ExamRoute() {
  return (
    <AuthGate>
      <ExamPage />
    </AuthGate>
  )
}
