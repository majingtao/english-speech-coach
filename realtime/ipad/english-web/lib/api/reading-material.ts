import { apiClient } from "@/lib/api/client"

export interface ReadingExample {
  formKey?: string
  formLabel?: string
  target?: string
  en: string
  cn?: string
}

export interface ReadingWordForms {
  base?: string
  plural?: string
  thirdPerson?: string
  pastTense?: string
  pastParticiple?: string
  presentParticiple?: string
  comparative?: string
  superlative?: string
}

export interface ReadingMaterial {
  id: number
  textEn: string
  textCn?: string
  description?: string
  materialType: "word" | "phrase" | "sentence"
  partOfSpeech?: "noun" | "verb" | "adjective" | "adverb" | "phrase" | "sentence" | "unknown"
  levelCode?: string
  tagsJson: string[] | string
  examplesJson: ReadingExample[] | string
  wordFormsJson: ReadingWordForms | string
  vocabId?: number
  vocabWord?: string
  audioUkUrl?: string
  audioUsUrl?: string
  sort?: number
  status?: number
}

export interface NormalizedReadingMaterial extends Omit<ReadingMaterial, "tagsJson" | "examplesJson" | "wordFormsJson"> {
  tags: string[]
  examples: ReadingExample[]
  wordForms: ReadingWordForms
}

const BASE = "/app-api/english/reading-material"

function parseJson<T>(value: unknown, fallback: T): T {
  if (value == null) return fallback
  if (typeof value !== "string") return value as T
  try {
    return JSON.parse(value) as T
  } catch {
    return fallback
  }
}

function normalize(raw: ReadingMaterial): NormalizedReadingMaterial {
  const { tagsJson, examplesJson, wordFormsJson, ...rest } = raw
  return {
    ...rest,
    tags: parseJson<string[]>(tagsJson, []),
    examples: parseJson<ReadingExample[]>(examplesJson, []),
    wordForms: parseJson<ReadingWordForms>(wordFormsJson, {}),
  }
}

export async function fetchReadingMaterials(params?: {
  level?: string
  materialType?: string
  tag?: string
}): Promise<NormalizedReadingMaterial[]> {
  const list = await apiClient.get(BASE + "/list", {
    params: {
      level: params?.level || "ket",
      materialType: params?.materialType,
      tag: params?.tag,
    },
  }) as unknown as ReadingMaterial[]
  return (list || []).map(normalize)
}

export async function fetchReadingTags(level = "ket"): Promise<string[]> {
  const tags = await apiClient.get(BASE + "/tags", { params: { level } }) as unknown as string[] | Record<string, unknown>
  return Array.isArray(tags) ? tags : Object.values(tags).filter((tag): tag is string => typeof tag === "string")
}
