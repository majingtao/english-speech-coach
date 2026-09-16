import type { useSpeechRecorder } from "@/lib/exam/use-speech-recorder"

export function RecordingFeedback({ recorder }: { recorder: ReturnType<typeof useSpeechRecorder> }) {
  if (!recorder.notice && !recorder.canRetry) return null
  return (
    <div className="recording-feedback">
      <span role="status">{recorder.notice}{recorder.recording ? ` · ${recorder.seconds}s` : ""}</span>
      {recorder.recording && (
        <meter min={0} max={1} value={recorder.level} aria-label="麦克风音量" />
      )}
      {recorder.canRetry && <button type="button" onClick={recorder.retry} disabled={recorder.busy}>重试这段录音</button>}
    </div>
  )
}
