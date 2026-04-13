<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { showNotify, showToast } from 'vant'
import { fetchQuestionBank } from '@/api/speech'
import type {
  AsrModel,
  ExamMessage,
  ExamMessageRole,
  ExamStep,
  LlmModel,
  QuestionBank,
  TtsVoiceInfo,
} from '@/types/speech'

const router = useRouter()

const levels = [
  { id: 'flyers', label: 'Flyers' },
] as const

type LevelId = typeof levels[number]['id']
const currentLevel = ref<LevelId>('flyers')
const levelSelections = reactive<Record<LevelId, { test: string, part: string }>>({
  flyers: { test: '', part: 'all' },
})

const configSummary = ref('加载中...')
const ttsEnabled = ref(true)
const ttsEngine = ref<'edge' | 'qwen-tts' | 'piper' | 'vibevoice' | 'system'>('edge')
const selectedVoice = ref('')
const llmProxy = ref(false)
const llmModels = ref<LlmModel[]>([])
const selectedLlmKey = ref('')
const asrModels = ref<AsrModel[]>([])
const selectedAsrId = ref('')
const pickerVisible = ref(false)
const pickerTitle = ref('')
const pickerColumns = ref<{ text: string, value: string }[]>([])
const pickerType = ref('')
const questionBank = ref<QuestionBank | null>(null)
const examVoiceOnly = ref(false)
const examStatus = ref('未开始')
const examStatusType = ref<'default' | 'success' | 'error'>('default')
const examMessages = ref<ExamMessage[]>([])
const examInputVisible = ref(false)
const examText = ref('')
const examSendDisabled = ref(true)
const examRecordDisabled = ref(true)
const examStartDisabled = ref(false)
const showChatLog = ref(false)
const settingsVisible = ref(false)
const examRecording = ref(false)
const examChatRef = ref<HTMLDivElement | null>(null)
const ttsLoading = ref(false)
const ttsSpeaking = ref(false)

const examStatusTag = computed(() => {
  if (examStatusType.value === 'success')
    return 'success'
  if (examStatusType.value === 'error')
    return 'danger'
  return 'primary'
})

function goHome() {
  router.push('/')
}

const currentSelection = computed(() => levelSelections[currentLevel.value])

const examPartOptions = [
  { value: 'all', label: '全流程' },
  { value: '1', label: 'Part 1' },
  { value: '2', label: 'Part 2' },
  { value: '3', label: 'Part 3' },
  { value: '4', label: 'Part 4' },
]

const currentLevelLabel = computed(() => levels.find(l => l.id === currentLevel.value)?.label || 'Flyers')

const currentLlm = computed(() => {
  if (!llmModels.value.length)
    return null
  if (!selectedLlmKey.value)
    return llmModels.value[0]
  const [provider, ...rest] = selectedLlmKey.value.split(':')
  const model = rest.join(':')
  return llmModels.value.find(m => m.provider === provider && m.model === model) || llmModels.value[0]
})

const currentLlmLabel = computed(() => currentLlm.value?.label || '未加载')

const currentAsrModel = computed(() => {
  if (!asrModels.value.length)
    return null
  if (!selectedAsrId.value)
    return asrModels.value[0]
  return asrModels.value.find(m => m.id === selectedAsrId.value) || asrModels.value[0]
})

const currentAsrLabel = computed(() => currentAsrModel.value?.label || '未加载')
const asrModeTip = computed(() => {
  const t = currentAsrModel.value?.type
  if (t === 'online')
    return '在线识别'
  if (t === 'offline')
    return '离线录音模式'
  return '实时模式'
})

const testsByLevel = computed<Record<LevelId, Array<{ value: string, label: string }>>>(() => {
  const grouped: Record<LevelId, Array<{ value: string, label: string }>> = {
    flyers: [],
  }
  const tests = questionBank.value?.tests || {}
  for (const [id, test] of Object.entries(tests)) {
    const level = ((test as any).level || 'all').toLowerCase()
    const option = { value: id, label: test.label || id }
    if (level === 'all' || !(level in grouped)) {
      levels.forEach(l => grouped[l.id].push(option))
    }
    else {
      grouped[level as LevelId].push(option)
    }
  }
  return grouped
})

const currentLevelTests = computed(() => testsByLevel.value[currentLevel.value] || [])

const examTestLabel = computed(() => {
  const sel = currentSelection.value.test
  const option = currentLevelTests.value.find(item => item.value === sel)
  return option?.label || '选择试卷'
})

const examPartLabel = computed(() => {
  const sel = currentSelection.value.part
  return examPartOptions.find(item => item.value === sel)?.label || '全流程'
})

const pickerMap: Record<string, () => void> = {
  'llm': () => {
    pickerTitle.value = '选择 LLM'
    pickerColumns.value = llmModels.value.map(m => ({ text: m.label, value: `${m.provider}:${m.model}` }))
    pickerType.value = 'llm'
    pickerVisible.value = true
  },
  'asr': () => {
    pickerTitle.value = '选择 ASR 模型'
    pickerColumns.value = asrModels.value.map(m => ({ text: m.label, value: m.id }))
    pickerType.value = 'asr'
    pickerVisible.value = true
  },
  'tts-engine': () => {
    pickerTitle.value = '选择播报引擎'
    pickerColumns.value = [
      { text: 'Edge-TTS', value: 'edge' },
      { text: 'Qwen3-TTS (Cloud)', value: 'qwen-tts' },
      { text: 'Piper (Local)', value: 'piper' },
      { text: 'VibeVoice', value: 'vibevoice' },
      { text: '系统语音', value: 'system' },
    ]
    pickerType.value = 'tts-engine'
    pickerVisible.value = true
  },
  'voice': () => {
    pickerTitle.value = '选择音色'
    pickerColumns.value = getVoiceOptions().map(v => ({ text: v.label || v.name, value: v.name }))
    pickerType.value = 'voice'
    pickerVisible.value = true
  },
  'exam-test': () => {
    pickerTitle.value = `${currentLevelLabel.value} 试卷`
    pickerColumns.value = currentLevelTests.value.map(t => ({ text: t.label, value: t.value }))
    pickerType.value = 'exam-test'
    pickerVisible.value = true
  },
  'exam-part': () => {
    pickerTitle.value = '起始部分'
    pickerColumns.value = examPartOptions.map(o => ({ text: o.label, value: o.value }))
    pickerType.value = 'exam-part'
    pickerVisible.value = true
  },
}
function openPicker(type: keyof typeof pickerMap) {
  if (pickerMap[type])
    pickerMap[type]()
}

