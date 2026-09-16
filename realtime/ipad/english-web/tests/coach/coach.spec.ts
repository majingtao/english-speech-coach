import { test, expect, type Page } from "@playwright/test"

async function fixture(page: Page, mode = "practice") {
  await page.context().addCookies([{ name: "esc_h5_token", value: "isolated-e2e-fixture", domain: "127.0.0.1", path: "/" }])
  await page.addInitScript(() => { localStorage.setItem("esc_h5_token", "isolated-e2e-fixture"); window.speechSynthesis.speak = utterance => { setTimeout(() => utterance.onend?.(new Event("end") as SpeechSynthesisEvent), 1) } })
  const tasks = [
    { id: "ket-reason-1", title: "补充理由 · 运动", kind: "interview", focus: "reason", skills: ["reason"], prompt: "Do you like swimming? Why?", seconds: 120, options: [] },
    { id: "ket-reason-2", title: "补充理由 · 交通", kind: "interview", focus: "reason", skills: ["reason"], prompt: "Do you like travelling by train? Why?", seconds: 120, options: [] },
  ]
  const session: any = { id: "fixture-session", mode, status: "active", deadline: mode === "mock" ? Date.now() + 600000 : 0, serverTime: Date.now(), tasks: [tasks[0]], attempts: [] }
  let gradeCalls = 0; let uploads = 0
  await page.route("**/py/**", route => route.fulfill({ json: route.request().url().includes("/asr") ? { text: "I like swimming." } : [] }))
  await page.route("**/app-api/**", async route => {
    const url = new URL(route.request().url()); const path = url.pathname; let data: any = null
    if (path.endsWith("/ai-config")) data = { asrModels: [{ id: "test", label: "Test", type: "online", port: 0 }], ttsEngines: [{ id: "system", label: "System" }], defaults: { asr: "test", ttsEngine: "system" } }
    else if (path.endsWith("/dashboard")) data = { tasks, skills: [{ code: "reason", label: "补充理由", status: "practice", observed: 2 }], plan: { id: "fixture-plan", date: "2026-09-09", minutes: 12, items: [{ taskId: tasks[0].id, title: tasks[0].title, seconds: 120, reason: "最近两次没有说明理由", completed: false }] }, sessions: [] }
    else if (path.endsWith("/sessions")) { session.mode = route.request().postDataJSON().mode; session.deadline = session.mode === "mock" ? Date.now() + 600000 : 0; data = session }
    else if (path.endsWith("/hint")) { session.tasks[0] = { ...session.tasks[0], hint: "补充一个相关理由", sample: "I like swimming because it is fun." }; data = session }
    else if (path.endsWith("/attempts")) {
      const input = route.request().postDataJSON(); uploads++
      const turns = input.turns.map((t: any) => ({ ...t, hasAudio: !!t.audioBase64 }))
      session.attempts.push({ id: input.id, taskId: input.taskId, parentId: input.parentId, stage: input.parentId ? session.attempts.some((a: any) => a.id === input.parentId && a.taskId === input.taskId) ? "retry" : "transfer" : "initial", status: "pending", assisted: !!input.parentId, error: "", turns })
      data = session
    } else if (path.endsWith("/grade")) {
      gradeCalls++; const id = path.split("/").at(-2); const a = session.attempts.find((a: any) => a.id === id)
      a.status = "graded"; a.feedback = { strength: "你清楚地表达了喜好", improvement: "试着补充一个相关理由", revised: "I like swimming because it is fun.", skills: [{ code: "reason", observed: true, met: gradeCalls > 1, evidence: a.turns[0].text, feedback: "补充理由让对方更了解你的想法" }], pronunciation: [{ turnIndex: 0, status: "not_configured", message: "发音评估尚未配置 Azure Speech" }] }
      data = session
    } else if (path.endsWith("/transfer")) { session.tasks.push(tasks[1]); data = session }
    else if (path.endsWith("/finish")) { session.status = "finished"; data = session }
    else if (path.includes("/audio/")) {
      const pieces = path.split("/"); const a = session.attempts.find((a: any) => a.id === pieces.at(-3)); const audio = a.turns[Number(pieces.at(-1))].audioBase64
      await route.fulfill({ body: Buffer.from(audio, "base64"), contentType: "audio/wav" }); return
    } else if (path.includes("/sessions/")) data = session
    else { await route.fulfill({ status: 404, json: { code: 404, msg: "Unmocked request" } }); return }
    await route.fulfill({ json: { code: 0, data, msg: "" } })
  })
  return { session, gradeCalls: () => gradeCalls, uploads: () => uploads }
}

test("practice records audio, displays feedback, retries and transfers without leaking hints", async ({ page }) => {
  const f = await fixture(page)
  await page.goto("/speaking-coach")
  await expect(page.getByRole("heading", { name: "今天，再多说一点。" })).toBeVisible()
  await page.screenshot({ path: "../../output/coach/dashboard.png", fullPage: true })
  await page.getByRole("button", { name: "开始", exact: true }).click()
  await page.getByRole("button", { name: "开始录音", exact: true }).click()
  await expect(page.getByRole("button", { name: "说完了" })).toBeVisible()
  await page.waitForTimeout(700)
  await page.getByRole("button", { name: "说完了" }).click()
  await expect(page.getByRole("textbox")).toHaveValue("I like swimming.")
  await page.getByRole("button", { name: "完成并查看反馈" }).click()
  await expect(page.getByText("你清楚地表达了喜好")).toBeVisible()
  await expect(page.getByText("发音评估尚未配置 Azure Speech")).toBeVisible()
  await expect(page.getByRole("button", { name: "播放录音", exact: true })).toBeEnabled()
  await page.screenshot({ path: "../../output/coach/feedback.png", fullPage: true })
  await page.getByRole("button", { name: "按建议再说一次" }).click()
  await page.getByRole("textbox").fill("I like swimming because it is fun.")
  await page.getByRole("button", { name: "完成并查看反馈" }).click()
  await expect(page.getByRole("heading", { name: "重说反馈" })).toBeVisible()
  await page.getByRole("button", { name: "换题独立验证" }).click()
  await expect(page.getByRole("heading", { name: "Do you like travelling by train? Why?" })).toBeVisible()
  await expect(page.getByText("I like swimming because it is fun.", { exact: true })).toHaveCount(0)
  expect(f.uploads()).toBe(2)
  await page.setViewportSize({ width: 390, height: 844 })
  await page.screenshot({ path: "../../output/coach/mobile.png", fullPage: true })
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true)
})

test("draft survives reload and mock does not grade before finish", async ({ page }) => {
  const f = await fixture(page, "mock")
  await page.goto("/speaking-coach?session=fixture-session")
  await expect(page.getByRole("button", { name: "我需要一点提示" })).toHaveCount(0)
  await expect(page.getByRole("textbox")).toHaveAttribute("readonly", "")
  expect(f.gradeCalls()).toBe(0)
  await page.getByRole("button", { name: "开始录音", exact: true }).click()
  await page.waitForTimeout(700)
  await page.getByRole("button", { name: "说完了" }).click()
  await expect(page.getByRole("textbox")).toHaveValue("I like swimming.")
  await page.waitForTimeout(350)
  await page.reload()
  await expect(page.getByRole("textbox")).toHaveValue("I like swimming.")
  expect(f.gradeCalls()).toBe(0)
  await page.getByRole("button", { name: "提交并继续" }).click()
  await expect(page.getByRole("heading", { name: "听见今天的进步。" })).toBeVisible()
  expect(f.session.status).toBe("finished")
  await expect.poll(f.gradeCalls).toBe(1)
})
