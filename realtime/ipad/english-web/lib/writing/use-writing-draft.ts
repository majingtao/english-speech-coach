"use client"

import { useEffect, useRef, useState } from "react"
import { apiClient } from "@/lib/api/client"

type Mode = "guided" | "imitate" | "free"
type Draft = { answer: string; mode: Mode; draftInfo: Record<string, string> }
type Snapshot = { taskId: string; value: Draft; saved: boolean }

export function useWritingDraft(taskId: string, initialMode: Mode) {
  const [owner, setOwner] = useState<string | null | undefined>(undefined)
  const [snapshot, setSnapshot] = useState<Snapshot | null>(null)
  const current = useRef<Snapshot | null>(null)
  const [notice, setNotice] = useState("正在读取草稿…")

  useEffect(() => {
    let active = true
    void (apiClient.get("/app-api/member/user/get") as unknown as Promise<{ id: number }>)
      .then((user) => {
        if (!Number.isSafeInteger(user.id) || user.id <= 0) throw new Error("Missing account")
        if (active) setOwner(`${process.env.NEXT_PUBLIC_TENANT_ID || "1"}:${user.id}`)
      })
      .catch(() => { if (active) setOwner(null) })
    return () => { active = false }
  }, [])

  useEffect(() => {
    if (owner === undefined) return
    const timer = window.setTimeout(() => {
      let value: Draft = { answer: "", mode: initialMode, draftInfo: {} }
      let message = owner ? "草稿自动保存在本机" : "暂时无法保存草稿，请勿刷新或离开页面"
      try {
        const raw = owner && localStorage.getItem(`ket-writing-draft:${owner}:${taskId}`)
        if (raw) {
          const draft = JSON.parse(raw)
          if (typeof draft.answer !== "string" || !["guided", "imitate", "free"].includes(draft.mode)
            || !draft.draftInfo || typeof draft.draftInfo !== "object" || Array.isArray(draft.draftInfo)
            || !Object.values(draft.draftInfo).every((item) => typeof item === "string")) throw new Error("Invalid draft")
          value = { answer: draft.answer, mode: draft.mode, draftInfo: draft.draftInfo }
          message = "已恢复本机草稿"
        }
      } catch {
        message = "草稿读取失败，请勿刷新或离开页面"
      }
      const next = { taskId, value, saved: true }
      current.current = next
      setSnapshot(next)
      setNotice(message)
    }, 0)
    return () => window.clearTimeout(timer)
  }, [owner, taskId, initialMode])

  useEffect(() => {
    const warn = (event: BeforeUnloadEvent) => {
      if (current.current && !current.current.saved) {
        event.preventDefault()
        event.returnValue = ""
      }
    }
    window.addEventListener("beforeunload", warn)
    return () => window.removeEventListener("beforeunload", warn)
  }, [])

  function update(patch: Partial<Draft>) {
    if (!current.current || current.current.taskId !== taskId) return
    const value = { ...current.current.value, ...patch }
    let saved = false
    try {
      if (!owner) throw new Error("Missing account")
      // Save in the input event, so immediate navigation cannot race a debounce.
      localStorage.setItem(`ket-writing-draft:${owner}:${taskId}`, JSON.stringify(value))
      saved = true
      setNotice("已保存到本机")
    } catch {
      setNotice("草稿未能保存，请勿刷新或离开页面")
    }
    const next = { taskId, value, saved }
    current.current = next
    setSnapshot(next)
  }

  return {
    ready: owner !== undefined && snapshot?.taskId === taskId,
    value: snapshot?.taskId === taskId ? snapshot.value : { answer: "", mode: initialMode, draftInfo: {} },
    notice,
    update,
    canLeave: () => !current.current || current.current.saved || window.confirm("草稿尚未保存，离开会丢失本次内容。仍要离开吗？"),
  }
}
