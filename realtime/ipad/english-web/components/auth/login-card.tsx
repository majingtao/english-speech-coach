"use client"

import { useEffect, useState } from "react"
import type { InputHTMLAttributes, ReactNode } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { BookOpen, Check, Eye, EyeOff, Loader2, Mail, MessageSquareMore, Mic, Sparkles } from "lucide-react"
import { z } from "zod"
import {
  checkEmail,
  checkMobile,
  type LoginResult,
  loginByEmail,
  loginBySms,
  registerByEmail,
  sendEmailCode,
  sendSmsCode,
} from "@/lib/api/auth"
import { useAuthStore } from "@/lib/stores/auth-store"
import { getErrorMessage } from "@/lib/utils/error"
import { REMEMBER_LOGIN_DAYS } from "@/lib/auth/constants"

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const mobilePattern = /^1[3-9]\d{9}$/

const emailLoginSchema = z.object({
  email: z.string().regex(emailPattern, "请输入正确的邮箱"),
  password: z.string().min(1, "请输入密码"),
})

const emailRegisterSchema = z.object({
  email: z.string().regex(emailPattern, "请输入正确的邮箱"),
  code: z.string().min(4, "请输入验证码"),
  password: z.string().min(6, "密码至少 6 位"),
})

const smsSchema = z.object({
  mobile: z.string().regex(mobilePattern, "请输入中国大陆手机号"),
  code: z.string().min(4, "请输入验证码"),
})

type EmailLoginForm = z.infer<typeof emailLoginSchema>
type EmailRegisterForm = z.infer<typeof emailRegisterSchema>
type SmsForm = z.infer<typeof smsSchema>

type Mode = "login" | "register"
type Channel = "email" | "sms"

const channelMap: Record<Channel, { label: string; icon: ReactNode }> = {
  email: { label: "邮箱", icon: <Mail className="size-4" /> },
  sms: { label: "手机", icon: <MessageSquareMore className="size-4" /> },
}

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string
  error?: string
  action?: React.ReactNode
}

function FormInput({ label, error, action, ...props }: InputProps) {
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      <span className="field-control">
        <input className="field-input" {...props} />
        {action}
      </span>
      {error ? <span className="field-error">{error}</span> : null}
    </label>
  )
}

type LoginCardProps = {
  initialMode: Mode
  initialChannel: Channel
  initialRedirect: string
}