function handlePickerConfirm(event: { selectedValues: string[], selectedOptions: Array<{ value: string }> }) {
  const value = event.selectedValues?.[0] || event.selectedOptions?.[0]?.value || ''
  switch (pickerType.value) {
    case 'llm':
      selectedLlmKey.value = value
      break
    case 'asr':
      selectedAsrId.value = value
      updateConfigSummary()
      break
    case 'tts-engine':
      ttsEngine.value = value as typeof ttsEngine.value
      ensureVoiceSelection()
      break
    case 'voice':
      selectedVoice.value = value
      break
    case 'exam-test':
      currentSelection.value.test = value
      resetExam()
      break
    case 'exam-part':
      currentSelection.value.part = value || 'all'
      resetExam()
      break
  }
  pickerVisible.value = false
  updateConfigSummary()
}
const ttsEngineLabel = computed(() => ({
  'edge': 'Edge-TTS',
  'qwen-tts': 'Qwen3-TTS',
  'piper': 'Piper (Local)',
  'vibevoice': 'VibeVoice',
  'system': '系统语音',
}[ttsEngine.value]))

const voiceLabel = computed(() => selectedVoice.value || '默认')

function updateConfigSummary() {
  const llm = currentLlm.value
  const asr = currentAsrModel.value
  const voice = selectedVoice.value || '-'
  configSummary.value = `LLM: ${llm?.label || '?'} (${llmProxy.value ? '代理' : '直连'}) | ASR: ${asr?.label || '?'} | TTS: ${ttsEngineLabel.value} (${voice})`
}

function handleTtsToggle() {
  if (ttsEnabled.value)
    unlockAudio()
  updateConfigSummary()
}

watch(currentLevel, (level) => {
  ensureLevelDefaults(level)
  resetExam()
})

watch(testsByLevel, () => {
  levels.forEach(l => ensureLevelDefaults(l.id))
  updateConfigSummary()
})

function ensureLevelDefaults(level: LevelId) {
  const list = testsByLevel.value[level]
  const sel = levelSelections[level]
  if (!sel.test && list && list.length > 0)
    sel.test = list[0].value
}

const isClient = typeof window !== 'undefined'

// Python 语音代理（LLM/ASR/TTS）统一走 vite dev proxy 的 /py 前缀，
// 由 vite.config.ts 转发到 https://localhost:8443，绕开混合内容 + CORS + 自签证书
function getRealtimeBase() {
  return '/py'
}

const endpoints = {
  tts: () => `${getRealtimeBase()}/tts`,
  ttsVoices: () => `${getRealtimeBase()}/tts/voices`,
  asrModels: () => `${getRealtimeBase()}/asr/models`,
  asrOffline: () => `${getRealtimeBase()}/asr`,
  llmModels: () => `${getRealtimeBase()}/llm/models`,
  llmChat: () => `${getRealtimeBase()}/llm/chat`,
  judge: () => `${getRealtimeBase()}/llm/judge`,
}

async function loadLlmModels() {
  try {
    const res = await fetch(endpoints.llmModels())
    if (res.ok)
      llmModels.value = await res.json()
  }
  catch (error) {
    console.warn('LLM models failed', error)
  }
  if (!llmModels.value.length)
    llmModels.value = [{ provider: 'ollama', model: 'qwen2.5:7b', label: 'Qwen 2.5 7B (Local)' }]
  // 默认优先 GPT-4o（精确匹配 model === 'gpt-4o'，避免选到 mini）
  const preferred = llmModels.value.find(m => m.model === 'gpt-4o') || llmModels.value[0]
  selectedLlmKey.value = `${preferred.provider}:${preferred.model}`
  llmProxy.value = !!preferred.use_proxy
  updateConfigSummary()
}

async function loadAsrModels() {
  try {
    const res = await fetch(endpoints.asrModels())
    if (res.ok)
      asrModels.value = await res.json()
  }
  catch (error) {
    console.warn('ASR models failed', error)
  }
  if (!asrModels.value.length)
    asrModels.value = [{ id: 'zipformer-en-2023-06-26', port: 6006, type: 'streaming', label: '默认流式模型' }]
  // 默认优先 SenseVoice Small (230MB)
  const preferredAsr = asrModels.value.find(m => m.id === 'sensevoice-small') || asrModels.value[0]
  selectedAsrId.value = preferredAsr.id
  updateConfigSummary()
}
const ttsAudio = isClient ? new Audio() : null
if (ttsAudio) {
  ttsAudio.autoplay = false
  ttsAudio.addEventListener('ended', () => {
    ttsSpeaking.value = false
  })
}
let ttsUnlocked = false
let ttsAudioUnlocked = false
let _ttsAudioCtx: AudioContext | null = null
let _ttsSourceNode: AudioBufferSourceNode | null = null
let _ttsResolve: (() => void) | null = null

function _getTtsAudioCtx() {
  if (!_ttsAudioCtx || _ttsAudioCtx.state === 'closed')
    _ttsAudioCtx = new (window.AudioContext || (window as any).webkitAudioContext)()
  if (_ttsAudioCtx.state === 'suspended')
    _ttsAudioCtx.resume().catch(() => {})
  return _ttsAudioCtx
}
const systemVoices = ref<TtsVoiceInfo[]>([])
const edgeVoices = ref<TtsVoiceInfo[]>([])
const vibeVoices = ref<TtsVoiceInfo[]>([])

if (isClient && 'speechSynthesis' in window) {
  window.speechSynthesis.onvoiceschanged = loadSystemVoices
  loadSystemVoices()
}

