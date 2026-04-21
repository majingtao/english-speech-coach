"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { useRouter } from "next/navigation"
import {
  BookOpen,
  ChevronDown,
  Headphones,
  Loader2,
  LogOut,
  MessageCircle,
  Mic,
  PenLine,
  RefreshCw,
  Settings,
  Sparkles,
  User,
  Zap,
} from "lucide-react"
import type { QuotaMe } from "@/lib/types/quota"
import { fetchMyQuota } from "@/lib/api/quota"
import { logout } from "@/lib/api/auth"
import { useAuthStore } from "@/lib/stores/auth-store"

interface AppItem {
  key: string
  label: string
  icon: React.ReactNode
  enabled: boolean
  href?: string
}

interface AppSection {
  key: string
  title: string
  subtitle: string
  icon: React.ReactNode
  items: AppItem[]
}

const sections: AppSection[] = [
  {
    key: "speaking",
    title: "口语考试练习",
    subtitle: "Cambridge YLE 官方真题模拟",
    icon: <Mic className="size-5" />,
    items: [
      { key: "yle", label: "YLE 剑桥少儿", icon: <Sparkles className="size-5" />, enabled: true, href: "/speech/yle" },
      { key: "ket", label: "KET", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "pet", label: "PET", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "fce", label: "FCE", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "ielts", label: "IELTS 雅思", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "toefl", label: "TOEFL 托福", icon: <MessageCircle className="size-5" />, enabled: false },
    ],
  },
  {
    key: "free-chat",
    title: "自由对话",
    subtitle: "按级别与 AI 自由口语练习",
    icon: <Headphones className="size-5" />,
    items: [
      { key: "chat-flyers", label: "Flyers", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "chat-ket", label: "KET", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "chat-pet", label: "PET", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "chat-fce", label: "FCE", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "chat-ielts", label: "IELTS 雅思", icon: <MessageCircle className="size-5" />, enabled: false },
      { key: "chat-toefl", label: "TOEFL 托福", icon: <MessageCircle className="size-5" />, enabled: false },
    ],
  },
  {
    key: "dictation",
    title: "听写训练",
    subtitle: "按教材进行听写练习",
    icon: <PenLine className="size-5" />,
    items: [
      { key: "dict-primary", label: "小学", icon: <BookOpen className="size-5" />, enabled: true, href: "/dictation" },
      { key: "dict-junior", label: "初中", icon: <BookOpen className="size-5" />, enabled: false },
      { key: "dict-senior", label: "高中", icon: <BookOpen className="size-5" />, enabled: false },
      { key: "dict-flyers", label: "Flyers", icon: <BookOpen className="size-5" />, enabled: false },
      { key: "dict-ket", label: "KET", icon: <BookOpen className="size-5" />, enabled: false },
    ],
  },
]

function QuotaBar({ label, used, total, unit }: { label: string; used: number; total: number; unit: string }) {
  const pct = total > 0 ? Math.min(100, Math.round((used / total) * 100)) : 0
  const remaining = total - used
  const critical = pct >= 90

  return (
    <div className="flex flex-col gap-1.5">
      <div className="flex items-center justify-between text-[13px]">
        <span className="text-slate-600 font-medium">{label}</span>
        <span className="text-slate-800 tabular-nums font-semibold">
          {used} / {total} {unit}
        </span>
      </div>
      <div className="h-2 rounded-full bg-slate-100 overflow-hidden">
        <div
          className={`h-full rounded-full transition-all duration-500 ${critical ? "bg-red-500" : "bg-blue-500"}`}
          style={{ width: `${pct}%` }}
        />
      </div>
      <span className="text-[11px] text-slate-400">
        剩余 {remaining} {unit}
      </span>
    </div>
  )
}

