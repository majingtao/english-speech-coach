"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const DialogueList = dynamic(
  () => import("@/components/expression/expression-dialogue-list").then((module) => ({ default: module.ExpressionDialogueList })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function ExpressionDialoguePage() {
  return <AuthGate><DialogueList /></AuthGate>
}
