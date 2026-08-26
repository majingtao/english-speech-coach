"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const SynonymPractice = dynamic(
  () =>
    import("@/components/synonyms/synonym-practice").then((m) => ({
      default: m.SynonymPractice,
    })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function SynonymsPage() {
  return (
    <AuthGate>
      <SynonymPractice />
    </AuthGate>
  )
}