function loadSystemVoices() {
  if (!isClient || !('speechSynthesis' in window))
    return
  systemVoices.value = window.speechSynthesis.getVoices().filter(v => v.lang?.startsWith('en'))
}

async function loadEdgeVoices() {
  try {
    const res = await fetch(`${endpoints.ttsVoices()}?engine=edge`)
    if (res.ok)
      edgeVoices.value = await res.json()
  }
  catch (error) {
    console.warn('edge voices failed', error)
  }
}

async function loadVibeVoices() {
  try {
    const res = await fetch(`${endpoints.ttsVoices()}?engine=vibevoice`)
    if (res.ok)
      vibeVoices.value = await res.json()
  }
  catch (error) {
    console.warn('vibe voices failed', error)
  }
}

function getVoiceOptions(): TtsVoiceInfo[] {
  if (ttsEngine.value === 'edge')
    return edgeVoices.value
  if (ttsEngine.value === 'vibevoice')
    return vibeVoices.value
  if (ttsEngine.value === 'qwen-tts') {
    return [
      { name: 'Chelsie', label: 'Chelsie (EN Female)' },
      { name: 'Ethan', label: 'Ethan (EN Male)' },
      { name: 'Cherry', label: 'Cherry (ZH Female)' },
      { name: 'Serena', label: 'Serena (ZH Female)' },
    ]
  }
  if (ttsEngine.value === 'piper')
    return [{ name: 'amy', label: 'Amy (en-US)' }]
  return systemVoices.value.map((v: any) => ({ name: v.name, label: `${v.name} (${v.lang || 'en'})` }))
}

function ensureVoiceSelection() {
  const options = getVoiceOptions()
  if (!options.length) {
    selectedVoice.value = ''
    return
  }
  if (!selectedVoice.value || !options.some(v => v.name === selectedVoice.value))
    selectedVoice.value = options[0].name
}

