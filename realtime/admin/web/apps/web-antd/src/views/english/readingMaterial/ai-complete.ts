import type { EnglishReadingMaterialApi } from '#/api/english/readingMaterial';

export interface ReadingMaterialAiSettings {
  apiKey: string;
  baseUrl: string;
  model: string;
}

export interface ReadingMaterialAiPatch {
  description: string;
  examplesJson: string;
  levelCode: string;
  materialType: 'phrase' | 'sentence' | 'word';
  partOfSpeech: NonNullable<EnglishReadingMaterialApi.Material['partOfSpeech']>;
  tagsJson: string;
  textCn: string;
  wordFormsJson: string;
}

const DEFAULT_AI_BASE_URL = 'https://api.deepseek.com';
const DEFAULT_AI_MODEL = 'deepseek-v4-flash';
const LEGACY_DEFAULT_AI_BASE_URL = 'https://api.openai.com/v1';
const LEGACY_DEFAULT_AI_MODEL = 'gpt-4o-mini';

export function getStoredAiSettings(): ReadingMaterialAiSettings {
  const storedBaseUrl = localStorage.getItem('readingMaterialAiBaseUrl') || '';
  const storedModel = localStorage.getItem('readingMaterialAiModel') || '';
  const usingLegacyDefaults =
    storedBaseUrl === LEGACY_DEFAULT_AI_BASE_URL &&
    (!storedModel || storedModel === LEGACY_DEFAULT_AI_MODEL);

  return {
    apiKey: localStorage.getItem('readingMaterialAiApiKey') || '',
    baseUrl: usingLegacyDefaults
      ? DEFAULT_AI_BASE_URL
      : storedBaseUrl || DEFAULT_AI_BASE_URL,
    model: usingLegacyDefaults
      ? DEFAULT_AI_MODEL
      : storedModel || DEFAULT_AI_MODEL,
  };
}

export function saveAiSettings(settings: ReadingMaterialAiSettings) {
  localStorage.setItem('readingMaterialAiBaseUrl', settings.baseUrl);
  localStorage.setItem('readingMaterialAiApiKey', settings.apiKey);
  localStorage.setItem('readingMaterialAiModel', settings.model);
}

function extractJson(text: string) {
  const trimmed = text
    .trim()
    .replace(/^```json\s*/i, '')
    .replace(/^```\s*/, '')
    .replace(/```$/, '')
    .trim();
  const start = trimmed.indexOf('{');
  const end = trimmed.lastIndexOf('}');
  return JSON.parse(
    start !== -1 && end > start ? trimmed.slice(start, end + 1) : trimmed,
  );
}

function normalizePartOfSpeech(
  value: unknown,
  materialType: ReadingMaterialAiPatch['materialType'],
): ReadingMaterialAiPatch['partOfSpeech'] {
  const allowed = [
    'adjective',
    'adverb',
    'noun',
    'phrase',
    'sentence',
    'unknown',
    'verb',
  ];
  if (typeof value === 'string' && allowed.includes(value)) {
    return value as ReadingMaterialAiPatch['partOfSpeech'];
  }
  if (materialType === 'phrase') return 'phrase';
  if (materialType === 'sentence') return 'sentence';
  return 'unknown';
}

function normalizeAiResult(raw: any): ReadingMaterialAiPatch {
  const materialType = ['phrase', 'sentence', 'word'].includes(raw.materialType)
    ? raw.materialType
    : 'word';
  const partOfSpeech = normalizePartOfSpeech(raw.partOfSpeech, materialType);
  return {
    description: raw.description || '',
    examplesJson: JSON.stringify(
      materialType === 'word' && Array.isArray(raw.examples)
        ? raw.examples.slice(0, 3)
        : [],
      null,
      2,
    ),
    levelCode: 'ket',
    materialType,
    partOfSpeech,
    tagsJson: JSON.stringify(Array.isArray(raw.tags) ? raw.tags : [], null, 2),
    textCn: raw.translation || raw.textCn || '',
    wordFormsJson: JSON.stringify(
      raw.wordForms && typeof raw.wordForms === 'object' ? raw.wordForms : {},
      null,
      2,
    ),
  };
}

export async function completeReadingMaterialWithAi(
  material: Pick<EnglishReadingMaterialApi.Material, 'textEn'>,
  settings: ReadingMaterialAiSettings,
): Promise<ReadingMaterialAiPatch> {
  const textEn = material.textEn?.trim();
  if (!textEn) throw new Error('请先输入英文内容');
  if (!settings.baseUrl || !settings.apiKey || !settings.model) {
    throw new Error('请先填写 AI Base URL、API Key 和模型');
  }

  const prompt = `Analyze this English learning material for a Chinese child at Cambridge KET/A2 level: "${textEn}".
Return strict JSON only:
{
  "materialType": "word|phrase|sentence",
  "partOfSpeech": "noun|verb|adjective|adverb|phrase|sentence|unknown",
  "translation": "natural Chinese translation",
  "description": "short Chinese note, optional",
  "tags": ["2-5 lowercase simple tags"],
  "wordForms": {},
  "examples": [{"formKey":"base","formLabel":"原型","target":"go","en":"KET example sentence","cn":"Chinese translation"}]
}
Rules:
- If materialType is word, example sentences must cover useful word forms.
- If it is phrase or sentence, examples must be [].
- For verbs include base, thirdPerson, pastTense, pastParticiple, presentParticiple.
- For verbs provide exactly 5 examples, one for each verb form: base, thirdPerson, pastTense, pastParticiple, presentParticiple.
- For adjectives include base, comparative, superlative.
- For adjectives provide exactly 3 examples, one for each adjective form: base, comparative, superlative.
- For nouns include base and plural.
- For nouns provide exactly 3 examples: one base, one plural, and one natural extra base example.
- Each example must include formKey, formLabel, target, en, cn. Use Chinese labels such as 原型, 三单, 过去式, 过去分词, 现在分词, 比较级, 最高级, 复数.
- Keep examples simple, natural, and suitable for ages 7-12.`;

  const resp = await fetch(
    `${settings.baseUrl.replace(/\/$/, '')}/chat/completions`,
    {
      body: JSON.stringify({
        messages: [
          {
            content: 'You produce strict JSON for English learning content.',
            role: 'system',
          },
          { content: prompt, role: 'user' },
        ],
        model: settings.model,
        response_format: { type: 'json_object' },
      }),
      headers: {
        Authorization: `Bearer ${settings.apiKey}`,
        'Content-Type': 'application/json',
      },
      method: 'POST',
    },
  );
  if (!resp.ok) throw new Error(`AI 请求失败 (${resp.status})`);
  const body = await resp.json();
  const content = body.choices?.[0]?.message?.content;
  if (!content) throw new Error('AI 没有返回内容');
  return normalizeAiResult(extractJson(content));
}
