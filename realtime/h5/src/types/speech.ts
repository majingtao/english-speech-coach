export interface LlmModel {
  provider: string
  model: string
  label: string
  use_proxy?: boolean
}

export interface AsrModel {
  id: string
  port: number
  type: 'streaming' | 'offline' | 'online'
  label: string
}

export interface TtsVoiceInfo {
  name: string
  label?: string
  gender?: string
}

export interface WarmupQuestion {
  examiner: string
  expected?: string
  sample?: string
  type?: string
}

export interface PictureSentence {
  text: string
  hint?: string
}

export interface PictureQuestion {
  pic: string
  prompt: string
  sentences: Array<string | PictureSentence>
}

export interface ExamQuestion {
  examiner: string
  expected?: string
  prompt?: string
  sample?: string
  hint?: string
  expected_question?: string
  open_ended?: boolean
}

export interface ExamPart {
  title?: string
  intro?: string
  example?: string
  instruction?: string
  lead?: string
  prompt?: string
  context?: string
  explanation?: string
  warmup?: ExamQuestion[]
  phaseA?: ExamQuestion[]
  phaseB?: ExamQuestion[]
  pictures?: PictureQuestion[]
  questions?: ExamQuestion[]
  followups?: ExamQuestion[]
  pic1?: string
}

export interface ExamTest {
  label?: string
  warmup?: ExamQuestion[]
  part1?: ExamPart
  part2?: ExamPart
  part3?: ExamPart
  part4?: ExamPart
  level?: string
}

export interface QuestionBank {
  warmup?: WarmupQuestion[]
  tests: Record<string, ExamTest>
}

export type ExamStep
  = { type: 'speak', text: string, label?: string }
    | { type: 'judge', question: string, expected?: string, sample?: string, label?: string, hint_text?: string, open_ended?: boolean }
    | { type: 'judge-question', hint: string, expected_question: string, label?: string, sample?: string }
    | { type: 'show-hint', hint: string }
    | { type: 'end' }

export type ExamMessageRole = 'examiner' | 'student' | 'judge-ok' | 'judge-fail' | 'hint' | 'system'

export interface ExamMessage {
  id: number
  role: ExamMessageRole
  text: string
  // 可重播的英文文本（不一定等于 text，例如 hint 显示 "💡 Sample: ..." 但只重播后半段）
  replay?: string
}
