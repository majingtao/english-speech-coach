"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const VocabWordbook = dynamic(
  () =>
    import("@/components/vocab/wordbook").then((m) => ({
      default: m.VocabWordbook,
    })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function VocabWordbookPage() {
  return (
    <AuthGate>
      <VocabWordbook />
    </AuthGate>
  )
}