export function HomePage() {
  const router = useRouter()
  const { clearToken } = useAuthStore()

  const [userMenuOpen, setUserMenuOpen] = useState(false)
  const [quotaOpen, setQuotaOpen] = useState(false)
  const [quota, setQuota] = useState<QuotaMe | null>(null)
  const [quotaLoading, setQuotaLoading] = useState(false)
  const [quotaError, setQuotaError] = useState("")

  const loadQuota = useCallback(async () => {
    setQuotaLoading(true)
    setQuotaError("")
    try {
      setQuota(await fetchMyQuota())
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "额度加载失败"
      setQuotaError(msg)
    } finally {
      setQuotaLoading(false)
    }
  }, [])

  useEffect(() => {
    loadQuota()
  }, [loadQuota])

  async function onLogout() {
    try {
      await logout()
    } catch {
      // ignore
    } finally {
      clearToken()
      window.location.href = "/login"
    }
  }

  const quotaRows = useMemo(() => {
    if (!quota) return []
    return [
      { key: "llm", label: "对话 (LLM)", used: quota.llmUsed, total: quota.llmDaily, remaining: quota.llmRemaining, unit: "次" },
      { key: "asr", label: "语音识别 (ASR)", used: quota.asrUsedSec, total: quota.asrDailySec, remaining: quota.asrRemainingSec, unit: "秒" },
      { key: "tts", label: "语音合成 (TTS)", used: quota.ttsUsedChars, total: quota.ttsDailyChars, remaining: quota.ttsRemainingChars, unit: "字符" },
    ]
  }, [quota])

  return (
    <main className="home-shell">
      {/* Header */}
      <header className="home-header">
        <div className="home-header-inner">
          <h1 className="home-logo">English Speech Coach</h1>
          <div className="home-header-actions">
            <button
              type="button"
              className="home-icon-btn"
              onClick={() => { setQuotaOpen(true); if (!quota) loadQuota() }}
              title="今日额度"
            >
              <Zap className="size-[18px]" />
            </button>
            <div className="home-user-menu-wrap">
              <button
                type="button"
                className="home-user-btn"
                onClick={() => setUserMenuOpen((v) => !v)}
              >
                <div className="home-user-avatar">
                  <User className="size-4 text-blue-600" />
                </div>
                <ChevronDown className={`size-3.5 text-slate-400 transition-transform ${userMenuOpen ? "rotate-180" : ""}`} />
              </button>
              {userMenuOpen && (
                <>
                  <div className="home-menu-backdrop" onClick={() => setUserMenuOpen(false)} />
                  <div className="home-menu-dropdown">
                    <button type="button" className="home-menu-item" onClick={() => { setUserMenuOpen(false); setQuotaOpen(true); if (!quota) loadQuota() }}>
                      <Zap className="size-4" /> 今日额度
                    </button>
                    <button type="button" className="home-menu-item" onClick={() => { setUserMenuOpen(false) }}>
                      <Settings className="size-4" /> 设置
                    </button>
                    <div className="home-menu-divider" />
                    <button type="button" className="home-menu-item home-menu-danger" onClick={() => { setUserMenuOpen(false); onLogout() }}>
                      <LogOut className="size-4" /> 退出登录
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Hero */}
      <section className="home-hero">
        <div className="home-hero-deco" />
        <div className="home-hero-content">
          <p className="home-hero-badge">
            <Sparkles className="size-3.5" />
            AI-Powered
          </p>
          <h2 className="home-hero-title">Speak Better,<br />Score Higher</h2>
          <p className="home-hero-subtitle">选择下方应用模块，开始你的英语口语训练之旅</p>
        </div>
      </section>

      {/* Sections */}
      <div className="home-sections">
        {sections.map((section) => (
          <section key={section.key} className="home-section">
            <div className="home-section-head">
              <div className="home-section-icon">{section.icon}</div>
              <div>
                <h3 className="home-section-title">{section.title}</h3>
                <p className="home-section-subtitle">{section.subtitle}</p>
              </div>
            </div>
            <div className="home-app-grid">
              {section.items.map((item) => {
                const Tag = item.enabled ? "button" : "div"
                return (
                  <Tag
                    key={item.key}
                    className={`home-app-card ${item.enabled ? "home-app-enabled" : "home-app-disabled"}`}
                    onClick={item.enabled && item.href ? () => router.push(item.href!) : undefined}
                    type={item.enabled ? "button" : undefined}
                  >
                    <div className={`home-app-icon ${item.enabled ? "home-app-icon-active" : ""}`}>
                      {item.icon}
                    </div>
                    <span className="home-app-label">{item.label}</span>
                    {!item.enabled && <span className="home-app-badge">即将开放</span>}
                  </Tag>
                )
              })}
            </div>
          </section>
        ))}
      </div>

      {/* Quota Sheet */}
      {quotaOpen && (
        <>
          <div className="home-overlay" onClick={() => setQuotaOpen(false)} />
          <div className="home-sheet">
            <div className="home-sheet-handle" />
            <div className="home-sheet-header">
              <h3 className="text-lg font-bold text-slate-800">今日额度</h3>
              <button
                type="button"
                className="home-sheet-refresh"
                disabled={quotaLoading}
                onClick={loadQuota}
              >
                <RefreshCw className={`size-3.5 ${quotaLoading ? "animate-spin" : ""}`} />
                {quotaLoading ? "加载中" : "刷新"}
              </button>
            </div>
            {quota && quota.enabled === false ? (
              <p className="text-red-500 text-sm text-center py-4">账号已被冻结，请联系管理员</p>
            ) : quotaError ? (
              <p className="text-red-500 text-sm py-2">{quotaError}</p>
            ) : quota ? (
              <div className="flex flex-col gap-4 mt-4">
                {quotaRows.map((row) => (
                  <QuotaBar key={row.key} label={row.label} used={row.used} total={row.total} unit={row.unit} />
                ))}
              </div>
            ) : (
              <div className="flex items-center justify-center py-8 text-slate-400 text-sm gap-2">
                <Loader2 className="size-4 animate-spin" /> 加载中...
              </div>
            )}
          </div>
        </>
      )}
    </main>
  )
}