watch([() => ttsEngine.value, edgeVoices, vibeVoices, systemVoices], ensureVoiceSelection)
function extractEnglishText(text: string) {
  return text
    .split('\n')
    .filter((line) => {
      const t = line.trim()
      if (!t)
        return false
      return ((t.match(/[\u4E00-\u9FFF]/g) || []).length / t.length) < 0.3
    })
    .join('. ')
    .replace(/#{1,6}\s*/g, '')
    .replace(/\*{1,3}([^*]+)\*{1,3}/g, '$1')
    .replace(/`([^`]+)`/g, '$1')
    .replace(/[_~>|]/g, '')
    .replace(/^\d+\.\s*/gm, '')
    .replace(/^[-*]\s*/gm, '')
    .replace(/\s{2,}/g, ' ')
    .trim()
}

function unlockAudio() {
  if (!isClient)
    return
  if (!ttsUnlocked && 'speechSynthesis' in window) {
    const utter = new SpeechSynthesisUtterance('')
    utter.volume = 0
    utter.lang = 'en-US'
    window.speechSynthesis.speak(utter)
    ttsUnlocked = true
  }
  if (ttsAudio && !ttsAudioUnlocked) {
    ttsAudio.src = 'data:audio/mp3;base64,SUQzBAAAAAAAI1RTU0UAAAAPAAADTGF2ZjU4Ljc2LjEwMAAAAAAAAAAAAAAA//tQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAWGluZwAAAA8AAAACAAABhgC7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7u7//////////////////////////////////////////////////////////////////8AAAAATGF2YzU4LjEzAAAAAAAAAAAAAAAAJAAAAAAAAAAABIYGAAAAAAAAAAAAAAAAAAAA//tQZAAP8AAAaQAAAAgAAA0gAAABAAABpAAAACAAADSAAAAETEFNRTMuMTAwVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV//tQZB4P8AAAaQAAAAgAAA0gAAABAAABpAAAACAAADSAAAAEVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVQ=='
    ttsAudio.volume = 0
    ttsAudio.play().then(() => {
      if (!ttsAudio)
        return
      ttsAudio.pause()
      ttsAudio.volume = 1
      ttsAudioUnlocked = true
    }).catch(() => {})
  }
}

function stopTts() {
  if ('speechSynthesis' in window)
    window.speechSynthesis.cancel()
  if (ttsAudio) {
    ttsAudio.pause()
    ttsAudio.currentTime = 0
  }
  if (_ttsSourceNode) {
    try { _ttsSourceNode.stop() }
    catch {}
    try { _ttsSourceNode.disconnect() }
    catch {}
    _ttsSourceNode = null
  }
  if (_ttsResolve) {
    _ttsResolve()
    _ttsResolve = null
  }
  ttsSpeaking.value = false
  ttsLoading.value = false
}

async function speakWithSystem(text: string) {
  if (!('speechSynthesis' in window))
    return
  window.speechSynthesis.cancel()
  if (_ttsResolve) {
    _ttsResolve()
    _ttsResolve = null
  }
  const toSpeak = extractEnglishText(text)
  if (!toSpeak)
    return
  await new Promise<void>((resolve) => {
    _ttsResolve = resolve
    const voice = window.speechSynthesis.getVoices().find(v => v.name === selectedVoice.value)
    const parts = toSpeak.match(/[^.!?]+[.!?]+|[^.!?]+$/g) || [toSpeak]
    parts.forEach((content, idx) => {
      const utter = new SpeechSynthesisUtterance(content)
      utter.lang = 'en-US'
      utter.rate = 0.9
      if (voice)
        utter.voice = voice
      if (idx === 0)
        utter.onstart = () => { ttsSpeaking.value = true }
      utter.onend = () => {
        if (idx === parts.length - 1) {
          ttsSpeaking.value = false
          if (_ttsResolve === resolve)
            _ttsResolve = null
          resolve()
        }
      }
      window.speechSynthesis.speak(utter)
    })
  })
}

async function speakWithServer(text: string, engine: string) {
  // 上一段还在播 → 先停掉 + resolve 旧 Promise，避免抢占
  stopTts()
  const toSpeak = extractEnglishText(text)
  if (!toSpeak)
    return
  unlockAudio()
  ttsLoading.value = true
  const url = endpoints.tts()
  const ctrl = new AbortController()
  const timer = setTimeout(() => ctrl.abort(), 45000)
  const body: Record<string, any> = { engine, text: toSpeak }
  if (engine === 'piper') {
    body.speed = 1.0
  }
  else if (engine === 'edge') {
    body.voice = selectedVoice.value || 'en-US-AnaNeural'
    body.rate = '-10%'
  }
  else if (engine === 'vibevoice') {
    body.voice = selectedVoice.value || 'en-Carter_man'
    body.cfg_scale = 1.5
  }
  else if (engine === 'qwen-tts') {
    body.voice = selectedVoice.value || 'Chelsie'
  }
  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
      signal: ctrl.signal,
    })
    clearTimeout(timer)
    if (!res.ok)
      throw new Error(`TTS 请求失败 ${res.status}`)
    const arrayBuf = await res.arrayBuffer()
    if (arrayBuf.byteLength < 256)
      return
    const ctx = _getTtsAudioCtx()
    const audioBuf = await ctx.decodeAudioData(arrayBuf.slice(0))
    ttsLoading.value = false
    // 等本段播放彻底结束再 resolve，串行播放才能按顺序
    await new Promise<void>((resolve) => {
      _ttsResolve = resolve
      const done = () => {
        _ttsSourceNode = null
        ttsSpeaking.value = false
        if (_ttsResolve === resolve)
          _ttsResolve = null
        resolve()
      }
      const src = ctx.createBufferSource()
      src.buffer = audioBuf
      src.connect(ctx.destination)
      src.onended = done
      _ttsSourceNode = src
      ttsSpeaking.value = true
      src.start()
    })
  }
  finally {
    clearTimeout(timer)
    ttsLoading.value = false
  }
}

async function speakText(text: string) {
  if (!ttsEnabled.value)
    return
  unlockAudio()
  try {
    if (ttsEngine.value === 'system')
      await speakWithSystem(text)
    else
      await speakWithServer(text, ttsEngine.value)
  }
  catch (error: any) {
    showNotify({ type: 'warning', message: error.message || '播报失败' })
  }
}
const examState = reactive({
  steps: [] as ExamStep[],
  stepIdx: 0,
  retries: 0,
  waiting: false,
  requireRepeat: null as string | null,
  score: { correct: 0, retry: 0, showAnswer: 0 },
})

const examProgress = computed(() => {
  const step = examState.steps[examState.stepIdx] as any
  if (!step)
    return ''
  const isQ = (s: any) => s && (s.type === 'judge' || s.type === 'judge-question')
  const total = examState.steps.filter(isQ).length
  const done = examState.steps.slice(0, examState.stepIdx).filter(isQ).length
  return `${step.label || ''}  (${done}/${total} questions)`
})

let examAudioCtx: AudioContext | null = null
let examMediaStream: MediaStream | null = null
let examSourceNode: MediaStreamAudioSourceNode | null = null
let examProcessorNode: ScriptProcessorNode | null = null
let examOfflineChunks: Float32Array[] = []
let examRecTimer: number | null = null

function chatClass(role: ExamMessageRole) {
  return {
    'chat-examiner': role === 'examiner',
    'chat-student': role === 'student',
    'chat-judge-ok': role === 'judge-ok',
    'chat-judge-fail': role === 'judge-fail',
    'chat-hint': role === 'hint',
    'chat-system': role === 'system',
  }
}

function addExamMsg(role: ExamMessageRole, text: string, replay?: string) {
  examMessages.value = [
    ...examMessages.value,
    { id: Date.now() + Math.random(), role, text, replay },
  ]
  nextTick(() => {
    const box = examChatRef.value
    if (box)
      box.scrollTop = 0
  })
}

const examMessagesReversed = computed(() => [...examMessages.value].slice().reverse())

const replayBusyId = ref<number | null>(null)
async function replayMessage(msg: ExamMessage) {
  const target = msg.replay || msg.text
  if (!target || replayBusyId.value !== null)
    return
  replayBusyId.value = msg.id
  try {
    await speakText(target)
  }
  catch {}
  finally {
    replayBusyId.value = null
  }
}

function setExamStatus(text: string, type: 'default' | 'success' | 'error' = 'default') {
  examStatus.value = text
  examStatusType.value = type
}

function resetExam() {
  examState.steps = []
  examState.stepIdx = 0
  examState.retries = 0
  examState.waiting = false
  examState.requireRepeat = null
  examState.score = { correct: 0, retry: 0, showAnswer: 0 }
  examMessages.value = []
  examInputVisible.value = false
  examSendDisabled.value = true
  examRecordDisabled.value = true
  examText.value = ''
  setExamStatus('未开始')
  releaseExamMic()
}

async function loadExamBank() {
  try {
    questionBank.value = await fetchQuestionBank()
  }
  catch (error: any) {
    showNotify({ type: 'danger', message: error.message || '题库加载失败' })
  }
}

async function ensureExamMic() {
  if (examMediaStream && examMediaStream.getTracks().some(track => track.readyState === 'live')) {
    if (!examAudioCtx || examAudioCtx.state === 'closed')
      examAudioCtx = new (window.AudioContext || (window as any).webkitAudioContext)()
    else if (examAudioCtx.state === 'suspended')
      await examAudioCtx.resume()
    return true
  }
  try {
    examMediaStream = await navigator.mediaDevices.getUserMedia({ audio: { channelCount: 1 } })
    examAudioCtx = new (window.AudioContext || (window as any).webkitAudioContext)()
    return true
  }
  catch {
    setExamStatus('麦克风未授权', 'error')
    return false
  }
}

function releaseExamMic() {
  examProcessorNode?.disconnect()
  examProcessorNode = null
  examSourceNode?.disconnect()
  examSourceNode = null
  examAudioCtx?.close()
  examAudioCtx = null
  examMediaStream?.getTracks().forEach(track => track.stop())
  examMediaStream = null
  if (examRecTimer) {
    clearInterval(examRecTimer)
    examRecTimer = null
  }
  examRecording.value = false
}
function buildSteps(): ExamStep[] {
  const steps: ExamStep[] = []
  const testId = currentSelection.value.test
  const bank = questionBank.value
  if (!testId || !bank)
    return steps
  const test = bank.tests[testId] as any
  if (!test)
    return steps

  const startPart = currentSelection.value.part // 'all' | '1' | '2' | '3' | '4'
  const doWarmup = startPart === 'all'
  const parts = startPart === 'all' ? ['1', '2', '3', '4'] : [startPart]

  // Warm-up 走顶层 bank.warmup（不是 test.warmup）
  if (doWarmup && bank.warmup && bank.warmup.length) {
    bank.warmup.forEach((w: any, i: number) => {
      steps.push({ type: 'speak', text: w.examiner, label: 'Warm-up' })
      steps.push({
        type: 'judge',
        question: w.examiner,
        expected: '',
        sample: w.sample || '',
        open_ended: true,
        label: `Warm-up ${i + 1}/${bank.warmup!.length}`,
      })
    })
  }

  for (const pn of parts) {
    const part = test[`part${pn}`]
    if (!part)
      continue

    if (pn === '1') {
      steps.push({ type: 'speak', text: part.intro, label: `Part 1 — ${part.title || ''}` })
      if (part.example)
        steps.push({ type: 'speak', text: part.example })
      if (part.instruction)
        steps.push({ type: 'speak', text: part.instruction })
      ;(part.questions || []).forEach((q: any, i: number) => {
        const label = `Part 1: ${i + 1}/${part.questions.length}`
        steps.push({ type: 'speak', text: q.examiner, label })
        steps.push({
          type: 'judge',
          question: q.examiner,
          expected: q.expected,
          label,
        } as any)
      })
    }

    if (pn === '2') {
      steps.push({ type: 'speak', text: part.intro, label: `Part 2 — ${part.title || ''}` })
      ;(part.phaseA || []).forEach((q: any, i: number) => {
        const label = `Part 2A: ${i + 1}/${part.phaseA.length}`
        steps.push({ type: 'speak', text: q.examiner, label })
        steps.push({
          type: 'judge',
          question: q.examiner,
          expected: q.expected,
          label,
        } as any)
      })
      if (part.transition)
        steps.push({ type: 'speak', text: part.transition, label: 'Part 2 — Now you ask' })
      ;(part.phaseB || []).forEach((q: any, i: number) => {
        const label = `Part 2B: ${i + 1}/${part.phaseB.length}`
        steps.push({ type: 'show-hint', hint: q.hint } as any)
        steps.push({
          type: 'judge-question',
          expected_question: q.expected_question,
          hint: q.hint,
          label,
        } as any)
        if (q.answer)
          steps.push({ type: 'speak', text: q.answer, label: 'Part 2B: answer' })
      })
    }

    if (pn === '3') {
      steps.push({ type: 'speak', text: part.intro, label: `Part 3 — ${part.title || ''}` })
      if (part.pic1)
        steps.push({ type: 'speak', text: part.pic1 })
      if (part.instruction)
        steps.push({ type: 'speak', text: part.instruction })
      ;(part.pictures || []).forEach((pic: any) => {
        steps.push({
          type: 'speak',
          text: `Picture ${pic.pic}: ${pic.prompt}`,
          label: `Part 3: Picture ${pic.pic}`,
        })
        pic.sentences.forEach((s: any, si: number) => {
          const sText = typeof s === 'string' ? s : s.text
          const sHint = typeof s === 'string' ? pic.prompt : s.hint
          steps.push({
            type: 'judge',
            question: `Picture ${pic.pic} — ${sHint}`,
            expected: sText,
            hint_text: sHint,
            label: `Part 3: Pic ${pic.pic} (${si + 1}/${pic.sentences.length})`,
          } as any)
        })
      })
    }

    if (pn === '4') {
      steps.push({ type: 'speak', text: part.intro, label: `Part 4 — ${part.title || ''}` })
      ;(part.questions || []).forEach((q: any, i: number) => {
        const label = `Part 4: ${i + 1}/${part.questions.length}`
        steps.push({ type: 'speak', text: q.examiner, label })
        steps.push({
          type: 'judge',
          question: q.examiner,
          expected: '',
          sample: q.sample || '',
          open_ended: true,
          label,
        } as any)
      })
      if (part.followups && part.followups.length) {
        part.followups.forEach((q: any, i: number) => {
          const label = `Part 4+: ${i + 1}/${part.followups.length}`
          steps.push({ type: 'speak', text: q.examiner, label })
          steps.push({
            type: 'judge',
            question: q.examiner,
            expected: '',
            sample: q.sample || '',
            open_ended: true,
            label,
          } as any)
        })
      }
    }
  }

  steps.push({ type: 'speak', text: 'OK. Thank you. Goodbye.', label: 'End' })
  steps.push({ type: 'end' })
  return steps
}

async function startExam() {
  if (!currentSelection.value.test) {
    showToast('请先选择试卷')
    return
  }
  examMessages.value = []
  examState.steps = buildSteps()
  if (!examState.steps.length) {
    showToast('试卷暂无题目')
    return
  }
  examState.stepIdx = 0
  examState.retries = 0
  examState.score = { correct: 0, retry: 0, showAnswer: 0 }
  examStartDisabled.value = true
  examInputVisible.value = true
  examSendDisabled.value = true
  examRecordDisabled.value = true
  setExamStatus('考试中', 'success')
  await runExamStep()
}

async function runExamStep() {
  if (examState.stepIdx >= examState.steps.length)
    return
  const step = examState.steps[examState.stepIdx] as any
  if (step.type === 'speak') {
    addExamMsg('examiner', step.text, step.text)
    await speakText(step.text)
    examState.stepIdx++
    await runExamStep()
  }
  else if (step.type === 'show-hint') {
    addExamMsg('hint', `Your turn to ask: ${step.hint}`, step.hint)
    examState.stepIdx++
    examState.waiting = true
    examState.retries = 0
    examSendDisabled.value = false
    examRecordDisabled.value = false
    examText.value = ''
    setExamStatus('用提示组成一个问题', 'success')
  }
  else if (step.type === 'judge' || step.type === 'judge-question') {
    // 连续 judge 步骤（前面没 speak）—— 比如 Part 3 一张图多句
    if (step.hint_text) {
      addExamMsg('examiner', step.hint_text, step.hint_text)
      await speakText(step.hint_text)
    }
    examState.waiting = true
    examState.retries = 0
    examSendDisabled.value = false
    examRecordDisabled.value = false
    examText.value = ''
    setExamStatus('轮到你回答了', 'success')
  }
  else if (step.type === 'end') {
    const s = examState.score
    addExamMsg('system', `考试结束 ✅ ${s.correct} 正确 | 🔄 ${s.retry} 重试 | 📖 ${s.showAnswer} 看答案`)
    setExamStatus('已完成', 'success')
    examState.stepIdx = examState.steps.length
    examStartDisabled.value = false
    examSendDisabled.value = true
    examRecordDisabled.value = true
  }
}

async function callJudge(question: string, expected: string, student: string) {
  const ctrl = new AbortController()
  const timer = setTimeout(() => ctrl.abort(), 45000)
  try {
    const res = await fetch(endpoints.judge(), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        provider: currentLlm.value?.provider,
        model: currentLlm.value?.model,
        use_proxy: llmProxy.value,
        question,
        expected,
        student,
      }),
      signal: ctrl.signal,
    })
    clearTimeout(timer)
    if (!res.ok)
      return { ok: false, fb: `Server error ${res.status}`, ans: expected }
    return await res.json()
  }
  catch (error: any) {
    clearTimeout(timer)
    if (error.name === 'AbortError')
      return { ok: false, timeout: true, fb: '判题超时', ans: expected }
    return { ok: false, fb: error.message || 'Judge error', ans: expected }
  }
}

async function safeTtsSpeak(text: string) {
  if (!ttsEnabled.value || !text)
    return
  try {
    await Promise.race([
      speakText(text),
      new Promise(resolve => setTimeout(resolve, 5000)),
    ])
  }
  catch {}
}

async function submitExamAnswer(text: string) {
  if (!examState.waiting)
    return
  examState.waiting = false
  examSendDisabled.value = true
  examRecordDisabled.value = true
  addExamMsg('student', text)

  try {
    // 抄写模式：上一题给出标准答案后，要求学生输入近似答案才能继续
    if (examState.requireRepeat) {
      setExamStatus('Checking...')
      const norm = (s: string) =>
        s.toLowerCase().replace(/[^a-z0-9\s]/g, '').replace(/\s+/g, ' ').trim()
      const nStudent = norm(text)
      const nExpected = norm(examState.requireRepeat)
      const expWords = nExpected.split(' ').filter(w => w.length > 2)
      const matched = expWords.filter(w => nStudent.includes(w)).length
      const ratio = expWords.length > 0 ? matched / expWords.length : 0
      if (nStudent === nExpected || ratio >= 0.7) {
        addExamMsg('judge-ok', "✅ Good. Let's continue.")
        examState.requireRepeat = null
        examState.retries = 0
        examState.stepIdx++
        setTimeout(() => runExamStep(), 800)
      }
      else {
        addExamMsg('judge-fail', `❌ Please type: ${examState.requireRepeat}`)
        examState.waiting = true
        examSendDisabled.value = false
        examRecordDisabled.value = false
        setExamStatus('请输入正确答案以继续', 'error')
      }
      return
    }

    setExamStatus('判分中...')
    const step = examState.steps[examState.stepIdx] as any
    const isQ = step.type === 'judge-question'
    const question = isQ ? `Form a question using hint: ${step.hint}` : step.question
    const expected = isQ ? step.expected_question : (step.expected || '')
    const sample = step.sample || ''
    const judgeExpected = expected || (sample ? `(open-ended, sample: ${sample})` : '')
    const result: any = await callJudge(question, judgeExpected, text)

    if (result.ok) {
      addExamMsg('judge-ok', '✅ Correct!')
      await safeTtsSpeak('Correct!')
      examState.score.correct++
      examState.retries = 0
      examState.stepIdx++
      setTimeout(() => runExamStep(), 300)
      return
    }

    if (result.timeout) {
      addExamMsg('system', '⏳ 判题超时，自动跳过')
      if (expected)
        addExamMsg('hint', `📖 Expected: ${expected}`, expected)
      else if (sample)
        addExamMsg('hint', `💡 Sample: ${sample}`, sample)
      examState.retries = 0
      examState.stepIdx++
      setTimeout(() => runExamStep(), 800)
      return
    }

    examState.retries++
    const fbText = result.fb || 'Not quite.'
    addExamMsg('judge-fail', `❌ ${fbText}`, fbText)
    await safeTtsSpeak('Not quite.')
    if (result.cn)
      addExamMsg('hint', `🇨🇳 ${result.cn}`)

    const correctText = result.ans || expected || sample || ''
    if (correctText)
      await safeTtsSpeak(correctText)

    if (step.open_ended) {
      // 开放题（Warm-up / Part 4）：给 sample 后直接放过
      if (examState.retries >= 3) {
        if (sample)
          addExamMsg('hint', `💡 Sample: ${sample}`, sample)
        addExamMsg('system', "Let's move on.")
        examState.score.showAnswer++
        examState.retries = 0
        examState.stepIdx++
        setTimeout(() => runExamStep(), 1000)
      }
      else {
        if (sample)
          addExamMsg('hint', `💡 Sample: ${sample}`, sample)
        addExamMsg('hint', 'Try again.')
        examState.score.retry++
        examState.waiting = true
        examSendDisabled.value = false
        examRecordDisabled.value = false
        setExamStatus('请再试一次', 'error')
      }
    }
    else {
      // 结构题（Part 1-3）：错 2 次后强制抄写标准答案
      const ans = expected || ''
      if (examState.retries >= 3) {
        if (ans) {
          addExamMsg('hint', `📖 Answer: ${ans}`, ans)
          addExamMsg('hint', '👉 Please type the correct answer to continue.')
          examState.score.showAnswer++
          examState.requireRepeat = ans
          setExamStatus('请输入正确答案以继续', 'success')
        }
        else {
          // 没有标准答案兜底直接跳过
          addExamMsg('system', "Let's move on.")
          examState.retries = 0
          examState.stepIdx++
          setTimeout(() => runExamStep(), 800)
          return
        }
      }
      else {
        if (ans)
          addExamMsg('hint', `💡 Hint: ${ans}`, ans)
        addExamMsg('hint', 'Try again.')
        examState.score.retry++
        setExamStatus('请再试一次', 'error')
      }
      examState.waiting = true
      examSendDisabled.value = false
      examRecordDisabled.value = false
    }
  }
  catch (err: any) {
    console.error('[exam] submitExamAnswer error:', err)
    addExamMsg('system', `Error: ${err?.message || err} — try again.`)
    examState.waiting = true
    examSendDisabled.value = false
    examRecordDisabled.value = false
    setExamStatus('出错了，请重试', 'error')
  }
}

function sendExamAnswer() {
  const text = examText.value.trim()
  if (!text)
    return
  submitExamAnswer(text)
  examText.value = ''
}
const TARGET_SAMPLE_RATE = 16000

function downsample(buffer: Float32Array, inputRate: number) {
  if (inputRate === TARGET_SAMPLE_RATE)
    return buffer
  const ratio = inputRate / TARGET_SAMPLE_RATE
  const length = Math.round(buffer.length / ratio)
  const result = new Float32Array(length)
  let offsetResult = 0
  let offsetBuffer = 0
  while (offsetResult < result.length) {
    const nextOffsetBuffer = Math.round((offsetResult + 1) * ratio)
    let accum = 0
    let count = 0
    for (let i = offsetBuffer; i < nextOffsetBuffer && i < buffer.length; i++) {
      accum += buffer[i]
      count++
    }
    result[offsetResult] = accum / count
    offsetResult++
    offsetBuffer = nextOffsetBuffer
  }
  return result
}

async function toggleExamRecording() {
  if (examRecording.value) {
    examRecording.value = false
    examRecordDisabled.value = true
    if (examRecTimer) {
      clearInterval(examRecTimer)
      examRecTimer = null
    }
    examProcessorNode?.disconnect()
    examSourceNode?.disconnect()
    if (!examOfflineChunks.length) {
      setExamStatus('无音频，请长按录音', 'error')
      if (examState.waiting)
        examRecordDisabled.value = false
      return
    }
    try {
      setExamStatus('识别中...')
      const totalLen = examOfflineChunks.reduce((sum, chunk) => sum + chunk.length, 0)
      const allSamples = new Float32Array(totalLen)
      let offset = 0
      examOfflineChunks.forEach((chunk) => {
        allSamples.set(chunk, offset)
        offset += chunk.length
      })
      examOfflineChunks = []
      const res = await fetch(`${endpoints.asrOffline()}?model=${currentAsrModel.value?.id}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/octet-stream' },
        body: allSamples.buffer,
      })
      const data = await res.json()
      const text = (data.text || '').trim()
      if (text) {
        if (examVoiceOnly.value) {
          // 语音直答模式：识别即提交，submitExamAnswer 内部会按需重新启用按钮
          submitExamAnswer(text)
        }
        else {
          examText.value = text
          setExamStatus('识别完成，可编辑后发送', 'success')
          examSendDisabled.value = false
          // 允许重新录音覆盖
          if (examState.waiting)
            examRecordDisabled.value = false
        }
      }
      else {
        setExamStatus('未识别到内容，请重试', 'error')
        if (examState.waiting)
          examRecordDisabled.value = false
      }
    }
    catch (error: any) {
      setExamStatus(`识别失败: ${error.message}`, 'error')
      if (examState.waiting)
        examRecordDisabled.value = false
    }
  }
  else {
    unlockAudio()
    const ok = await ensureExamMic()
    if (!ok)
      return
    if (examAudioCtx!.state === 'suspended')
      await examAudioCtx!.resume()
    examSourceNode = examAudioCtx!.createMediaStreamSource(examMediaStream!)
    examProcessorNode = examAudioCtx!.createScriptProcessor(4096, 1, 1)
    examOfflineChunks = []
    examProcessorNode.onaudioprocess = (e) => {
      const raw = e.inputBuffer.getChannelData(0)
      examOfflineChunks.push(downsample(raw, examAudioCtx!.sampleRate))
    }
    examSourceNode.connect(examProcessorNode)
    examProcessorNode.connect(examAudioCtx!.destination)
    examRecording.value = true
    let sec = 0
    setExamStatus('录音中 0s', 'success')
    examRecTimer && clearInterval(examRecTimer)
    examRecTimer = window.setInterval(() => {
      sec++
      setExamStatus(`录音中 ${sec}s`, 'success')
    }, 1000)
  }
}
onMounted(async () => {
  if (!isClient)
    return
  await Promise.all([loadLlmModels(), loadAsrModels(), loadEdgeVoices(), loadVibeVoices(), loadExamBank()])
  ensureLevelDefaults(currentLevel.value)
  updateConfigSummary()
})

