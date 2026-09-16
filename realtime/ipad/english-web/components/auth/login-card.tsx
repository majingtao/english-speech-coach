"use client"

import { Fragment, useEffect, useState } from "react"
import type { InputHTMLAttributes, ReactNode } from "react"
import { useForm, type UseFormRegisterReturn } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { BookOpen, Check, Eye, EyeOff, Loader2, Mic, Sparkles } from "lucide-react"
import { z } from "zod"
import {
  checkEmail,
  checkMobile,
  type LoginResult,
  loginByEmail,
  loginByMobilePassword,
  loginBySms,
  registerByEmail,
  resetPasswordByEmail,
  resetPasswordByMobile,
  sendEmailCode,
  sendSmsCode,
  SMS_SCENE,
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

const mobilePasswordSchema = z.object({
  mobile: z.string().regex(mobilePattern, "请输入中国大陆手机号"),
  password: z.string().min(1, "请输入密码"),
})

const emailResetSchema = z
  .object({
    email: z.string().regex(emailPattern, "请输入正确的邮箱"),
    code: z.string().regex(/^\d{4,8}$/, "请输入验证码"),
    password: z.string().min(6, "密码至少 6 位").max(32, "密码最多 32 位"),
    confirmPassword: z.string().min(1, "请再次输入新密码"),
  })
  .refine((v) => v.password === v.confirmPassword, {
    message: "两次输入的密码不一致",
    path: ["confirmPassword"],
  })

// 手机密码与 yudao /member/auth/login 保持一致：最多 16 位
const smsResetSchema = z
  .object({
    mobile: z.string().regex(mobilePattern, "请输入中国大陆手机号"),
    code: z.string().regex(/^\d{4,6}$/, "请输入验证码"),
    password: z.string().min(6, "密码至少 6 位").max(16, "密码最多 16 位"),
    confirmPassword: z.string().min(1, "请再次输入新密码"),
  })
  .refine((v) => v.password === v.confirmPassword, {
    message: "两次输入的密码不一致",
    path: ["confirmPassword"],
  })

type EmailLoginForm = z.infer<typeof emailLoginSchema>
type EmailRegisterForm = z.infer<typeof emailRegisterSchema>
type SmsForm = z.infer<typeof smsSchema>
type MobilePasswordForm = z.infer<typeof mobilePasswordSchema>
type EmailResetForm = z.infer<typeof emailResetSchema>
type SmsResetForm = z.infer<typeof smsResetSchema>

type Mode = "login" | "register" | "forgot"
type Channel = "email" | "sms"
type SmsLoginType = "code" | "password"

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string
  error?: string
  action?: ReactNode
  labelAction?: ReactNode
}

function FormInput({ label, error, action, labelAction, ...props }: InputProps) {
  return (
    <label className="field">
      <span className="field-label-row">
        <span className="field-label">{label}</span>
        {labelAction}
      </span>
      <span className="field-control">
        <input className="field-input" {...props} />
        {action}
      </span>
      {error ? <span className="field-error">{error}</span> : null}
    </label>
  )
}

type PasswordPairProps = {
  passwordField: UseFormRegisterReturn
  confirmField: UseFormRegisterReturn
  passwordError?: string
  confirmError?: string
  placeholder: string
  show: boolean
  onToggle: () => void
}

