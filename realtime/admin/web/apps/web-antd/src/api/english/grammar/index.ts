import type { PageParam, PageResult } from '@vben/request';
import { requestClient } from '#/api/request';

export namespace EnglishGrammarApi {
  export interface Point { id: number; code: string; nameCn: string; nameEn: string; description: string; levelCode: string; difficultyConfigJson?: string; status: number }
  export interface Question {
    id?: number; grammarPointId: number; generationJobId?: number; sourceQuestionId?: number; code: string;
    questionType: 'single_choice' | 'text_input'; difficulty: number; instruction?: string; stem: string;
    optionsJson?: string; answerJson: string; explanationZh: string; ruleText?: string; errorTagsJson?: string;
    mediaJson?: string; source?: string; validationStatus?: number; sort?: number; status?: number;
  }
  export interface BatchImport { grammarPointId: number; difficulty: number; questionTypes: string; autoPublish: boolean; model: string; settingsJson: string; questions: Question[] }
}
export const getGrammarPoints = (level = 'ket') => requestClient.get<EnglishGrammarApi.Point[]>('/english/grammar/point/list', { params: { level } });
export const getGrammarQuestionPage = (params: PageParam & Record<string, any>) => requestClient.get<PageResult<EnglishGrammarApi.Question>>('/english/grammar/question/page', { params });
export const getGrammarQuestion = (id: number) => requestClient.get<EnglishGrammarApi.Question>('/english/grammar/question/get', { params: { id } });
export const createGrammarQuestion = (data: EnglishGrammarApi.Question) => requestClient.post('/english/grammar/question/create', data);
export const updateGrammarQuestion = (data: EnglishGrammarApi.Question) => requestClient.put('/english/grammar/question/update', data);
export const deleteGrammarQuestion = (id: number) => requestClient.delete('/english/grammar/question/delete', { params: { id } });
export const importGeneratedGrammarQuestions = (data: EnglishGrammarApi.BatchImport) => requestClient.post('/english/grammar/generation/import', data);