onBeforeUnmount(() => {
  resetExam()
  stopTts()
})
</script>

<template>
  <div class="speech-page">
    <van-nav-bar
      class="top-nav"
      title="口语考试 - Flyers"
      left-text="首页"
      left-arrow
      @click-left="goHome"
    />

    <!-- 模型设置面板（隐藏，通过底部齿轮按钮唤出） -->
    <van-popup v-model:show="settingsVisible" round position="bottom" :style="{ maxHeight: '70vh' }">
      <div class="settings-popup-body">
        <van-cell-group inset>
          <van-cell title="LLM" :value="currentLlmLabel" is-link @click="openPicker('llm')" />
          <van-cell title="代理">
            <template #right-icon>
              <van-switch id="llmProxyToggle" v-model="llmProxy" size="20px" @change="updateConfigSummary" />
            </template>
          </van-cell>
          <van-cell title="ASR" :value="currentAsrLabel" :label="asrModeTip" is-link @click="openPicker('asr')" />
          <van-cell title="语音播报">
            <template #right-icon>
              <van-switch id="ttsToggle" v-model="ttsEnabled" size="20px" @change="handleTtsToggle" />
            </template>
          </van-cell>
          <van-cell title="播报引擎" :value="ttsEngineLabel" is-link @click="openPicker('tts-engine')" />
          <van-cell title="播报音色" :value="voiceLabel" is-link @click="openPicker('voice')" />
        </van-cell-group>
      </div>
    </van-popup>

    <van-tabs v-model:active="currentLevel" type="card" class="level-tabs">
      <van-tab v-for="level in levels" :key="level.id" :title="level.label" :name="level.id">
        <section class="speech-card">
          <van-cell-group inset>
            <van-cell title="测试卷" :value="examTestLabel" is-link @click="openPicker('exam-test')" />
            <van-cell title="起始部分" :value="examPartLabel" is-link @click="openPicker('exam-part')" />
            <van-cell title="仅语音模式">
              <template #right-icon>
                <van-switch id="voiceOnlyToggle" v-model="examVoiceOnly" size="20px" />
              </template>
            </van-cell>
            <van-cell title="显示对话">
              <template #right-icon>
                <van-switch v-model="showChatLog" size="20px" />
              </template>
            </van-cell>
          </van-cell-group>
          <van-space class="action-row" wrap>
            <van-button id="examStartBtn" type="primary" :disabled="examStartDisabled" @click="startExam">
              开始考试
            </van-button>
            <van-button
              type="default"
              size="small"
              plain
              @click="settingsVisible = true"
            >
              设置
            </van-button>
            <van-button
              v-show="ttsSpeaking"
              id="ttsStopBtn2"
              type="warning"
              size="small"
              plain
              @click="stopTts"
            >
              停止播报
            </van-button>
            <van-loading v-show="ttsLoading" id="ttsLoading2" size="16px" />
          </van-space>
          <div class="status-row">
            <van-tag :type="examStatusTag" round>
              {{ examStatus }}
            </van-tag>
            <span id="examProgress" class="status-sub">{{ examProgress }}</span>
          </div>
          <div v-show="examInputVisible" id="examInput" class="exam-input">
            <van-field
              id="examTextField"
              v-model="examText"
              type="textarea"
              rows="3"
              placeholder="输入或点击麦克风回答"
            />
            <van-space id="examBtnRow" class="action-row" justify="end">
              <van-button id="examRecordBtn" type="warning" size="small" :disabled="examRecordDisabled" @click="toggleExamRecording">
                {{ examRecording ? '停止录音' : '🎙️ 麦克风' }}
              </van-button>
              <van-button id="examSendBtn" type="primary" size="small" :disabled="examSendDisabled" @click="sendExamAnswer">
                发送
              </van-button>
            </van-space>
          </div>
          <div v-show="showChatLog" class="text-box">
            <div class="box-label">
              考试对话
            </div>
            <div id="examChat" ref="examChatRef" class="chat-panel">
              <div v-for="msg in examMessagesReversed" :key="msg.id" class="chat-msg" :class="chatClass(msg.role)">
                <span class="chat-msg-text">{{ msg.text }}</span>
                <van-button
                  v-if="msg.replay"
                  class="chat-replay-btn"
                  size="mini"
                  type="primary"
                  plain
                  :loading="replayBusyId === msg.id"
                  :disabled="replayBusyId !== null && replayBusyId !== msg.id"
                  @click="replayMessage(msg)"
                >
                  🔊 重播
                </van-button>
              </div>
            </div>
          </div>
        </section>
      </van-tab>
    </van-tabs>

    <van-popup v-model:show="pickerVisible" round position="bottom">
      <van-picker
        show-toolbar
        :title="pickerTitle"
        :columns="pickerColumns"
        @confirm="handlePickerConfirm"
        @cancel="pickerVisible = false"
      />
    </van-popup>
  </div>
