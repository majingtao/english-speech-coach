"use client"

import { ArrowLeft } from "lucide-react"
import Link from "next/link"

export function DictationContent() {
  return (
    <div className="yle-shell">
      <header className="yle-header">
        <Link href="/" className="yle-back" style={{ textDecoration: "none" }}>
          <ArrowLeft className="size-[18px]" />
          <span>首页</span>
        </Link>
        <h1 className="yle-header-title">听写训练</h1>
        <div className="w-[72px]" />
      </header>

      <div className="flex flex-col items-center justify-center gap-4 py-24 text-slate-400">
        <p className="text-lg font-semibold text-slate-600">听写模块</p>
        <p className="text-sm">即将开放</p>
      </div>
    </div>
  )
}