export function LoginCard({
  initialMode,
  initialChannel,
  initialRedirect,
}: LoginCardProps) {
  const { setLoginResult, hydrate, isHydrated, isLoggedIn } = useAuthStore()

  const [mode, setMode] = useState<Mode>(initialMode)
  const [channel, setChannel] = useState<Channel>(initialChannel)
  const [emailCountdown, setEmailCountdown] = useState(0)
  const [smsCountdown, setSmsCountdown] = useState(0)
  const [submitting, setSubmitting] = useState(false)
  const [status, setStatus] = useState<{ type: "error" | "success"; message: string } | null>(null)
  const [redirect] = useState(initialRedirect)
  const [showPassword, setShowPassword] = useState<Record<string, boolean>>({})
  const [rememberLogin, setRememberLogin] = useState(true)

  function switchMode(next: Mode) {
    setMode(next)
    setStatus(null)
  }

  function switchChannel(next: Channel) {
    setChannel(next)
    setStatus(null)
  }

  function togglePassword(key: string) {
    setShowPassword((current) => ({
      ...current,
      [key]: !current[key],
    }))
  }

  useEffect(() => {
    const params = new URLSearchParams()
    params.set("mode", mode)
    params.set("channel", channel)
    params.set("redirect", redirect)
    window.history.replaceState(null, "", `/login?${params.toString()}`)
  }, [channel, mode, redirect])

  useEffect(() => {
    if (!isHydrated) {
      hydrate()
      return
    }
    if (isLoggedIn) {
      window.location.href = redirect
    }
  }, [hydrate, isHydrated, isLoggedIn, redirect])

  useEffect(() => {
    if (emailCountdown <= 0) {
      return
    }
    const timer = setInterval(() => {
      setEmailCountdown((v) => (v > 0 ? v - 1 : 0))
    }, 1000)
    return () => clearInterval(timer)
  }, [emailCountdown])

  useEffect(() => {
    if (smsCountdown <= 0) {
      return
    }
    const timer = setInterval(() => {
      setSmsCountdown((v) => (v > 0 ? v - 1 : 0))
    }, 1000)
    return () => clearInterval(timer)
  }, [smsCountdown])

  const emailLoginForm = useForm<EmailLoginForm>({
    resolver: zodResolver(emailLoginSchema),
    defaultValues: { email: "", password: "" },
  })
  const emailRegisterForm = useForm<EmailRegisterForm>({
    resolver: zodResolver(emailRegisterSchema),
    defaultValues: { email: "", code: "", password: "" },
  })
  const smsLoginForm = useForm<SmsForm>({
    resolver: zodResolver(smsSchema),
    defaultValues: { mobile: "", code: "" },
  })
  const smsRegisterForm = useForm<SmsForm>({
    resolver: zodResolver(smsSchema),
    defaultValues: { mobile: "", code: "" },
  })

  async function handleAuthSuccess(result: LoginResult, successText: string) {
    setLoginResult(result, rememberLogin)
    setStatus({ type: "success", message: successText })
    window.location.href = redirect
  }

  async function handleSubmit(action: () => Promise<void>) {
    try {
      setSubmitting(true)
      setStatus(null)
      await action()
    } catch (error) {
      setStatus({ type: "error", message: getErrorMessage(error) })
    } finally {
      setSubmitting(false)
    }
  }

  async function submitCurrentForm() {
    if (mode === "login" && channel === "email") {
      await emailLoginForm.handleSubmit(async (values) => {
        await handleSubmit(async () => {
          const result = await loginByEmail(values)
          await handleAuthSuccess(result, "登录成功")
        })
      })()
      return
    }

    if (mode === "register" && channel === "email") {
      await emailRegisterForm.handleSubmit(async (values) => {
        await handleSubmit(async () => {
          const result = await registerByEmail(values)
          await handleAuthSuccess(result, "注册成功")
        })
      })()
      return
    }

    if (mode === "login" && channel === "sms") {
      await smsLoginForm.handleSubmit(async (values) => {
        await handleSubmit(async () => {
          const result = await loginBySms(values)
          await handleAuthSuccess(result, "登录成功")
        })
      })()
      return
    }

    await smsRegisterForm.handleSubmit(async (values) => {
      await handleSubmit(async () => {
        const result = await loginBySms(values)
        await handleAuthSuccess(result, "注册成功")
      })
    })()
  }

  async function onSendEmailCode() {
    const email = emailRegisterForm.getValues("email")
    if (!emailPattern.test(email)) {
      setStatus({ type: "error", message: "请输入正确的邮箱" })
      return
    }
    if (emailCountdown > 0) {
      return
    }
    setEmailCountdown(60)
    try {
      const exists = await checkEmail(email)
      if (exists) {
        setEmailCountdown(0)
        setStatus({ type: "error", message: "该邮箱已注册，请直接登录" })
        return
      }
      await sendEmailCode({ email, scene: "register" })
      setStatus({ type: "success", message: "验证码已发送" })
    } catch (error) {
      setEmailCountdown(0)
      setStatus({ type: "error", message: getErrorMessage(error, "发送失败") })
    }
  }

  async function onSendSmsCode() {
    const activeForm = mode === "register" ? smsRegisterForm : smsLoginForm
    const mobile = activeForm.getValues("mobile")
    if (!mobilePattern.test(mobile)) {
      setStatus({ type: "error", message: "请输入正确的手机号" })
      return
    }
    if (smsCountdown > 0) {
      return
    }
    setSmsCountdown(60)
    try {
      if (mode === "register") {
        const exists = await checkMobile(mobile)
        if (exists) {
          setSmsCountdown(0)
          setStatus({ type: "error", message: "该手机号已注册，请直接登录" })
          return
        }
      }
      await sendSmsCode({ mobile, scene: 1 })
      setStatus({ type: "success", message: "验证码已发送" })
    } catch (error) {
      setSmsCountdown(0)
      setStatus({ type: "error", message: getErrorMessage(error, "发送失败") })
    }
  }

  return (
    <section className="auth-shell">
      <div className="auth-brand">
        <span className="brand-deco brand-deco-1" />
        <span className="brand-deco brand-deco-2" />
        <span className="brand-deco brand-deco-3" />
        <p className="brand-chip">
          <Sparkles className="size-3.5" />
          English Speech Coach
        </p>
        <h1>Speak Better,<br />Score Higher</h1>
        <p className="brand-subtitle">AI 驱动的英语口语训练平台，专为剑桥少儿英语考试设计。</p>
        <div className="brand-features">
          <span className="brand-feature"><Mic className="size-3.5" />实时语音评测</span>
          <span className="brand-feature"><BookOpen className="size-3.5" />真题模拟练习</span>
        </div>
      </div>

      <div className="auth-card">
        <div className="auth-card-header">
          <h2>{mode === "register" ? "创建账号" : "欢迎回来"}</h2>
          <p>{mode === "register" ? "注册后即可开始口语训练" : "登录继续你的学习之旅"}</p>
        </div>
        <div className="mode-switch">
          <button
            className={mode === "login" ? "is-active" : ""}
            onClick={() => switchMode("login")}
            type="button"
          >
            登录
          </button>
          <button
            className={mode === "register" ? "is-active" : ""}
            onClick={() => switchMode("register")}
            type="button"
          >
            注册
          </button>
        </div>

        <div className="channel-switch">
          {(Object.keys(channelMap) as Channel[]).map((key) => (
            <button
              key={key}
              className={channel === key ? "is-active" : ""}
              onClick={() => switchChannel(key)}
              type="button"
            >
              {channelMap[key].icon}
              {channelMap[key].label}
            </button>
          ))}
        </div>

        <form
          className="form-grid"
          onSubmit={(event) => {
            event.preventDefault()
            void submitCurrentForm()
          }}
        >
          {mode === "login" && channel === "email" ? (
            <>
              <FormInput
                autoComplete="email"
                error={emailLoginForm.formState.errors.email?.message}
                label="邮箱"
                placeholder="you@example.com"
                type="email"
                {...emailLoginForm.register("email")}
              />
              <FormInput
                autoComplete="current-password"
                error={emailLoginForm.formState.errors.password?.message}
                label="密码"
                placeholder="请输入密码"
                type={showPassword["email-login"] ? "text" : "password"}
                action={
                  <button
                    className="password-toggle"
                    aria-label="显示或隐藏密码"
                    onClick={() => togglePassword("email-login")}
                    type="button"
                  >
                    {showPassword["email-login"] ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                  </button>
                }
                {...emailLoginForm.register("password")}
              />
            </>
          ) : null}

          {mode === "register" && channel === "email" ? (
            <>
              <FormInput
                autoComplete="email"
                error={emailRegisterForm.formState.errors.email?.message}
                label="邮箱"
                placeholder="you@example.com"
                type="email"
                {...emailRegisterForm.register("email")}
              />
              <FormInput
                autoComplete="one-time-code"
                error={emailRegisterForm.formState.errors.code?.message}
                label="验证码"
                placeholder="请输入验证码"
                type="text"
                action={
                  <button
                    className="code-btn"
                    disabled={emailCountdown > 0}
                    onClick={onSendEmailCode}
                    type="button"
                  >
                    {emailCountdown > 0 ? `${emailCountdown}s` : "发送"}
                  </button>
                }
                {...emailRegisterForm.register("code")}
              />
              <FormInput
                autoComplete="new-password"
                error={emailRegisterForm.formState.errors.password?.message}
                label="密码"
                placeholder="至少 6 位"
                type={showPassword["email-register"] ? "text" : "password"}
                action={
                  <button
                    className="password-toggle"
                    aria-label="显示或隐藏密码"
                    onClick={() => togglePassword("email-register")}
                    type="button"
                  >
                    {showPassword["email-register"] ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                  </button>
                }
                {...emailRegisterForm.register("password")}
              />
            </>
          ) : null}

          {channel === "sms" ? (
            <>
              <FormInput
                autoComplete="tel"
                error={
                  mode === "login"
                    ? smsLoginForm.formState.errors.mobile?.message
                    : smsRegisterForm.formState.errors.mobile?.message
                }
                label="手机号"
                placeholder="请输入中国大陆手机号"
                type="tel"
                {...(mode === "login"
                  ? smsLoginForm.register("mobile")
                  : smsRegisterForm.register("mobile"))}
              />
              <FormInput
                autoComplete="one-time-code"
                error={
                  mode === "login"
                    ? smsLoginForm.formState.errors.code?.message
                    : smsRegisterForm.formState.errors.code?.message
                }
                label="验证码"
                placeholder="请输入验证码"
                type="text"
                action={
                  <button
                    className="code-btn"
                    disabled={smsCountdown > 0}
                    onClick={onSendSmsCode}
                    type="button"
                  >
                    {smsCountdown > 0 ? `${smsCountdown}s` : "发送"}
                  </button>
                }
                {...(mode === "login"
                  ? smsLoginForm.register("code")
                  : smsRegisterForm.register("code"))}
              />
            </>
          ) : null}

          {status ? (
            <p className={status.type === "error" ? "status is-error" : "status is-success"}>
              {status.message}
            </p>
          ) : null}

          {mode === "login" ? (
            <label className="remember-login">
              <input
                checked={rememberLogin}
                onChange={(event) => setRememberLogin(event.target.checked)}
                type="checkbox"
              />
              <span className="remember-login-box">
                {rememberLogin ? <Check className="size-3.5" /> : null}
              </span>
              <span>
                保持登录 {REMEMBER_LOGIN_DAYS} 天
              </span>
            </label>
          ) : null}

          <button className="submit-btn" disabled={submitting} type="submit">
            {submitting ? <Loader2 className="size-4 animate-spin" /> : null}
            {mode === "register" ? "注册并开始学习" : "登录并继续学习"}
          </button>
        </form>
      </div>
    </section>
  )
}
