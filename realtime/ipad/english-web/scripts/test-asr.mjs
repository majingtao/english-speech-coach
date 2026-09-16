// Run with: node --test scripts/test-asr.mjs
import { test } from "node:test"
import assert from "node:assert/strict"
import fs from "node:fs"
import { fileURLToPath } from "node:url"
import vm from "node:vm"
import ts from "typescript"

function fixture(responses = []) {
  const uploads = []
  const tracks = []
  let processor
  let mic = async () => {
    const track = { readyState: "live", stop() { this.readyState = "ended" } }
    tracks.push(track)
    return { getTracks: () => [track] }
  }
  class AudioContext {
    state = "running"
    sampleRate = 16000
    destination = {}
    createMediaStreamSource() { return { connect() {}, disconnect() {} } }
    createScriptProcessor() { processor = { connect() {}, disconnect() {} }; return processor }
    async close() { this.state = "closed" }
  }
  const compiledModule = { exports: {} }
  const code = ts.transpileModule(fs.readFileSync(fileURLToPath(new URL("../lib/exam/asr.ts", import.meta.url)), "utf8"), {
    compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS },
  }).outputText
  vm.runInNewContext(code, {
    module: compiledModule, exports: compiledModule.exports, AudioContext, AbortController, DOMException, Error,
    Float32Array, Blob, setInterval, clearInterval, setTimeout, clearTimeout,
    window: { isSecureContext: true }, navigator: { mediaDevices: { getUserMedia: () => mic() } },
    require: (id) => id === "./endpoints" ? { endpoints: { asrOffline: () => "/asr" } } : {
      pyFetch: async (_, init) => {
        uploads.push(Buffer.from(init.body))
        const next = responses.shift()
        if (next instanceof Error) throw next
        return new Response(JSON.stringify(next || { text: "hello" }), { headers: { "Content-Type": "application/json" } })
      },
      handleAuthRejection: async () => false,
      handleQuotaRejection: async () => false,
    },
  })
  const recorder = new compiledModule.exports.AsrRecorder()
  return {
    recorder, uploads, tracks,
    setMic: (fn) => { mic = fn },
    capture: (samples) => processor.onaudioprocess({ inputBuffer: { getChannelData: () => samples } }),
  }
}

test("network failure retains exactly the same audio for retry and releases the microphone", async () => {
  const f = fixture([new Error("offline"), { text: "hello again" }])
  try {
    await f.recorder.startRecording(() => {})
    f.capture(new Float32Array([.1, .2, .3]))
    assert.equal((await f.recorder.stopAndRecognize("test")).error, "offline")
    assert.equal(f.recorder.hasPendingAudio, true)
    assert.equal(f.tracks[0].readyState, "ended")
    assert.equal((await f.recorder.stopAndRecognize("test")).text, "hello again")
    assert.deepEqual(f.uploads[1], f.uploads[0])
    assert.equal(f.recorder.hasPendingAudio, false)
  } finally { f.recorder.release() }
})

test("a reused 16 kHz audio buffer cannot corrupt already captured samples", async () => {
  const f = fixture()
  try {
    await f.recorder.startRecording(() => {})
    const samples = new Float32Array([.25, .5])
    f.capture(samples)
    samples.fill(0)
    await f.recorder.stopAndRecognize("test")
    assert.deepEqual(f.uploads[0], Buffer.from(new Float32Array([.25, .5]).buffer))
  } finally { f.recorder.release() }
})

test("pausing for backgrounding preserves audio without uploading", async () => {
  const f = fixture()
  try {
    await f.recorder.startRecording(() => {})
    f.capture(new Float32Array([.5]))
    f.recorder.pause()
    assert.equal(f.recorder.recording, false)
    assert.equal(f.recorder.hasPendingAudio, true)
    assert.equal(f.tracks[0].readyState, "ended")
    assert.equal(f.uploads.length, 0)
    assert.equal((await f.recorder.stopAndRecognize("test")).text, "hello")
  } finally { f.recorder.release() }
})

test("unmount cancels a late microphone permission grant", async () => {
  const f = fixture()
  let grant
  const track = { stopped: false, stop() { this.stopped = true } }
  f.setMic(() => new Promise((resolve) => { grant = resolve }))
  const starting = f.recorder.startRecording(() => {})
  f.recorder.release()
  grant({ getTracks: () => [track] })
  assert.equal(await starting, false)
  assert.equal(track.stopped, true)
  assert.equal(f.recorder.recording, false)
})

test("recording again replaces failed audio and release clears retained samples", async () => {
  const f = fixture([new Error("offline"), { text: "new answer" }])
  try {
    await f.recorder.startRecording(() => {})
    f.capture(new Float32Array([.1]))
    await f.recorder.stopAndRecognize("test")
    await f.recorder.startRecording(() => {})
    f.capture(new Float32Array([.8]))
    assert.equal((await f.recorder.stopAndRecognize("test")).text, "new answer")
    assert.notDeepEqual(f.uploads[0], f.uploads[1])
    f.recorder.release()
    assert.equal(f.recorder.hasPendingAudio, false)
  } finally { f.recorder.release() }
})
