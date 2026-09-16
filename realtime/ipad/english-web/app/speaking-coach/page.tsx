"use client"

import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"
const SpeakingCoach = dynamic(() => import("@/components/speaking-coach/speaking-coach").then(m => m.SpeakingCoach), { ssr: false, loading: () => <p className="p-10 text-center">正在准备口语练习…</p> })
export default function Page() { return <AuthGate><SpeakingCoach /></AuthGate> }
