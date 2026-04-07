#!/bin/bash
# Start all ASR server processes in the sherpa-rt Docker container.
# Usage: docker exec -d sherpa-rt bash /workspace/start_asr.sh

REPO=/workspace/repo/python-api-examples
MODELS=/workspace/models
DOC_ROOT=$REPO/web

echo "Starting ASR servers..."

# --- Streaming: zipformer-en-2023-06-26 (port 6006) ---
python3 $REPO/streaming_server.py \
  --encoder $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-26/encoder-epoch-99-avg-1-chunk-16-left-128.onnx \
  --decoder $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-26/decoder-epoch-99-avg-1-chunk-16-left-128.onnx \
  --joiner $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-26/joiner-epoch-99-avg-1-chunk-16-left-128.onnx \
  --tokens $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-26/tokens.txt \
  --port 6006 --doc-root $DOC_ROOT &
echo "  [6006] zipformer-en-2023-06-26 (streaming)"

# --- Streaming: zipformer-en-2023-06-21 (port 6007) ---
python3 $REPO/streaming_server.py \
  --encoder $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-21/encoder-epoch-99-avg-1.onnx \
  --decoder $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-21/decoder-epoch-99-avg-1.onnx \
  --joiner $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-21/joiner-epoch-99-avg-1.onnx \
  --tokens $MODELS/sherpa-onnx-streaming-zipformer-en-2023-06-21/tokens.txt \
  --port 6007 --doc-root $DOC_ROOT &
echo "  [6007] zipformer-en-2023-06-21 (streaming)"

# --- Offline: Whisper medium.en (port 6008) ---
python3 $REPO/non_streaming_server.py \
  --whisper-encoder=$MODELS/sherpa-onnx-whisper-medium.en/medium.en-encoder.onnx \
  --whisper-decoder=$MODELS/sherpa-onnx-whisper-medium.en/medium.en-decoder.onnx \
  --tokens=$MODELS/sherpa-onnx-whisper-medium.en/medium.en-tokens.txt \
  --port 6008 --doc-root $DOC_ROOT &
echo "  [6008] Whisper medium.en (offline)"

# --- Offline: SenseVoice-small (port 6009) ---
python3 $REPO/non_streaming_server.py \
  --sense-voice=$MODELS/sherpa-onnx-sense-voice-zh-en-ja-ko-yue-2024-07-17/model.int8.onnx \
  --tokens=$MODELS/sherpa-onnx-sense-voice-zh-en-ja-ko-yue-2024-07-17/tokens.txt \
  --port 6009 --doc-root $DOC_ROOT &
echo "  [6009] SenseVoice-small (offline)"

echo "All ASR servers started. Waiting..."
wait
