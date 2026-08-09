export interface LlmModel {
  provider: string
  model: string
  label: string
  use_proxy?: boolean
}

export interface AsrModel {
  id: string
  port: number
  type: "streaming" | "offline" | "online"
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
  imageA?: string
  imageB?: string
  images?: string | string[]
  transition?: string
  scene?: string
  story_name?: string
}

export interface PartnerInfo {
  name: string
  avatarSeed?: string
  voice?: string
}

export type CandidateSeat = "A" | "B"

export interface KetResponseSpec {
  kind: "basic" | "extended" | "tell_me_about" | "partner_opening" | "discussion" | "followup"
  sample?: string
  criteria?: string[]
}

export interface KetExaminerTurn {
  id: string
  speaker: "examiner"
  target: CandidateSeat
  text: string
  response: KetResponseSpec
  virtualAnswer: string
}

export interface KetPart1Topic {
  id: string
  title: string
  intro?: string
  turns: KetExaminerTurn[]
}

export interface KetPart1V2 {
  title?: string
  intro?: string
  turns: KetExaminerTurn[]
  topics: KetPart1Topic[]
}

export interface KetOption {
  id: string
  label: string
  image?: string
}

export interface KetConversationTurn {
  speaker: CandidateSeat
  goal: string
  virtualFallback: string
  responseKind: "partner_opening" | "discussion"
  criteria?: string[]
}

export interface KetFollowupTurn extends KetExaminerTurn {
  response: KetResponseSpec & { kind: "followup" }
}

export interface KetPart2V2 {
  title?: string
  examinerSetup: string
  material: {
    image?: string
    options: KetOption[]
  }
  conversation: {
    starter: CandidateSeat
    turns: KetConversationTurn[]
  }
  followups: KetFollowupTurn[]
}

export interface ExamTest {
  label?: string
  format?: "flyers" | "ket"
  schemaVersion?: number
  defaultUserSeat?: CandidateSeat
  warmup?: ExamQuestion[]
  partner?: PartnerInfo
  virtualCandidate?: PartnerInfo & { level?: string; personality?: string }
  candidateProfiles?: Partial<Record<CandidateSeat, PartnerInfo>>
  part1?: ExamPart | KetPart1V2
  part2?: ExamPart | KetPart2V2
  part3?: ExamPart
  part4?: ExamPart
  level?: string
}

export interface QuestionBank {
  warmup?: WarmupQuestion[]
  tests: Record<string, ExamTest>
}

export type ExamStep =
  | { type: "speak"; text: string; label?: string; target?: CandidateSeat }
  | { type: "candidate-speak"; seat: CandidateSeat; text: string; label?: string }
  | {
      type: "judge"
      question: string
      expected?: string
      sample?: string
      label?: string
      hint_text?: string
      open_ended?: boolean
      kind?: string
      target?: CandidateSeat
    }
  | {
      type: "judge-question"
      hint: string
      expected_question: string
      label?: string
      sample?: string
    }
  | { type: "show-hint"; hint: string }
  | { type: "show-image"; images: string[]; label?: string }
  | { type: "show-ket-material"; image?: string; options: KetOption[]; label?: string }
  | { type: "partner-speak"; text: string; label?: string }
  | { type: "partner-turn"; label?: string }
  | { type: "end" }

export type ExamMessageRole =
  | "examiner"
  | "student"
  | "partner"
  | "candidateA"
  | "candidateB"
  | "judge-ok"
  | "judge-fail"
  | "hint"
  | "system"

export interface ExamMessage {
  id: number
  role: ExamMessageRole
  text: string
  replay?: string
}
