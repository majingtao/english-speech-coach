"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const KetWritingPractice = dynamic(
  () => import("@/components/writing/ket-writing-practice").then((m) => ({ default: m.KetWritingPractice })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function KetWritingPage() {
  return <AuthGate><KetWritingPractice /></AuthGate>
}
