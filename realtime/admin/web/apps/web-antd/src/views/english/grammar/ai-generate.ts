import type { EnglishGrammarApi } from '#/api/english/grammar';

const STORAGE_KEY = 'grammar-ai-settings-v1';
export interface GrammarAiSettings { baseUrl: string; apiKey: string; model: string }
export function getGrammarAiSettings(): GrammarAiSettings {
  try { return { baseUrl: '', apiKey: '', model: '', ...JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}') }; }
  catch { return { baseUrl: '', apiKey: '', model: '' }; }
}
export function saveGrammarAiSettings(settings: GrammarAiSettings) { localStorage.setItem(STORAGE_KEY, JSON.stringify(settings)); }

function extractJson(text: string) {
  const cleaned = text.replace(/^```(?:json)?\s*/i, '').replace(/\s*```$/, '');
  return JSON.parse(cleaned);
}
function validateQuestion(raw: any, pointId: number, difficulty: number, index: number): EnglishGrammarApi.Question {
  const type = raw.questionType;
  if (!['single_choice', 'text_input'].includes(type)) throw new Error(`第 ${index + 1} 题题型无效`);
  const options = Array.isArray(raw.options) ? raw.options.map(String) : [];
  const answers = Array.isArray(raw.acceptedAnswers) ? raw.acceptedAnswers.map(String) : [String(raw.correctAnswer || '')].filter(Boolean);
  if (!raw.stem || !raw.explanationZh || answers.length === 0) throw new Error(`第 ${index + 1} 题缺少题干、答案或解析`);
  if (type === 'single_choice' && (options.length < 3 || answers.length !== 1 || !options.includes(answers[0]))) throw new Error(`第 ${index + 1} 题单选配置无效`);
  return {
    grammarPointId: pointId, code: `grammar-${pointId}-${Date.now()}-${index + 1}`, questionType: type,
    difficulty, instruction: raw.instruction || (type === 'single_choice' ? 'Choose the correct answer.' : 'Complete the sentence.'),
    stem: String(raw.stem), optionsJson: JSON.stringify(options), answerJson: JSON.stringify(answers),
    explanationZh: String(raw.explanationZh), ruleText: String(raw.ruleText || ''),
    errorTagsJson: JSON.stringify(raw.errorTags || {}), mediaJson: 'null', source: 'ai', validationStatus: 1, sort: index * 10, status: 1,
  };
}
export async function generateGrammarQuestions(args: { point: EnglishGrammarApi.Point; difficulty: number; count: number; types: string[]; scene: string; settings: GrammarAiSettings }) {
  const { point, difficulty, count, types, scene, settings } = args;
  const levelRule = difficulty === 1
    ? 'L1: one short sentence, obvious clue, common A1-A2 vocabulary, only the target rule, weak distractors.'
    : 'L2: common context, include negative/question forms where suitable, one plausible distractor, still only one unambiguous answer.';
  const prompt = `Create exactly ${count} Cambridge A2 Key for Schools grammar practice questions for children aged 8-12.
Grammar point: ${point.nameEn} (${point.nameCn}). Scope: ${point.description}.
Difficulty: ${levelRule}
Allowed types: ${types.join(', ')}. Scene mix: ${scene || 'school, family, hobbies, travel'}.
Return strict JSON only: {"questions":[{"questionType":"single_choice|text_input","instruction":"short English instruction","stem":"sentence containing ____","options":["..."],"correctAnswer":"...","acceptedAnswers":["..."],"explanationZh":"简短中文解析","ruleText":"简短规则","errorTags":{"wrong option":"针对性中文提示"}}]}.
Rules: exactly one blank; single_choice has 4 unique options and exactly one correct answer; text_input includes every acceptable answer; no image; no trick question; no answer ambiguity; A2 vocabulary; do not repeat sentence patterns.`;
  const response = await fetch(`${settings.baseUrl.replace(/\/$/, '')}/chat/completions`, {
    method: 'POST', headers: { Authorization: `Bearer ${settings.apiKey}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ model: settings.model, response_format: { type: 'json_object' }, messages: [
      { role: 'system', content: 'You create reliable child-friendly English grammar questions and return strict JSON.' },
      { role: 'user', content: prompt },
    ] }),
  });
  if (!response.ok) throw new Error(`AI 请求失败 (${response.status})`);
  const body = await response.json();
  const parsed = extractJson(body.choices?.[0]?.message?.content || '');
  if (!Array.isArray(parsed.questions) || parsed.questions.length === 0) throw new Error('AI 未返回有效题目');
  return parsed.questions.map((q: any, i: number) => validateQuestion(q, point.id, difficulty, i));
}
