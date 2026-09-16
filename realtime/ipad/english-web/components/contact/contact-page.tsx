"use client"

import { useState } from "react"
import type { ReactNode } from "react"
import { useRouter } from "next/navigation"
import {
  ArrowLeft,
  AtSign,
  Check,
  Copy,
  ExternalLink,
  Mail,
  MessageCircle,
  MessagesSquare,
  QrCode,
  Send,
} from "lucide-react"
import { CONTACT } from "@/lib/contact"

async function copyText(text: string) {
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
      return true
    }
  } catch {
    // 走下面的兼容写法
  }
  // 局域网 http 访问时 clipboard API 不可用，退回 execCommand
  const ta = document.createElement("textarea")
  ta.value = text
  ta.setAttribute("readonly", "")
  ta.style.position = "fixed"
  ta.style.opacity = "0"
  document.body.appendChild(ta)
  ta.select()
  let ok = false
  try {
    ok = document.execCommand("copy")
  } catch {
    ok = false
  }
  document.body.removeChild(ta)
  return ok
}

function QrImage({ src, label }: { src: string; label: string }) {
  const [failed, setFailed] = useState(false)
  if (!src || failed) {
    return (
      <div className="ct-qr ct-qr-empty" aria-label={`${label}（待上传）`}>
        <QrCode className="size-8" />
        <span>二维码待上传</span>
      </div>
    )
  }
  return (
    // eslint-disable-next-line @next/next/no-img-element
    <img className="ct-qr" src={src} alt={label} onError={() => setFailed(true)} />
  )
}

function ContactCard({
  icon,
  tone,
  title,
  subtitle,
  children,
}: {
  icon: ReactNode
  tone: "wechat" | "x" | "telegram" | "more"
  title: string
  subtitle?: string
  children: ReactNode
}) {
  return (
    <section className={`ct-card ct-card-${tone}`}>
      <div className="ct-card-head">
        <span className="ct-card-icon">{icon}</span>
        <div>
          <h2 className="ct-card-title">{title}</h2>
          {subtitle && <p className="ct-card-subtitle">{subtitle}</p>}
        </div>
      </div>
      {children}
    </section>
  )
}

export function ContactPage() {
  const router = useRouter()
  const [copied, setCopied] = useState(false)
  const { wechat, x, telegram, email, discordUrl, whatsappUrl } = CONTACT
  const hasMore = Boolean(email || discordUrl || whatsappUrl)

  async function onCopyWechat() {
    const ok = await copyText(wechat.id)
    setCopied(ok)
    if (ok) window.setTimeout(() => setCopied(false), 2000)
  }

  function goBack() {
    if (window.history.length > 1) router.back()
    else router.push("/")
  }

  return (
    <main className="ct-shell">
      <header className="vocab-header">
        <button type="button" className="vocab-back" onClick={goBack}>
          <ArrowLeft className="size-[18px]" />
          <span>返回</span>
        </button>
        <h1 className="vocab-header-title">联系我们</h1>
        <span className="ct-header-spacer" />
      </header>

      <div className="ct-body">
        <p className="ct-intro">
          使用中遇到问题、有功能建议，或想加入家长交流群，都可以通过下面的方式找到我们。
        </p>

        <div className="ct-grid">
          <ContactCard
            icon={<AtSign className="size-5" />}
            tone="x"
            title="X（Twitter）"
            subtitle="产品动态与更新"
          >
            <div className="ct-row">
              <span className="ct-row-label">账号</span>
              <span className="ct-row-value">@{x.handle}</span>
            </div>
            <a
              className="ct-link-btn"
              href={`https://x.com/${x.handle}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              在 X 上关注
              <ExternalLink className="size-4" />
            </a>
          </ContactCard>

          <ContactCard
            icon={<MessageCircle className="size-5" />}
            tone="wechat"
            title="微信"
            subtitle="国内用户推荐"
          >
            <div className="ct-row">
              <span className="ct-row-label">微信号</span>
              <span className="ct-row-value">{wechat.id}</span>
              <button type="button" className="ct-copy" onClick={onCopyWechat}>
                {copied ? <Check className="size-4" /> : <Copy className="size-4" />}
                {copied ? "已复制" : "复制"}
              </button>
            </div>
            <p className="ct-tip">添加好友时请备注“英语学习”</p>
            <div className="ct-qr-list">
              <figure className="ct-qr-item">
                <QrImage src={wechat.groupQr} label={`微信群「${wechat.groupName}」二维码`} />
                <figcaption>微信群「{wechat.groupName}」</figcaption>
              </figure>
              {wechat.personalQr && (
                <figure className="ct-qr-item">
                  <QrImage src={wechat.personalQr} label="个人微信二维码" />
                  <figcaption>扫码加我微信</figcaption>
                </figure>
              )}
            </div>
            <p className="ct-tip">
              群二维码每 7 天更新一次。扫码失效时，请加微信 {wechat.id}（备注“英语学习”），我拉你进群。
            </p>
          </ContactCard>

          <ContactCard
            icon={<Send className="size-5" />}
            tone="telegram"
            title="Telegram 群"
            subtitle="海外用户推荐"
          >
            <div className="ct-qr-list">
              <figure className="ct-qr-item">
                <QrImage src={telegram.groupQr} label={`Telegram 群「${telegram.groupName}」二维码`} />
                <figcaption>Telegram 群「{telegram.groupName}」</figcaption>
              </figure>
            </div>
            {telegram.groupUrl && (
              <a className="ct-link-btn" href={telegram.groupUrl} target="_blank" rel="noopener noreferrer">
                打开 Telegram 加入群组
                <ExternalLink className="size-4" />
              </a>
            )}
            {telegram.groupUrl && <p className="ct-tip">在手机上浏览时，直接点上面的按钮即可加入。</p>}
          </ContactCard>

          {hasMore && (
            <ContactCard
              icon={<MessagesSquare className="size-5" />}
              tone="more"
              title="邮箱及其他"
              subtitle={email ? "海外用户、合作洽谈欢迎发邮件" : undefined}
            >
              {email && (
                <a className="ct-row ct-row-link" href={`mailto:${email}`}>
                  <Mail className="size-4" />
                  <span className="ct-row-value">{email}</span>
                </a>
              )}
              {whatsappUrl && (
                <a className="ct-row ct-row-link" href={whatsappUrl} target="_blank" rel="noopener noreferrer">
                  <MessageCircle className="size-4" />
                  <span className="ct-row-value">WhatsApp</span>
                  <ExternalLink className="size-4 ct-row-ext" />
                </a>
              )}
              {discordUrl && (
                <a className="ct-row ct-row-link" href={discordUrl} target="_blank" rel="noopener noreferrer">
                  <MessagesSquare className="size-4" />
                  <span className="ct-row-value">Discord 社区</span>
                  <ExternalLink className="size-4 ct-row-ext" />
                </a>
              )}
            </ContactCard>
          )}
        </div>
      </div>
    </main>
  )
}