</template>

<style scoped>
.speech-page {
  padding-bottom: 60px;
}
.top-nav {
  margin-bottom: 8px;
}
.settings-card,
.speech-card {
  padding: 16px;
}
.level-tabs {
  margin-top: 12px;
}
.status-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.status-sub {
  font-size: 12px;
  color: #909399;
}
.action-row {
  margin: 12px 0;
}
.settings-panel {
  margin-bottom: 8px;
}
.text-box {
  margin-bottom: 16px;
}
.box-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
.chat-panel {
  border: 1px solid #ebedf0;
  border-radius: 12px;
  padding: 12px;
  max-height: 55vh;
  overflow-y: auto;
}
.chat-msg {
  padding: 8px 12px;
  border-radius: 12px;
  margin-bottom: 10px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.chat-msg-text {
  flex: 1 1 auto;
  word-break: break-word;
}
.chat-replay-btn {
  flex: 0 0 auto;
}
.chat-examiner {
  background: #e3f2fd;
  color: #1565c0;
  margin-right: auto;
}
.chat-student {
  background: #e8f5e9;
  color: #2e7d32;
  margin-left: auto;
}
.chat-judge-ok {
  background: #f1f8e9;
  color: #558b2f;
}
.chat-judge-fail {
  background: #fff3e0;
  color: #ef6c00;
}
.chat-hint {
  background: #ede7f6;
  color: #5e35b1;
}
.chat-system {
  background: #f5f5f5;
  color: #666;
  text-align: center;
}
.exam-input {
  margin-top: 12px;
}
.settings-popup-body {
  padding: 16px 0;
}
</style>

<style>
/* 禁止移动端左右拖动 */
html, body {
  overflow-x: hidden;
  overscroll-behavior-x: none;
}
.speech-page {
  touch-action: pan-y;
  overflow-x: hidden;
}
</style>
