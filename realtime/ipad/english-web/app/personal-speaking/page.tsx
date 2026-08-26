"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const PersonalPracticePage = dynamic(
  () => import("@/components/personal-practice/personal-practice-page").then((module) => ({ default: module.PersonalPracticePage })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function PersonalSpeakingPage() {
  return <AuthGate><PersonalPracticePage type="speaking" /></AuthGate>
}
