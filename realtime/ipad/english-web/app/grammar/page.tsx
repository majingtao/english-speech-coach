"use client"
import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"
const GrammarTopics = dynamic(() => import("@/components/grammar/grammar-topics").then((m) => ({ default: m.GrammarTopics })), { ssr: false })
export default function Page() { return <AuthGate><GrammarTopics /></AuthGate> }
