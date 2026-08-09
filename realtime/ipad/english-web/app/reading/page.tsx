"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"

const ReadingMaterials = dynamic(
  () => import("@/components/reading/reading-materials").then((m) => ({ default: m.ReadingMaterials })),
  { ssr: false, loading: () => <div className="loading-dot" style={{ margin: "40vh auto" }} /> },
)

export default function ReadingPage() {
  return <AuthGate><ReadingMaterials /></AuthGate>
}
