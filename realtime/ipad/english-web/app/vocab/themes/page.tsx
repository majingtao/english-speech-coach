"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const VocabThemeGrid = dynamic(
  () =>
    import("@/components/vocab/theme-grid").then((m) => ({
      default: m.VocabThemeGrid,
    })),
  {
    ssr: false,
    loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} />,
  },
)

export default function VocabThemesPage() {
  return (
    <AuthGate>
      <VocabThemeGrid />
    </AuthGate>
  )
}
