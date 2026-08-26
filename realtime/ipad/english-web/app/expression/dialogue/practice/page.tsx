"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const DialoguePractice = dynamic(
  () => import("@/components/expression/expression-dialogue-practice").then((module) => ({ default: module.ExpressionDialoguePractice })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function ExpressionDialoguePracticePage() {
  return <AuthGate><DialoguePractice /></AuthGate>
}
