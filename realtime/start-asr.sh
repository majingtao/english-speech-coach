#!/bin/bash
#
# Start sherpa-onnx ASR container with GPU support.
#
# Usage:
#   ./start-asr.sh          # GPU mode (default)
#   ./start-asr.sh cpu      # CPU-only mode (fallback)
#
# Container: sherpa-rt
# Ports: 6006 (zipformer streaming), 6007 (zipformer-large streaming),
#        6008 (whisper-medium offline), 6009 (sensevoice offline)
#
set -e

CONTAINER_NAME="sherpa-rt"
IMAGE="python:3.10"
WORKSPACE="E:/project/englishAi/realtime"
PORTS="-p 6006:6006 -p 6007:6007 -p 6008:6008 -p 6009:6009"
MODE="${1:-gpu}"

# Models
MODELS_DIR="/workspace/models"
ZIPFORMER_26="${MODELS_DIR}/sherpa-onnx-streaming-zipformer-en-2023-06-26"
ZIPFORMER_21="${MODELS_DIR}/sherpa-onnx-streaming-zipformer-en-2023-06-21"
WHISPER="${MODELS_DIR}/sherpa-onnx-whisper-medium.en"
SENSEVOICE="${MODELS_DIR}/sherpa-onnx-sense-voice-zh-en-ja-ko-yue-2024-07-17"
SERVER_DIR="/workspace/repo/python-api-examples"

echo "========================================"
echo " sherpa-onnx ASR Container Launcher"
echo " Mode: ${MODE}"
echo "========================================"

# Stop existing container if running
if docker ps -q -f name="${CONTAINER_NAME}" 2>/dev/null | grep -q .; then
    echo "[*] Stopping existing container..."
    docker stop "${CONTAINER_NAME}" >/dev/null 2>&1 || true
fi
if docker ps -aq -f name="${CONTAINER_NAME}" 2>/dev/null | grep -q .; then
    echo "[*] Removing existing container..."
    docker rm "${CONTAINER_NAME}" >/dev/null 2>&1 || true
fi

# Build GPU/CPU flags
GPU_FLAGS=""
PROVIDER="cpu"
PIP_EXTRA=""
if [ "${MODE}" = "gpu" ]; then
    GPU_FLAGS="--gpus all"
    PROVIDER="cuda"
    PIP_EXTRA="onnxruntime-gpu"
    echo "[*] GPU mode: will use --gpus all + onnxruntime-gpu"
else
    echo "[*] CPU mode: no GPU acceleration"
fi

echo "[*] Creating container..."
docker run -d \
    --name "${CONTAINER_NAME}" \
    ${GPU_FLAGS} \
    ${PORTS} \
    -v "${WORKSPACE}:/workspace" \
    "${IMAGE}" \
    bash -c "tail -f /dev/null"

echo "[*] Installing dependencies..."
MSYS_NO_PATHCONV=1 docker exec "${CONTAINER_NAME}" \
    pip install --quiet sherpa_onnx==1.12.33 websockets numpy ${PIP_EXTRA}

echo "[*] Starting ASR services..."

# 1. Zipformer EN streaming (port 6006)
echo "    [6006] Zipformer EN (streaming)..."
MSYS_NO_PATHCONV=1 docker exec -d "${CONTAINER_NAME}" \
    python3 ${SERVER_DIR}/streaming_server.py \
    --encoder ${ZIPFORMER_26}/encoder-epoch-99-avg-1-chunk-16-left-128.onnx \
    --decoder ${ZIPFORMER_26}/decoder-epoch-99-avg-1-chunk-16-left-128.onnx \
    --joiner ${ZIPFORMER_26}/joiner-epoch-99-avg-1-chunk-16-left-128.onnx \
    --tokens ${ZIPFORMER_26}/tokens.txt \
    --port 6006 \
    --provider ${PROVIDER} \
    --doc-root ${SERVER_DIR}/web

# 2. Zipformer EN Large streaming (port 6007)
echo "    [6007] Zipformer EN Large (streaming)..."
MSYS_NO_PATHCONV=1 docker exec -d "${CONTAINER_NAME}" \
    python3 ${SERVER_DIR}/streaming_server.py \
    --encoder ${ZIPFORMER_21}/encoder-epoch-99-avg-1.onnx \
    --decoder ${ZIPFORMER_21}/decoder-epoch-99-avg-1.onnx \
    --joiner ${ZIPFORMER_21}/joiner-epoch-99-avg-1.onnx \
    --tokens ${ZIPFORMER_21}/tokens.txt \
    --port 6007 \
    --provider ${PROVIDER} \
    --doc-root ${SERVER_DIR}/web

# 3. Whisper Medium EN offline (port 6008)
echo "    [6008] Whisper Medium EN (offline)..."
MSYS_NO_PATHCONV=1 docker exec -d "${CONTAINER_NAME}" \
    python3 ${SERVER_DIR}/non_streaming_server.py \
    --whisper-encoder=${WHISPER}/medium.en-encoder.onnx \
    --whisper-decoder=${WHISPER}/medium.en-decoder.onnx \
    --tokens=${WHISPER}/medium.en-tokens.txt \
    --port 6008 \
    --provider ${PROVIDER} \
    --doc-root ${SERVER_DIR}/web

# 4. SenseVoice Small offline (port 6009)
echo "    [6009] SenseVoice Small (offline)..."
MSYS_NO_PATHCONV=1 docker exec -d "${CONTAINER_NAME}" \
    python3 ${SERVER_DIR}/non_streaming_server.py \
    --sense-voice=${SENSEVOICE}/model.int8.onnx \
    --tokens=${SENSEVOICE}/tokens.txt \
    --port 6009 \
    --provider ${PROVIDER} \
    --doc-root ${SERVER_DIR}/web

echo ""
echo "[*] Waiting for services to start..."
sleep 3

# Health check
OK=0
for port in 6006 6007 6008 6009; do
    if docker exec "${CONTAINER_NAME}" bash -c "echo | timeout 2 bash -c 'cat > /dev/tcp/localhost/${port}'" 2>/dev/null; then
        echo "    [${port}] OK"
        OK=$((OK + 1))
    else
        echo "    [${port}] waiting..."
    fi
done

if [ ${OK} -lt 4 ]; then
    echo "[*] Some services still starting, waiting 5 more seconds..."
    sleep 5
    for port in 6006 6007 6008 6009; do
        if docker exec "${CONTAINER_NAME}" bash -c "echo | timeout 2 bash -c 'cat > /dev/tcp/localhost/${port}'" 2>/dev/null; then
            echo "    [${port}] OK"
        else
            echo "    [${port}] FAILED — check: docker exec ${CONTAINER_NAME} ps aux"
        fi
    done
fi

echo ""
echo "========================================"
echo " ASR services started (${MODE} mode)"
echo " Container: ${CONTAINER_NAME}"
echo " Ports: 6006, 6007, 6008, 6009"
echo " Provider: ${PROVIDER}"
echo ""
echo " To check logs:"
echo "   docker logs ${CONTAINER_NAME}"
echo " To check processes:"
echo "   docker exec ${CONTAINER_NAME} ps aux"
echo "========================================"
