"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const WordFormLab = dynamic(
  () => import("@/components/grammar/word-form-lab").then((module) => ({ default: module.WordFormLab })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function Page() {
  return <AuthGate><WordFormLab module="comparisons" /></AuthGate>
}
