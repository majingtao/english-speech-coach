#!/bin/bash
#
# Stop sherpa-onnx ASR container.
#
# Usage:
#   ./stop-asr.sh          # Stop and remove container
#   ./stop-asr.sh keep     # Stop but keep container (for restart)
#
set -e

CONTAINER_NAME="sherpa-rt"
MODE="${1:-remove}"

echo "========================================"
echo " Stopping ASR Container: ${CONTAINER_NAME}"
echo "========================================"

if ! docker ps -aq -f name="${CONTAINER_NAME}" 2>/dev/null | grep -q .; then
    echo "[*] Container not found. Nothing to do."
    exit 0
fi

# Show running processes before stopping
echo "[*] Running ASR processes:"
MSYS_NO_PATHCONV=1 docker exec "${CONTAINER_NAME}" ps aux 2>/dev/null | grep python | grep -v grep || echo "    (none)"

echo "[*] Stopping container..."
docker stop "${CONTAINER_NAME}" >/dev/null 2>&1 || true

if [ "${MODE}" = "keep" ]; then
    echo "[*] Container stopped (kept for restart with: docker start ${CONTAINER_NAME})"
else
    echo "[*] Removing container..."
    docker rm "${CONTAINER_NAME}" >/dev/null 2>&1 || true
    echo "[*] Container removed."
fi

echo ""
echo "========================================"
echo " ASR services stopped."
echo " To restart: ./start-asr.sh [gpu|cpu]"
echo "========================================"
