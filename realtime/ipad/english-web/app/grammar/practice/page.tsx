"use client"
import dynamic from "next/dynamic"
import { AuthGate } from "@/components/auth/auth-gate"
const GrammarPractice = dynamic(() => import("@/components/grammar/grammar-practice").then((m) => ({ default: m.GrammarPractice })), { ssr: false })
export default function Page() { return <AuthGate><GrammarPractice /></AuthGate> }
