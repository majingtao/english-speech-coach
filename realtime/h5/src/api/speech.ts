import type { QuestionBank } from '@/types/speech'

// 题库当前直接走前端 public 静态文件，不经过 yudao /admin-api
// 后续如果要做后台维护，再切到 request.get('/english/question-bank/all')
export async function fetchQuestionBank(): Promise<QuestionBank> {
  const base = import.meta.env.BASE_URL || '/'
  const staticUrl = `${base}question-bank.json`.replace(/\/+/g, '/')
  const res = await fetch(staticUrl)
  if (!res.ok) {
    throw new Error(`无法加载题库数据 (${res.status})`)
  }
  return (await res.json()) as QuestionBank
}
