"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const VocabFlashcardSession = dynamic(
  () =>
    import("@/components/vocab/flashcard").then((m) => ({
      default: m.VocabFlashcardSession,
    })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function VocabReviewPage() {
  return (
    <AuthGate>
      <VocabFlashcardSession />
    </AuthGate>
  )
}
