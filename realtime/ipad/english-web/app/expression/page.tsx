"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const ExpressionThemes = dynamic(
  () => import("@/components/expression/expression-themes").then((m) => ({ default: m.ExpressionThemes })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function ExpressionPage() {
  return <AuthGate><ExpressionThemes /></AuthGate>
}
