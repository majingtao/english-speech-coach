"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const ExpressionPractice = dynamic(
  () => import("@/components/expression/expression-practice").then((m) => ({ default: m.ExpressionPractice })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function ExpressionPracticePage() {
  return <AuthGate><ExpressionPractice /></AuthGate>
}
