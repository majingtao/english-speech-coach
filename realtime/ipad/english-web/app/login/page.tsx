import { LoginCard } from "@/components/auth/login-card"

type SearchParams = Record<string, string | string[] | undefined>

function toSingle(value: string | string[] | undefined) {
  return Array.isArray(value) ? value[0] : value
}

export default async function LoginPage({
  searchParams,
}: {
  searchParams: Promise<SearchParams>
}) {
  const params = await searchParams
  const modeParam = toSingle(params.mode)
  const channelParam = toSingle(params.channel)
  const redirectParam = toSingle(params.redirect)

  const initialMode = modeParam === "register" ? "register" : "login"
  const initialChannel =
    channelParam === "account" || channelParam === "sms" ? channelParam : "email"
  const initialRedirect =
    redirectParam && redirectParam.startsWith("/") ? redirectParam : "/"

  return (
    <main className="page-login">
      <LoginCard
        initialChannel={initialChannel}
        initialMode={initialMode}
        initialRedirect={initialRedirect}
      />
    </main>
  )
}
