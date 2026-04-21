import { type NextRequest, NextResponse } from "next/server"
import https from "node:https"
import http from "node:http"
import type { RequestOptions } from "node:http"

const PY_TARGET = process.env.NEXT_PUBLIC_PY_BASE_URL || "https://127.0.0.1:8443"
const isHttps = PY_TARGET.startsWith("https")

function buildUrl(segments: string[], search: string) {
  return `${PY_TARGET}/${segments.join("/")}${search}`
}

function proxy(
  url: string,
  opts: { method: string; headers: Record<string, string>; body?: Buffer | null },
): Promise<{ status: number; headers: Record<string, string>; body: Buffer }> {
  return new Promise((resolve, reject) => {
    const parsed = new URL(url)
    const reqOpts: RequestOptions = {
      hostname: parsed.hostname,
      port: parsed.port || (isHttps ? "443" : "80"),
      path: `${parsed.pathname}${parsed.search}`,
      method: opts.method,
      headers: opts.headers,
    }
    const mod = isHttps ? https : http
    if (isHttps) {
      (reqOpts as https.RequestOptions).rejectUnauthorized = false
    }
    const req = mod.request(reqOpts, (res) => {
      const chunks: Buffer[] = []
      res.on("data", (c: Buffer) => chunks.push(c))
      res.on("end", () => {
        const h: Record<string, string> = {}
        for (const [k, v] of Object.entries(res.headers)) {
          if (v) h[k] = Array.isArray(v) ? v.join(", ") : v
        }
        resolve({ status: res.statusCode || 502, headers: h, body: Buffer.concat(chunks) })
      })
    })
    req.on("error", reject)
    if (opts.body) req.write(opts.body)
    req.end()
  })
}

const SKIP_REQ = new Set(["host", "connection", "transfer-encoding"])
const SKIP_RES = new Set(["transfer-encoding", "connection", "content-encoding"])

async function handle(req: NextRequest, params: { path: string[] }) {
  const url = buildUrl(params.path, req.nextUrl.search)
  const fwd: Record<string, string> = {}
  req.headers.forEach((v, k) => {
    if (!SKIP_REQ.has(k)) fwd[k] = v
  })

  let body: Buffer | null = null
  if (req.method !== "GET" && req.method !== "HEAD") {
    body = Buffer.from(await req.arrayBuffer())
  }

  try {
    const res = await proxy(url, { method: req.method, headers: fwd, body })
    const out = new Headers()
    for (const [k, v] of Object.entries(res.headers)) {
      if (!SKIP_RES.has(k)) out.set(k, v)
    }
    return new NextResponse(new Uint8Array(res.body), { status: res.status, headers: out })
  } catch (err) {
    return NextResponse.json({ error: String(err) }, { status: 502 })
  }
}

export async function GET(req: NextRequest, ctx: { params: Promise<{ path: string[] }> }) {
  return handle(req, await ctx.params)
}

export async function POST(req: NextRequest, ctx: { params: Promise<{ path: string[] }> }) {
  return handle(req, await ctx.params)
}
