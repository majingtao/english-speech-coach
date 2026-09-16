"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const VocabWordList = dynamic(
  () =>
    import("@/components/vocab/word-list").then((m) => ({
      default: m.VocabWordList,
    })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function VocabWordListPage() {
  return (
    <AuthGate>
      <VocabWordList />
    </AuthGate>
  )
}