function PasswordPair({
  passwordField,
  confirmField,
  passwordError,
  confirmError,
  placeholder,
  show,
  onToggle,
}: PasswordPairProps) {
  return (
    <>
      <FormInput
        autoComplete="new-password"
        error={passwordError}
        label="新密码"
        placeholder={placeholder}
        type={show ? "text" : "password"}
        action={
          <button
            className="password-toggle"
            aria-label="显示或隐藏密码"
            onClick={onToggle}
            type="button"
          >
            {show ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        }
        {...passwordField}
      />
      <FormInput
        autoComplete="new-password"
        error={confirmError}
        label="确认新密码"
        placeholder="再次输入新密码"
        type={show ? "text" : "password"}
        {...confirmField}
      />
    </>
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
  const [smsLoginType, setSmsLoginType] = useState<SmsLoginType>("code")
  const [emailCountdown, setEmailCountdown] = useState(0)
  const [smsCountdown, setSmsCountdown] = useState(0)
  const [submitting, setSubmitting] = useState(false)
  const [status, setStatus] = useState<{ type: "error" | "success"; message: string } | null>(null)
  const [redirect] = useState(initialRedirect)
  const [showPassword, setShowPassword] = useState<Record<string, boolean>>({})
  const [rememberLogin, setRememberLogin] = useState(true)
  const [agreedTerms, setAgreedTerms] = useState(false)

  const isForgot = mode === "forgot"
  const actionText = mode === "register" ? "注册" : "登录"
  const otherChannelText = isForgot
    ? channel === "sms" ? "通过邮箱找回" : "通过手机找回"
    : channel === "sms" ? `邮箱${actionText}` : `手机${actionText}`

  const headerTitle = isForgot ? "找回密码" : mode === "register" ? "创建账号" : "欢迎回来"
  const headerSubtitle = isForgot
    ? channel === "sms"
      ? "通过手机验证码设置新密码（验证码注册的用户也可在此首次设置密码）"
      : "通过邮箱验证码设置新密码"
    : mode === "register"
      ? "注册后即可开始口语训练"
      : "登录继续你的学习之旅"
  const submitText = isForgot
    ? "重置密码"
    : mode === "register"
      ? "注册并开始学习"
      : "登录并继续学习"

  function switchMode(next: Mode) {
    setMode(next)
    setStatus(null)
  }

  function switchChannel(next: Channel) {
    setChannel(next)
    setStatus(null)
  }

  function toggleChannel() {
    switchChannel(channel === "sms" ? "email" : "sms")
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
  const mobilePasswordForm = useForm<MobilePasswordForm>({
    resolver: zodResolver(mobilePasswordSchema),
    defaultValues: { mobile: "", password: "" },
  })
  const emailResetForm = useForm<EmailResetForm>({
    resolver: zodResolver(emailResetSchema),
    defaultValues: { email: "", code: "", password: "", confirmPassword: "" },
  })
  const smsResetForm = useForm<SmsResetForm>({
    resolver: zodResolver(smsResetSchema),
    defaultValues: { mobile: "", code: "", password: "", confirmPassword: "" },
  })

  /** 从登录页进入「忘记密码」，把已输入的账号带过去 */
  function openForgot(from: Channel) {
    if (from === "email") {
      const email = emailLoginForm.getValues("email")
      if (email) emailResetForm.setValue("email", email)
    } else {
      const mobile = mobilePasswordForm.getValues("mobile") || smsLoginForm.getValues("mobile")
      if (mobile) smsResetForm.setValue("mobile", mobile)
    }
    setChannel(from)
    switchMode("forgot")
  }

  function backToLogin() {
    if (channel === "email") {
      const email = emailResetForm.getValues("email")
      if (email) emailLoginForm.setValue("email", email)
    } else {
      const mobile = smsResetForm.getValues("mobile")
      if (mobile) mobilePasswordForm.setValue("mobile", mobile)
      setSmsLoginType("password")
    }
    switchMode("login")
  }

  function toggleSmsLoginType() {
    const next: SmsLoginType = smsLoginType === "code" ? "password" : "code"
    const mobile = next === "password"
      ? smsLoginForm.getValues("mobile")
      : mobilePasswordForm.getValues("mobile")
    if (mobile) {
      if (next === "password") mobilePasswordForm.setValue("mobile", mobile)
      else smsLoginForm.setValue("mobile", mobile)
    }
    setSmsLoginType(next)
    setStatus(null)
  }

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
    if (mode === "forgot" && channel === "email") {
      await emailResetForm.handleSubmit(async (values) => {
        await handleSubmit(async () => {
          await resetPasswordByEmail({
            email: values.email,
            code: values.code,
            password: values.password,
          })
          emailLoginForm.reset({ email: values.email, password: "" })
          emailResetForm.reset({ email: values.email, code: "", password: "", confirmPassword: "" })
          setEmailCountdown(0)
          setMode("login")
          setStatus({ type: "success", message: "密码已重置，请使用新密码登录" })
        })
      })()
      return
    }

    if (mode === "forgot" && channel === "sms") {
      await smsResetForm.handleSubmit(async (values) => {
        await handleSubmit(async () => {
          await resetPasswordByMobile({
            mobile: values.mobile,
            code: values.code,
            password: values.password,
          })
          mobilePasswordForm.reset({ mobile: values.mobile, password: "" })
          smsResetForm.reset({ mobile: values.mobile, code: "", password: "", confirmPassword: "" })
          setSmsCountdown(0)
          setSmsLoginType("password")
          setMode("login")
          setStatus({ type: "success", message: "密码已重置，请使用新密码登录" })
        })
      })()
      return
    }

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

    if (mode === "login" && channel === "sms" && smsLoginType === "password") {
      await mobilePasswordForm.handleSubmit(async (values) => {
        await handleSubmit(async () => {
          const result = await loginByMobilePassword(values)
          await handleAuthSuccess(result, "登录成功")
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
    const isReset = mode === "forgot"
    const email = isReset
      ? emailResetForm.getValues("email")
      : emailRegisterForm.getValues("email")
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
      if (!isReset && exists) {
        setEmailCountdown(0)
        setStatus({ type: "error", message: "该邮箱已注册，请直接登录" })
        return
      }
      if (isReset && !exists) {
        setEmailCountdown(0)
        setStatus({ type: "error", message: "该邮箱未注册" })
        return
      }
      await sendEmailCode({ email, scene: isReset ? "reset" : "register" })
      setStatus({ type: "success", message: "验证码已发送" })
    } catch (error) {
      setEmailCountdown(0)
      setStatus({ type: "error", message: getErrorMessage(error, "发送失败") })
    }
  }

  async function onSendSmsCode() {
    const mobile =
      mode === "forgot"
        ? smsResetForm.getValues("mobile")
        : mode === "register"
          ? smsRegisterForm.getValues("mobile")
          : smsLoginForm.getValues("mobile")
    if (!mobilePattern.test(mobile)) {
      setStatus({ type: "error", message: "请输入正确的手机号" })
      return
    }
    if (smsCountdown > 0) {
      return
    }
    setSmsCountdown(60)
    try {
      if (mode === "register" || mode === "forgot") {
        const exists = await checkMobile(mobile)
        if (mode === "register" && exists) {
          setSmsCountdown(0)
          setStatus({ type: "error", message: "该手机号已注册，请直接登录" })
          return
        }
        if (mode === "forgot" && !exists) {
          setSmsCountdown(0)
          setStatus({ type: "error", message: "该手机号未注册" })
          return
        }
      }
      await sendSmsCode({
        mobile,
        scene: mode === "forgot" ? SMS_SCENE.MEMBER_RESET_PASSWORD : SMS_SCENE.MEMBER_LOGIN,
      })
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
          <h2>{headerTitle}</h2>
          <p>{headerSubtitle}</p>
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

        <form
          className="form-grid"
          onSubmit={(event) => {
            event.preventDefault()
            void submitCurrentForm()
          }}
        >
          {mode === "login" && channel === "email" ? (
            <Fragment key="email-login">
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
                labelAction={
                  <button
                    className="forgot-link"
                    onClick={() => openForgot("email")}
                    type="button"
                  >
                    忘记密码？
                  </button>
                }
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
            </Fragment>
          ) : null}

          {mode === "register" && channel === "email" ? (
            <Fragment key="email-register">
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
                inputMode="numeric"
                maxLength={6}
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
            </Fragment>
          ) : null}

          {mode === "login" && channel === "sms" && smsLoginType === "password" ? (
            <Fragment key="sms-password-login">
              <FormInput
                autoComplete="tel"
                inputMode="numeric"
                maxLength={11}
                error={mobilePasswordForm.formState.errors.mobile?.message}
                label="手机号"
                placeholder="请输入中国大陆手机号"
                type="tel"
                labelAction={
                  <button className="forgot-link" onClick={toggleSmsLoginType} type="button">
                    验证码登录
                  </button>
                }
                {...mobilePasswordForm.register("mobile")}
              />
              <FormInput
                autoComplete="current-password"
                error={mobilePasswordForm.formState.errors.password?.message}
                label="密码"
                placeholder="请输入密码"
                type={showPassword["sms-login"] ? "text" : "password"}
                labelAction={
                  <button className="forgot-link" onClick={() => openForgot("sms")} type="button">
                    忘记密码？
                  </button>
                }
                action={
                  <button
                    className="password-toggle"
                    aria-label="显示或隐藏密码"
                    onClick={() => togglePassword("sms-login")}
                    type="button"
                  >
                    {showPassword["sms-login"] ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                  </button>
                }
                {...mobilePasswordForm.register("password")}
              />
            </Fragment>
          ) : null}

          {isForgot && channel === "email" ? (
            <Fragment key="email-reset">
              <FormInput
                autoComplete="email"
                error={emailResetForm.formState.errors.email?.message}
                label="注册邮箱"
                placeholder="you@example.com"
                type="email"
                {...emailResetForm.register("email")}
              />
              <FormInput
                autoComplete="one-time-code"
                inputMode="numeric"
                maxLength={8}
                error={emailResetForm.formState.errors.code?.message}
                label="验证码"
                placeholder="请输入邮箱验证码"
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
                {...emailResetForm.register("code")}
              />
              <PasswordPair
                confirmError={emailResetForm.formState.errors.confirmPassword?.message}
                confirmField={emailResetForm.register("confirmPassword")}
                passwordError={emailResetForm.formState.errors.password?.message}
                passwordField={emailResetForm.register("password")}
                placeholder="6-32 位"
                show={!!showPassword["email-reset"]}
                onToggle={() => togglePassword("email-reset")}
              />
            </Fragment>
          ) : null}

          {isForgot && channel === "sms" ? (
            <Fragment key="sms-reset">
              <FormInput
                autoComplete="tel"
                inputMode="numeric"
                maxLength={11}
                error={smsResetForm.formState.errors.mobile?.message}
                label="手机号"
                placeholder="请输入注册手机号"
                type="tel"
                {...smsResetForm.register("mobile")}
              />
              <FormInput
                autoComplete="one-time-code"
                inputMode="numeric"
                maxLength={6}
                error={smsResetForm.formState.errors.code?.message}
                label="验证码"
                placeholder="请输入短信验证码"
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
                {...smsResetForm.register("code")}
              />
              <PasswordPair
                confirmError={smsResetForm.formState.errors.confirmPassword?.message}
                confirmField={smsResetForm.register("confirmPassword")}
                passwordError={smsResetForm.formState.errors.password?.message}
                passwordField={smsResetForm.register("password")}
                placeholder="6-16 位"
                show={!!showPassword["sms-reset"]}
                onToggle={() => togglePassword("sms-reset")}
              />
            </Fragment>
          ) : null}

          {channel === "sms" && !isForgot && !(mode === "login" && smsLoginType === "password") ? (
            <Fragment key={`sms-code-${mode}`}>
              <FormInput
                autoComplete="tel"
                inputMode="numeric"
                maxLength={11}
                error={
                  mode === "login"
                    ? smsLoginForm.formState.errors.mobile?.message
                    : smsRegisterForm.formState.errors.mobile?.message
                }
                label="手机号"
                placeholder="请输入中国大陆手机号"
                type="tel"
                labelAction={
                  mode === "login" ? (
                    <button className="forgot-link" onClick={toggleSmsLoginType} type="button">
                      密码登录
                    </button>
                  ) : undefined
                }
                {...(mode === "login"
                  ? smsLoginForm.register("mobile")
                  : smsRegisterForm.register("mobile"))}
              />
              <FormInput
                autoComplete="one-time-code"
                inputMode="numeric"
                maxLength={6}
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
            </Fragment>
          ) : null}

          {status ? (
            <p
              className={status.type === "error" ? "status is-error" : "status is-success"}
              role="alert"
              aria-live="polite"
            >
              {status.message}
            </p>
          ) : null}

          <div className={mode === "register" ? "form-aux form-aux-end" : "form-aux"}>
            {isForgot ? (
              <button className="channel-link" onClick={backToLogin} type="button">
                返回登录
              </button>
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
            <button className="channel-link" onClick={toggleChannel} type="button">
              {otherChannelText}
            </button>
          </div>

          {mode === "register" ? (
            <label className="agree-terms">
              <input
                checked={agreedTerms}
                onChange={(event) => setAgreedTerms(event.target.checked)}
                type="checkbox"
              />
              <span className="agree-terms-box">
                {agreedTerms ? <Check className="size-3.5" /> : null}
              </span>
              <span className="agree-terms-text">
                我已阅读并同意
                <a href="#" onClick={(event) => event.stopPropagation()}>《用户协议》</a>
                和
                <a href="#" onClick={(event) => event.stopPropagation()}>《隐私政策》</a>
              </span>
            </label>
          ) : null}

          <button
            className="submit-btn"
            disabled={submitting || (mode === "register" && !agreedTerms)}
            type="submit"
          >
            {submitting ? <Loader2 className="size-4 animate-spin" /> : null}
            {submitText}
          </button>
        </form>

        <p className="auth-contact">
          遇到问题？<a href="/contact">联系我们</a>
        </p>
      </div>
    </section>
  )
}
