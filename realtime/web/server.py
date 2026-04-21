"""
HTTPS reverse proxy + static file server + multi-model ASR + TTS + LLM proxy.

All traffic goes through one HTTPS port (8443):
  - /ws?model=xxx  → WebSocket proxy to streaming ASR (port per model)
  - /asr?model=xxx → POST audio for offline ASR recognition
  - /asr/models    → List available ASR models
  - /llm/chat      → Unified LLM proxy (Ollama / OpenAI / Claude)
  - /llm/models    → Available LLM models
  - /api/*         → HTTP proxy to Ollama (localhost:11434)
  - /tts           → Edge-TTS or Piper TTS
  - /tts/voices    → Voice list (Edge or Piper)
  - /*             → Static files

Usage:
    pip install aiohttp edge-tts websockets sherpa-onnx numpy
    python server.py
"""

import asyncio
import base64
import copy
import io
import json
import logging
import math
import os
import ssl
import struct
import subprocess
import sys
import uuid

# Force transformers/HuggingFace Hub to work offline — avoids network calls to
# huggingface.co on every startup (fails when the host can't reach HF).
os.environ.setdefault("HF_HUB_OFFLINE", "1")
os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")

# Remove proxy env vars so local websocket/HTTP calls (ASR, Ollama) are never proxied.
# websockets v16+ ignores NO_PROXY and routes through http_proxy, breaking local ASR.
# External API calls (OpenAI, Claude, etc.) configure proxy explicitly via aiohttp.
_saved_http_proxy = os.environ.pop("http_proxy", None) or os.environ.pop("HTTP_PROXY", None)
os.environ.pop("https_proxy", None)
os.environ.pop("HTTPS_PROXY", None)
os.environ.pop("all_proxy", None)
os.environ.pop("ALL_PROXY", None)
import wave
from pathlib import Path

import numpy as np

try:
    import aiohttp
    from aiohttp import web
except ImportError:
    print("Missing: pip install aiohttp")
    sys.exit(1)

try:
    import edge_tts
except ImportError:
    print("Missing: pip install edge-tts")
    sys.exit(1)

try:
    import websockets as ws_lib
    from websockets.legacy.client import connect as ws_legacy_connect
except ImportError:
    print("Missing: pip install websockets")
    sys.exit(1)

try:
    import sherpa_onnx
except ImportError:
    sherpa_onnx = None
    print("[warn] sherpa-onnx not installed, Piper TTS disabled. Run: pip install sherpa-onnx")

try:
    import dashscope
    from dashscope.audio.asr import Recognition, RecognitionCallback, RecognitionResult
    from dashscope import MultiModalConversation
    from dashscope.audio.qwen_tts_realtime import (
        QwenTtsRealtime, QwenTtsRealtimeCallback, AudioFormat,
    )
except ImportError:
    dashscope = None
    Recognition = None
    MultiModalConversation = None
    QwenTtsRealtime = None
    print("[warn] dashscope not installed, Alibaba ASR/TTS disabled. Run: pip install dashscope")


try:
    import torch
except ImportError:
    torch = None

try:
    import soundfile as sf
except ImportError:
    sf = None

try:
    from vibevoice.modular.modeling_vibevoice_streaming_inference import (
        VibeVoiceStreamingForConditionalGenerationInference,
    )
    from vibevoice.processor.vibevoice_streaming_processor import (
        VibeVoiceStreamingProcessor,
    )
except ImportError:
    VibeVoiceStreamingForConditionalGenerationInference = None
    VibeVoiceStreamingProcessor = None

HOST = "0.0.0.0"
PORT = 8443
CERT_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "cert.pem")
KEY_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "key.pem")
STATIC_DIR = os.path.dirname(os.path.abspath(__file__))
MODELS_DIR = os.path.join(os.path.dirname(STATIC_DIR), "models")
PROJECT_ROOT = os.path.dirname(os.path.dirname(STATIC_DIR))
VIBEVOICE_MODEL_DIR = os.environ.get(
    "VIBEVOICE_MODEL_DIR",
    os.path.join(PROJECT_ROOT, "audio", "VibeVoice-Realtime-0.5B"),
)
VIBEVOICE_VOICE_DIR = os.environ.get(
    "VIBEVOICE_VOICE_DIR",
    os.path.join(PROJECT_ROOT, "assets", "vibevoice", "voices"),
)
VIBEVOICE_DEVICE = os.environ.get("VIBEVOICE_DEVICE", "auto")
VIBEVOICE_CFG_SCALE = float(os.environ.get("VIBEVOICE_CFG_SCALE", "1.5"))
ENABLE_VIBEVOICE_TTS = os.environ.get("ENABLE_VIBEVOICE_TTS", "true").lower() not in ("0", "false", "no")


OLLAMA_BASE = "http://localhost:11434"

# ---------- yudao quota client ----------

from quota_client import consume as quota_consume, QuotaError  # noqa: E402


def _quota_rejected_response(err: QuotaError):
    """统一的业务码响应：HTTP 200 + {code, msg}，H5 按 code 展示提示"""
    return web.json_response({"code": err.code, "msg": err.msg, "data": None})


def _client_authorization(request):
    return request.headers.get("Authorization") or request.headers.get("authorization")

# ---------- Load .env ----------

def load_dotenv():
    env_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), ".env")
    if not os.path.exists(env_path):
        return
    with open(env_path, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, _, val = line.partition("=")
            key, val = key.strip(), val.strip().strip('"').strip("'")
            if key and val:
                os.environ.setdefault(key, val)

load_dotenv()

OPENAI_API_KEY = os.environ.get("OPENAI_API_KEY", "")
ANTHROPIC_API_KEY = os.environ.get("ANTHROPIC_API_KEY", "")
MOONSHOT_API_KEY = os.environ.get("MOONSHOT_API_KEY", "")
OPENROUTER_API_KEY = os.environ.get("OPENROUTER_API_KEY", "")
#OPENAI_BASE_URL = os.environ.get("OPENAI_BASE_URL", "https://api.openai.com/v1")
OPENAI_BASE_URL = os.environ.get("OPENAI_BASE_URL", "https://api.gptsapi.net/v1") #openai 中转
ANTHROPIC_BASE_URL = os.environ.get("ANTHROPIC_BASE_URL", "https://api.anthropic.com")
MOONSHOT_BASE_URL = os.environ.get("MOONSHOT_BASE_URL", "https://api.moonshot.cn/v1")
OPENROUTER_BASE_URL = os.environ.get("OPENROUTER_BASE_URL", "https://openrouter.ai/api/v1")
HTTP_PROXY = _saved_http_proxy or ""

# 火山引擎 ASR V2（一句话识别 / 流式识别）
# VOLC_ASR_APPID   = 控制台项目 App ID
# VOLC_ASR_TOKEN   = 控制台项目 Token
# VOLC_ASR_CLUSTER = 集群名称（一句话识别: volcengine_input_common）
VOLC_ASR_APPID = os.environ.get("VOLC_ASR_APPID", "")
VOLC_ASR_TOKEN = os.environ.get("VOLC_ASR_TOKEN", "")
VOLC_ASR_CLUSTER = os.environ.get("VOLC_ASR_CLUSTER", "volcengine_input_common")

# 阿里云百炼 ASR（DashScope）
DASHSCOPE_API_KEY = os.environ.get("DASHSCOPE_API_KEY", "")

# ---------- LLM model registry ----------

# Detect Ollama availability at startup
def _detect_ollama_models():
    """Query Ollama for available models. Returns list of LLM model entries, or [] if unreachable."""
    import urllib.request
    try:
        req = urllib.request.Request(OLLAMA_BASE + "/api/tags", method="GET")
        with urllib.request.urlopen(req, timeout=3) as resp:
            data = json.loads(resp.read())
            models = []
            for m in data.get("models", []):
                name = m.get("name", "")
                if name:
                    models.append({"provider": "ollama", "model": name, "label": f"{name} (Local)", "use_proxy": False})
            return models
    except Exception:
        return []

_ollama_models = _detect_ollama_models()
if _ollama_models:
    print(f"[ollama] Detected {len(_ollama_models)} models: {', '.join(m['model'] for m in _ollama_models)}")
else:
    print("[ollama] Not available — skipping local models")

LLM_MODELS = list(_ollama_models)

if OPENAI_API_KEY:
    LLM_MODELS.extend([
        {"provider": "openai", "model": "gpt-4o-mini", "label": "GPT-4o Mini", "use_proxy": True},
        {"provider": "openai", "model": "gpt-4o", "label": "GPT-4o", "use_proxy": True},
        {"provider": "openai", "model": "gpt-4.1-mini", "label": "GPT-4.1 Mini", "use_proxy": True},
        {"provider": "openai", "model": "gpt-4.1", "label": "GPT-4.1", "use_proxy": True},
    ])

if ANTHROPIC_API_KEY:
    LLM_MODELS.extend([
        {"provider": "claude", "model": "claude-sonnet-4-20250514", "label": "Claude Sonnet 4", "use_proxy": True},
        {"provider": "claude", "model": "claude-haiku-4-5-20251001", "label": "Claude Haiku 4.5", "use_proxy": True},
    ])

if MOONSHOT_API_KEY:
    LLM_MODELS.extend([
        {"provider": "moonshot", "model": "kimi-k2.5", "label": "Kimi K2.5", "use_proxy": False},
        {"provider": "moonshot", "model": "kimi-k2-thinking", "label": "Kimi K2 Thinking", "use_proxy": False},
    ])

if OPENROUTER_API_KEY:
    LLM_MODELS.extend([
        {"provider": "openrouter", "model": "qwen/qwen3.6-plus-preview:free", "label": "Qwen 3.6 Plus Free (OR)", "use_proxy": False},
        {"provider": "openrouter", "model": "google/gemini-2.5-flash", "label": "Gemini 2.5 Flash (OR)", "use_proxy": False},
        {"provider": "openrouter", "model": "google/gemini-2.5-pro", "label": "Gemini 2.5 Pro (OR)", "use_proxy": False},
        {"provider": "openrouter", "model": "deepseek/deepseek-chat-v3", "label": "DeepSeek V3 (OR)", "use_proxy": False},
        {"provider": "openrouter", "model": "qwen/qwen3-235b-a22b", "label": "Qwen3 235B (OR)", "use_proxy": False},
    ])


# ---------- ASR model registry ----------

_LOCAL_ASR_MODELS = [
    {"id": "zipformer-en-2023-06-26", "port": 6006, "type": "streaming", "label": "Zipformer EN (130MB, streaming)"},
    {"id": "zipformer-en-2023-06-21", "port": 6007, "type": "streaming", "label": "Zipformer EN Large (300MB, streaming)"},
    {"id": "whisper-medium-en", "port": 6008, "type": "offline", "label": "Whisper Medium EN (1.5GB, offline)"},
    {"id": "sensevoice-small", "port": 6009, "type": "offline", "label": "SenseVoice Small (230MB, bilingual, offline)"},
]

def _detect_local_asr():
    """Check if any local ASR port is reachable."""
    import socket
    for m in _LOCAL_ASR_MODELS:
        try:
            s = socket.create_connection(("localhost", m["port"]), timeout=1)
            s.close()
            return True
        except Exception:
            continue
    return False

_has_local_asr = _detect_local_asr()
if _has_local_asr:
    print(f"[asr] Local ASR detected (sherpa-rt)")
    ASR_MODELS = list(_LOCAL_ASR_MODELS)
else:
    print("[asr] Local ASR not available — skipping sherpa-onnx models")
    ASR_MODELS = []

if VOLC_ASR_APPID and VOLC_ASR_TOKEN:
    ASR_MODELS.append({
        "id": "volcano-asr",
        "port": 0,
        "type": "online",
        "label": "Volcano ASR (ByteDance, online)",
    })

if DASHSCOPE_API_KEY and dashscope is not None:
    ASR_MODELS.append({
        "id": "dashscope-asr",
        "port": 0,
        "type": "online",
        "label": "Paraformer v2 (Alibaba, realtime)",
    })
    ASR_MODELS.append({
        "id": "qwen3-asr-flash",
        "port": 0,
        "type": "online",
        "label": "Qwen3-ASR-Flash (Alibaba, online)",
    })

ASR_MODEL_MAP = {m["id"]: m for m in ASR_MODELS}

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("proxy")


def generate_cert():
    if os.path.exists(CERT_FILE) and os.path.exists(KEY_FILE):
        return
    log.info("Generating self-signed certificate...")
    subprocess.run([
        "openssl", "req", "-x509", "-newkey", "rsa:2048",
        "-keyout", KEY_FILE, "-out", CERT_FILE,
        "-days", "365", "-nodes",
        "-subj", "/CN=EnglishAI Speech Coach"
    ], check=True)
    log.info("Certificate generated.")


# ---------- Shared session lifecycle ----------

async def create_shared_session(app):
    timeout = aiohttp.ClientTimeout(total=600, connect=10)
    connector = aiohttp.TCPConnector(limit=50, keepalive_timeout=60)
    app["session"] = aiohttp.ClientSession(timeout=timeout, connector=connector)
    log.info("Shared HTTP session created")


async def close_shared_session(app):
    await app["session"].close()
    log.info("Shared HTTP session closed")


# ---------- ASR model list ----------

async def asr_models_handler(request):
    return web.json_response(ASR_MODELS)


# ---------- WebSocket proxy: /ws?model=xxx → streaming ASR ----------

async def ws_proxy(request):
    model_id = request.query.get("model", "zipformer-en-2023-06-26")
    model = ASR_MODEL_MAP.get(model_id)
    if not model or model["type"] != "streaming":
        log.warning("[ws] Invalid streaming model: %s", model_id)
        ws_err = web.WebSocketResponse()
        await ws_err.prepare(request)
        await ws_err.close(message=b"Invalid streaming model")
        return ws_err

    asr_url = f"ws://localhost:{model['port']}"
    session = request.app["session"]
    ws_client = web.WebSocketResponse(
        max_msg_size=4 * 1024 * 1024,
        heartbeat=20,
    )
    await ws_client.prepare(request)
    log.info("[ws] Client %s → %s (%s)", request.remote, model_id, asr_url)

    try:
        ws_server = await session.ws_connect(
            asr_url, max_msg_size=4 * 1024 * 1024, heartbeat=20,
        )
    except Exception as e:
        log.error("[ws] Cannot connect to ASR %s: %s", asr_url, e)
        await ws_client.close(message=b"ASR unavailable")
        return ws_client

    done = asyncio.Event()

    async def client_to_server():
        try:
            async for msg in ws_client:
                if ws_server.closed:
                    break
                if msg.type == aiohttp.WSMsgType.TEXT:
                    await ws_server.send_str(msg.data)
                elif msg.type == aiohttp.WSMsgType.BINARY:
                    await ws_server.send_bytes(msg.data)
                elif msg.type == aiohttp.WSMsgType.ERROR:
                    break
        except (ConnectionResetError, asyncio.CancelledError):
            pass
        except Exception as e:
            log.warning("[ws] c2s error: %s", e)
        finally:
            done.set()

    async def server_to_client():
        try:
            async for msg in ws_server:
                if ws_client.closed:
                    break
                if msg.type == aiohttp.WSMsgType.TEXT:
                    await ws_client.send_str(msg.data)
                elif msg.type == aiohttp.WSMsgType.BINARY:
                    await ws_client.send_bytes(msg.data)
                elif msg.type == aiohttp.WSMsgType.ERROR:
                    break
        except (ConnectionResetError, asyncio.CancelledError):
            pass
        except Exception as e:
            log.warning("[ws] s2c error: %s", e)
        finally:
            done.set()

    t1 = asyncio.create_task(client_to_server())
    t2 = asyncio.create_task(server_to_client())
    await done.wait()
    t1.cancel()
    t2.cancel()
    await asyncio.gather(t1, t2, return_exceptions=True)

    if not ws_server.closed:
        await ws_server.close()
    if not ws_client.closed:
        await ws_client.close()

    log.info("[ws] Closed %s for %s", model_id, request.remote)
    return ws_client


# ---------- Volcano Engine ASR V2 (WebSocket binary protocol) ----------
# 完全对齐官方 streaming_asr_demo.py 协议

import gzip as gzip_mod

VOLC_ASR_WS_URL = "wss://openspeech.bytedance.com/api/v2/asr"

# Message types
_VOLC_FULL_CLIENT_REQUEST  = 0b0001
_VOLC_AUDIO_ONLY           = 0b0010
_VOLC_SERVER_FULL_RESPONSE = 0b1001
_VOLC_SERVER_ACK           = 0b1011
_VOLC_SERVER_ERROR         = 0b1111

# Flags
_VOLC_NO_SEQ  = 0b0000
_VOLC_NEG_SEQ = 0b0010

# Serialization / Compression
_VOLC_JSON = 0b0001
_VOLC_GZIP = 0b0001

SUCCESS_CODE = 1000


def _volc_build_header(msg_type, flags=_VOLC_NO_SEQ, serial=_VOLC_JSON, compress=_VOLC_GZIP):
    return bytes([
        (0x01 << 4) | 0x01,       # version=1, header_size=1
        (msg_type << 4) | flags,
        (serial << 4) | compress,
        0x00,
    ])


def _volc_pack(header: bytes, payload: bytes) -> bytes:
    """header(4) + payload_size(4 big-endian) + payload"""
    return header + len(payload).to_bytes(4, "big") + payload


def _volc_parse_response(data: bytes):
    """Parse server response, matching official demo parse_response()."""
    if len(data) < 4:
        return {"error": "response too short"}
    header_size = data[0] & 0x0F
    msg_type = (data[1] >> 4) & 0x0F
    msg_flags = data[1] & 0x0F
    serial_method = (data[2] >> 4) & 0x0F
    compress = data[2] & 0x0F
    payload = data[header_size * 4:]
    result = {"msg_type": msg_type, "flags": msg_flags}

    payload_msg = None
    if msg_type == _VOLC_SERVER_FULL_RESPONSE:
        if len(payload) >= 4:
            payload_size = int.from_bytes(payload[:4], "big", signed=True)
            payload_msg = payload[4:]
    elif msg_type == _VOLC_SERVER_ACK:
        seq = int.from_bytes(payload[:4], "big", signed=True)
        result["seq"] = seq
        if len(payload) >= 8:
            payload_size = int.from_bytes(payload[4:8], "big", signed=False)
            payload_msg = payload[8:]
    elif msg_type == _VOLC_SERVER_ERROR:
        if len(payload) >= 4:
            code = int.from_bytes(payload[:4], "big", signed=False)
            result["code"] = code
        if len(payload) >= 8:
            payload_size = int.from_bytes(payload[4:8], "big", signed=False)
            payload_msg = payload[8:]

    if payload_msg is not None:
        if compress == _VOLC_GZIP:
            payload_msg = gzip_mod.decompress(payload_msg)
        if serial_method == _VOLC_JSON:
            payload_msg = json.loads(payload_msg.decode("utf-8"))
        result["payload_msg"] = payload_msg
    return result


async def asr_volcano(session, pcm_float32: bytes, sample_rate: int = 16000):
    """Volcano Engine ASR V2 — matches official streaming_asr_demo.py protocol.
    Input: raw float32 PCM → int16 → chunk upload → get text result.
    """
    import websockets

    samples = np.frombuffer(pcm_float32, dtype=np.float32)
    int16_samples = np.clip(samples * 32767, -32768, 32767).astype(np.int16)
    audio_bytes = int16_samples.tobytes()
    duration = len(samples) / sample_rate
    reqid = str(uuid.uuid4())
    log.info("[asr] volcano v2: %.1fs audio, %d samples, reqid=%s",
             duration, len(samples), reqid[:8])

    # Auth header — Bearer; {token}（官方 demo token_auth）
    ws_headers = {"Authorization": f"Bearer; {VOLC_ASR_TOKEN}"}

    # Full client request config（官方 demo construct_request）
    config = {
        "app": {
            "appid": VOLC_ASR_APPID,
            "cluster": VOLC_ASR_CLUSTER,
            "token": VOLC_ASR_TOKEN,
        },
        "user": {
            "uid": "esc-h5",
        },
        "audio": {
            "format": "raw",
            "rate": sample_rate,
            "bits": 16,
            "channel": 1,
            "language": "en",
            "codec": "raw",
        },
        "request": {
            "reqid": reqid,
            "workflow": "audio_in,resample,partition,vad,fe,decode,itn,nlu_punctuate",
            "sequence": 1,
            "nbest": 1,
            "show_utterances": True,
            "result_type": "full",
        },
    }

    log.info("[asr] volcano v2 appid=%s cluster=%s", VOLC_ASR_APPID, VOLC_ASR_CLUSTER)

    try:
        # 1) Build full_client_request frame
        config_gz = gzip_mod.compress(json.dumps(config).encode("utf-8"))
        header = _volc_build_header(_VOLC_FULL_CLIENT_REQUEST)
        full_request = _volc_pack(header, config_gz)

        # 2) Connect & send (使用 websockets 库，和官方 demo 一致)
        async with websockets.connect(
            VOLC_ASR_WS_URL,
            additional_headers=ws_headers,
            max_size=1000000000,
        ) as ws:
            def _get_code(pm):
                """Extract code from payload_msg, handles both dict and other types."""
                if isinstance(pm, dict):
                    return pm.get("code", SUCCESS_CODE)
                return SUCCESS_CODE

            def _get_msg(pm):
                if isinstance(pm, dict):
                    return pm.get("message", "unknown")
                return str(pm)[:200]

            # Send config
            await ws.send(full_request)
            res = await ws.recv()
            result = _volc_parse_response(res)
            log.debug("[asr] volcano v2 config resp: %s",
                      json.dumps(result.get("payload_msg"), ensure_ascii=False, default=str)[:300]
                      if "payload_msg" in result else "no payload")
            if "payload_msg" in result:
                code = _get_code(result["payload_msg"])
                if code != SUCCESS_CODE:
                    msg = _get_msg(result["payload_msg"])
                    log.error("[asr] volcano v2 config rejected: %s", msg)
                    return {"error": f"Volcano ASR: {msg}"}

            # Send audio chunks (~1s each, gzip compressed)
            chunk_size = sample_rate * 2  # 1 second of int16
            total = len(audio_bytes)
            offset = 0
            final_result = result
            while offset < total:
                end = min(offset + chunk_size, total)
                is_last = (end >= total)
                chunk = audio_bytes[offset:end]
                chunk_gz = gzip_mod.compress(chunk)

                if is_last:
                    audio_header = _volc_build_header(_VOLC_AUDIO_ONLY, flags=_VOLC_NEG_SEQ)
                else:
                    audio_header = _volc_build_header(_VOLC_AUDIO_ONLY, flags=_VOLC_NO_SEQ)

                frame = _volc_pack(audio_header, chunk_gz)
                await ws.send(frame)
                res = await ws.recv()
                result = _volc_parse_response(res)
                if "payload_msg" in result:
                    code = _get_code(result["payload_msg"])
                    if code != SUCCESS_CODE:
                        msg = _get_msg(result["payload_msg"])
                        log.error("[asr] volcano v2 chunk error: %s", msg)
                        return {"error": f"Volcano ASR: {msg}"}
                final_result = result
                offset = end

            # Extract text from final result
            log.info("[asr] volcano v2 final payload: %s",
                     json.dumps(final_result.get("payload_msg"), ensure_ascii=False, default=str)[:500])
            text = ""
            pm = final_result.get("payload_msg")
            if isinstance(pm, dict):
                # result 是 list（一句话识别返回格式）
                res_field = pm.get("result", [])
                if isinstance(res_field, list):
                    for item in res_field:
                        if isinstance(item, dict) and item.get("text"):
                            text += item["text"] + " "
                elif isinstance(res_field, dict):
                    for u in res_field.get("utterances", []):
                        if isinstance(u, dict):
                            text += u.get("text", "") + " "
                # 兜底: 直接取顶层 text
                if not text.strip():
                    text = pm.get("text", "")

            log.info("[asr] volcano v2 result: %s", text.strip()[:100])
            return {"text": text.strip()}

    except Exception as e:
        log.error("[asr] volcano v2 exception: %s", e)
        return {"error": f"Volcano ASR: {e}"}


# ---------- DashScope ASR (阿里云百炼) ----------

async def asr_dashscope(pcm_float32: bytes, sample_rate: int = 16000):
    """Alibaba DashScope ASR — uses dashscope SDK Recognition class.
    Input: raw float32 PCM → int16 PCM → feed in chunks → collect text.
    """
    import threading

    samples = np.frombuffer(pcm_float32, dtype=np.float32)
    int16_samples = np.clip(samples * 32767, -32768, 32767).astype(np.int16)
    audio_bytes = int16_samples.tobytes()
    duration = len(samples) / sample_rate
    log.info("[asr] dashscope: %.1fs audio, %d samples", duration, len(samples))

    # Callback collects final sentence texts
    collected_texts = []
    error_msg = [None]
    done_event = threading.Event()

    class _Callback(RecognitionCallback):
        def on_open(self):
            log.debug("[asr] dashscope: connection opened")

        def on_close(self):
            log.debug("[asr] dashscope: connection closed")

        def on_complete(self):
            log.debug("[asr] dashscope: recognition completed")
            done_event.set()

        def on_error(self, message):
            log.error("[asr] dashscope error: %s", message.message if hasattr(message, 'message') else message)
            error_msg[0] = str(message.message if hasattr(message, 'message') else message)
            done_event.set()

        def on_event(self, result: RecognitionResult):
            sentence = result.get_sentence()
            if sentence and 'text' in sentence:
                if RecognitionResult.is_sentence_end(sentence):
                    collected_texts.append(sentence['text'])
                    log.debug("[asr] dashscope sentence: %s", sentence['text'][:100])

    def _run_recognition():
        try:
            dashscope.api_key = DASHSCOPE_API_KEY
            callback = _Callback()
            recognition = Recognition(
                model='paraformer-realtime-v2',
                format='pcm',
                sample_rate=sample_rate,
                semantic_punctuation_enabled=False,
                callback=callback,
            )
            recognition.start()

            # Feed audio in chunks (~200ms each = 3200 samples * 2 bytes)
            chunk_size = 3200  # 200ms at 16kHz, int16
            offset = 0
            while offset < len(audio_bytes):
                end = min(offset + chunk_size, len(audio_bytes))
                recognition.send_audio_frame(audio_bytes[offset:end])
                offset = end

            recognition.stop()
        except Exception as e:
            error_msg[0] = str(e)
            done_event.set()

    # Run SDK in a thread (it's synchronous/callback-based)
    loop = asyncio.get_event_loop()
    await loop.run_in_executor(None, _run_recognition)

    # Wait for completion (should already be done after stop())
    done_event.wait(timeout=30)

    if error_msg[0]:
        return {"error": f"DashScope ASR: {error_msg[0]}"}

    text = " ".join(collected_texts).strip()
    log.info("[asr] dashscope result: %s", text[:100] if text else "(empty)")
    return {"text": text}


# ---------- Qwen3-ASR-Flash (阿里云百炼) ----------

async def asr_qwen3_flash(pcm_float32: bytes, sample_rate: int = 16000):
    """Qwen3-ASR-Flash — uses MultiModalConversation.call() with base64 WAV.
    Input: raw float32 PCM → int16 → WAV bytes → base64 → API call.
    """
    # Convert float32 PCM to int16 WAV in memory
    samples = np.frombuffer(pcm_float32, dtype=np.float32)
    int16_samples = np.clip(samples * 32767, -32768, 32767).astype(np.int16)
    duration = len(samples) / sample_rate
    log.info("[asr] qwen3-asr-flash: %.1fs audio, %d samples", duration, len(samples))

    wav_buf = io.BytesIO()
    with wave.open(wav_buf, "wb") as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        wf.writeframes(int16_samples.tobytes())
    wav_bytes = wav_buf.getvalue()
    audio_b64 = base64.b64encode(wav_bytes).decode("utf-8")

    messages = [
        {
            "role": "user",
            "content": [
                {"audio": f"data:audio/wav;base64,{audio_b64}"},
            ],
        }
    ]

    def _call():
        dashscope.api_key = DASHSCOPE_API_KEY
        return MultiModalConversation.call(
            model="qwen3-asr-flash",
            messages=messages,
        )

    try:
        loop = asyncio.get_event_loop()
        response = await loop.run_in_executor(None, _call)

        if response.status_code != 200:
            log.error("[asr] qwen3-asr-flash error: %s %s",
                      response.status_code, response.message)
            return {"error": f"Qwen3-ASR-Flash: {response.message}"}

        # Extract text from response
        text = ""
        output = response.output
        if hasattr(output, "choices") and output.choices:
            choice = output.choices[0]
            msg = choice.message if hasattr(choice, "message") else choice.get("message", {})
            content = msg.content if hasattr(msg, "content") else msg.get("content", [])
            if isinstance(content, str):
                text = content
            elif isinstance(content, list):
                for item in content:
                    if isinstance(item, dict) and "text" in item:
                        text += item["text"]
                    elif isinstance(item, str):
                        text += item

        log.info("[asr] qwen3-asr-flash result: %s", text.strip()[:100] if text else "(empty)")
        return {"text": text.strip()}

    except Exception as e:
        log.error("[asr] qwen3-asr-flash exception: %s", e)
        return {"error": f"Qwen3-ASR-Flash: {e}"}


# ---------- Offline/Online ASR: POST /asr?model=xxx ----------

async def asr_offline(request):
    model_id = request.query.get("model", "whisper-medium-en")
    model = ASR_MODEL_MAP.get(model_id)
    if not model:
        return web.json_response({"error": "Invalid ASR model"}, status=400)
    if model["type"] not in ("offline", "online"):
        return web.json_response({"error": "Model does not support POST"}, status=400)

    audio_bytes = await request.read()
    if not audio_bytes:
        return web.json_response({"error": "No audio data"}, status=400)

    # Assume raw float32 PCM at 16kHz from frontend
    sample_rate = 16000
    pcm_data = audio_bytes

    # If WAV header detected, parse it
    if audio_bytes[:4] == b"RIFF":
        sample_rate = int.from_bytes(audio_bytes[24:28], "little")
        bits_per_sample = int.from_bytes(audio_bytes[34:36], "little")
        # Find 'data' chunk
        data_offset = audio_bytes.find(b"data")
        if data_offset < 0:
            return web.json_response({"error": "Invalid WAV"}, status=400)
        data_size = int.from_bytes(audio_bytes[data_offset + 4:data_offset + 8], "little")
        raw_pcm = audio_bytes[data_offset + 8:data_offset + 8 + data_size]
        if bits_per_sample == 16:
            samples = np.frombuffer(raw_pcm, dtype=np.int16).astype(np.float32) / 32768.0
        else:
            samples = np.frombuffer(raw_pcm, dtype=np.float32)
        pcm_data = samples.astype(np.float32).tobytes()

    # --- 配额扣减：按实际音频秒数（不足 1 秒按 1 秒） ---
    _samples_for_duration = np.frombuffer(pcm_data, dtype=np.float32)
    _duration_sec = len(_samples_for_duration) / max(sample_rate, 1)
    try:
        await quota_consume(
            request.app["session"],
            _client_authorization(request),
            "asr",
            max(1, int(math.ceil(_duration_sec))),
        )
    except QuotaError as qe:
        return _quota_rejected_response(qe)

    # ---------- 在线 ASR（火山引擎等）----------
    if model["type"] == "online":
        session = request.app["session"]
        if model_id == "volcano-asr":
            result = await asr_volcano(session, pcm_data, sample_rate)
        elif model_id == "dashscope-asr":
            result = await asr_dashscope(pcm_data, sample_rate)
        elif model_id == "qwen3-asr-flash":
            result = await asr_qwen3_flash(pcm_data, sample_rate)
        else:
            return web.json_response({"error": f"Unknown online model: {model_id}"}, status=400)
        if "error" in result:
            return web.json_response(result, status=502)
        return web.json_response(result)

    # ---------- 本地离线 ASR（sherpa-onnx websocket）----------
    port = model["port"]
    header = struct.pack("<ii", sample_rate, len(pcm_data))

    # Debug: check audio content
    samples = np.frombuffer(pcm_data, dtype=np.float32)
    duration = len(samples) / sample_rate
    max_val = float(np.max(np.abs(samples))) if len(samples) > 0 else 0
    log.info("[asr] %s audio=%d bytes (%.1fs, max=%.4f, sr=%d) → port %d",
             model_id, len(pcm_data), duration, max_val, sample_rate, port)

    try:
        async with ws_legacy_connect(
            f"ws://localhost:{port}",
            max_size=50 * 1024 * 1024,  # 50MB for large audio
            ping_interval=None,
        ) as ws:
            await ws.send(header + pcm_data)
            await ws.send("Done")
            result = await asyncio.wait_for(ws.recv(), timeout=60)
    except asyncio.TimeoutError:
        log.error("[asr] Recognition timed out for %s", model_id)
        return web.json_response({"error": "Recognition timed out"}, status=504)
    except Exception as e:
        log.error("[asr] Error connecting to %s: %s", model_id, e)
        return web.json_response({"error": f"ASR error: {e}"}, status=502)

    text = result if result and result != "<EMPTY>" else ""
    log.info("[asr] %s result: %s", model_id, text[:100])
    return web.json_response({"text": text.strip()})


# ---------- HTTP proxy: /api/* → Ollama ----------

async def ollama_proxy(request):
    session = request.app["session"]
    target_url = OLLAMA_BASE + request.path_qs
    body = await request.read()
    headers = {}
    if request.content_type:
        headers["Content-Type"] = request.content_type

    log.info("[api] %s %s", request.method, request.path_qs)

    try:
        resp = await session.request(
            request.method, target_url, headers=headers, data=body,
        )
    except Exception as e:
        log.error("[api] Ollama connection failed: %s", e)
        return web.Response(text=f"Ollama proxy error: {e}", status=502)

    try:
        response = web.StreamResponse(
            status=resp.status,
            headers={"Content-Type": resp.headers.get("Content-Type", "application/json")},
        )
        await response.prepare(request)
        async for chunk in resp.content.iter_any():
            await response.write(chunk)
        await response.write_eof()
    except (ConnectionResetError, asyncio.CancelledError):
        log.warning("[api] Client disconnected")
    except Exception as e:
        log.warning("[api] Streaming error: %s", e)
    finally:
        resp.release()

    return response


# ---------- LLM proxy: /llm/chat + /llm/models + /llm/judge ----------

async def llm_models_handler(request):
    return web.json_response(LLM_MODELS)


JUDGE_SYSTEM_PROMPT = """You are a strict English exam judge for children (A2 level, age 7-12).
Reply ONLY with this JSON, nothing else:
{"ok":true} or {"ok":false,"fb":"brief feedback in English","cn":"简短中文提示","ans":"correct answer"}

Rules:
- Grammar must be correct
- Spelling must be correct
- If an expected answer is provided, key words must match (not paraphrase)
- Minor differences in articles/prepositions are OK if meaning is the same
- IGNORE all punctuation — missing or wrong punctuation (periods, question marks, commas, etc.) is NEVER an error
- If the expected answer starts with "(open-ended", only judge grammar and relevance to the question. Accept any grammatically correct answer that addresses the question. Do NOT require specific content or length. The "sample" is just a reference, not the required answer.
- The "ans" field in your response must be a SHORT corrected version of the student's answer, never include the question text
- Do NOT explain, do NOT teach, do NOT add any text outside the JSON"""


async def llm_judge(request):
    """Non-streaming LLM judge for exam mode. Returns JSON verdict."""
    try:
        body = await request.json()
    except Exception:
        return web.json_response({"ok": False, "fb": "Invalid request", "cn": "请求格式错误", "ans": ""}, status=400)

    provider = body.get("provider", "openai")
    model = body.get("model", "gpt-4o")
    use_proxy = body.get("use_proxy", False)
    proxy = HTTP_PROXY if (use_proxy and HTTP_PROXY) else None
    question = body.get("question", "")
    expected = body.get("expected", "")
    student = body.get("student", "")
    # "local" is an alias for ollama (local inference)
    if provider == "local":
        provider = "ollama"

    if not student:
        return web.json_response({"ok": False, "fb": "No answer provided", "cn": "没有回答", "ans": expected})

    # --- 配额扣减：judge 算 1 次 LLM ---
    try:
        await quota_consume(request.app["session"], _client_authorization(request), "llm", 1)
    except QuotaError as qe:
        return _quota_rejected_response(qe)

    # Build the user message
    parts = []
    if question:
        parts.append(f'Examiner: "{question}"')
    if expected:
        parts.append(f'Expected answer: "{expected}"')
    parts.append(f'Student said: "{student}"')
    parts.append("Judge:")
    user_msg = "\n".join(parts)

    messages = [
        {"role": "system", "content": JUDGE_SYSTEM_PROMPT},
        {"role": "user", "content": user_msg},
    ]

    log.info("[judge] provider=%s model=%s q=%s student=%s", provider, model, question[:50], student[:50])

    session = request.app["session"]

    try:
        if provider == "ollama":
            coro = _judge_ollama(session, model, messages)
        elif provider == "openai":
            coro = _judge_openai(session, model, messages, proxy)
        elif provider == "claude":
            coro = _judge_claude(session, model, messages, proxy)
        elif provider == "moonshot":
            coro = _judge_openai_compat(session, model, messages, MOONSHOT_BASE_URL, MOONSHOT_API_KEY, proxy)
        elif provider == "openrouter":
            coro = _judge_openai_compat(session, model, messages, OPENROUTER_BASE_URL, OPENROUTER_API_KEY, proxy)
        else:
            return web.json_response({"ok": False, "fb": "Unknown provider", "cn": "未知提供商", "ans": expected})
        timeout = 60 if provider == "ollama" else 25
        result = await asyncio.wait_for(coro, timeout=timeout)
    except asyncio.TimeoutError:
        log.error("[judge] Timed out: provider=%s model=%s", provider, model)
        result = {"ok": False, "fb": "Judge timed out", "cn": "判分超时", "ans": expected}
    except Exception as e:
        log.error("[judge] Error: %s", e)
        result = {"ok": False, "fb": f"Judge error: {e}", "cn": "判分出错", "ans": expected}

    log.info("[judge] result: %s", json.dumps(result, ensure_ascii=False)[:200])
    return web.json_response(result)


def _parse_judge_json(text):
    """Try to parse JSON from LLM response, with fallback."""
    text = text.strip()
    # Try direct parse
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        pass
    # Try to find JSON in the text
    start = text.find("{")
    end = text.rfind("}") + 1
    if start >= 0 and end > start:
        try:
            return json.loads(text[start:end])
        except json.JSONDecodeError:
            pass
    return None


async def _judge_ollama(session, model, messages):
    """Non-streaming Ollama judge call."""
    payload = json.dumps({"model": model, "messages": messages, "stream": False, "format": "json"})
    async with session.post(
        OLLAMA_BASE + "/api/chat",
        data=payload,
        headers={"Content-Type": "application/json"},
    ) as resp:
        data = await resp.json()
        content = data.get("message", {}).get("content", "")
        result = _parse_judge_json(content)
        if result and "ok" in result:
            return result
        return {"ok": False, "fb": "Could not parse response", "cn": "无法解析回复", "ans": ""}


async def _judge_openai(session, model, messages, proxy=None):
    """Non-streaming OpenAI judge call with response_format."""
    payload = json.dumps({
        "model": model, "messages": messages, "stream": False,
        "response_format": {"type": "json_object"},
    })
    url = OPENAI_BASE_URL.rstrip("/") + "/chat/completions"
    kwargs = {
        "data": payload,
        "headers": {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + OPENAI_API_KEY,
        },
    }
    if proxy:
        kwargs["proxy"] = proxy
    async with session.post(url, **kwargs) as resp:
        data = await resp.json()
        if resp.status != 200:
            err = data.get("error", {}).get("message", str(data))
            return {"ok": False, "fb": f"OpenAI error: {err[:100]}", "cn": "API错误", "ans": ""}
        content = data.get("choices", [{}])[0].get("message", {}).get("content", "")
        result = _parse_judge_json(content)
        if result and "ok" in result:
            return result
        return {"ok": False, "fb": "Could not parse response", "cn": "无法解析回复", "ans": ""}


async def _judge_claude(session, model, messages, proxy=None):
    """Non-streaming Claude judge call."""
    system_text = ""
    chat_msgs = []
    for m in messages:
        if m["role"] == "system":
            system_text += m["content"] + "\n"
        else:
            chat_msgs.append({"role": m["role"], "content": m["content"]})

    payload = {"model": model, "max_tokens": 256, "messages": chat_msgs}
    if system_text.strip():
        payload["system"] = system_text.strip()

    url = ANTHROPIC_BASE_URL.rstrip("/") + "/v1/messages"
    kwargs = {
        "data": json.dumps(payload),
        "headers": {
            "Content-Type": "application/json",
            "x-api-key": ANTHROPIC_API_KEY,
            "anthropic-version": "2023-06-01",
        },
    }
    if proxy:
        kwargs["proxy"] = proxy
    async with session.post(url, **kwargs) as resp:
        data = await resp.json()
        if resp.status != 200:
            err = data.get("error", {}).get("message", str(data))
            return {"ok": False, "fb": f"Claude error: {err[:100]}", "cn": "API错误", "ans": ""}
        content = ""
        for block in data.get("content", []):
            if block.get("type") == "text":
                content += block.get("text", "")
        result = _parse_judge_json(content)
        if result and "ok" in result:
            return result
        return {"ok": False, "fb": "Could not parse response", "cn": "无法解析回复", "ans": ""}


async def _judge_openai_compat(session, model, messages, base_url, api_key, proxy=None):
    """Non-streaming OpenAI-compatible judge call (Moonshot etc.)."""
    payload = json.dumps({
        "model": model, "messages": messages, "stream": False,
        "response_format": {"type": "json_object"},
    })
    url = base_url.rstrip("/") + "/chat/completions"
    kwargs = {
        "data": payload,
        "headers": {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + api_key,
        },
    }
    if proxy:
        kwargs["proxy"] = proxy
    async with session.post(url, **kwargs) as resp:
        data = await resp.json()
        if resp.status != 200:
            err = data.get("error", {}).get("message", str(data))
            return {"ok": False, "fb": f"API error: {err[:100]}", "cn": "API错误", "ans": ""}
        content = data.get("choices", [{}])[0].get("message", {}).get("content", "")
        result = _parse_judge_json(content)
        if result and "ok" in result:
            return result
        return {"ok": False, "fb": "Could not parse response", "cn": "无法解析回复", "ans": ""}


async def llm_chat(request):
    """Unified LLM streaming proxy. Accepts provider/model/messages, returns unified SSE."""
    try:
        body = await request.json()
    except Exception:
        return web.Response(text="Invalid JSON", status=400)

    provider = body.get("provider", "ollama")
    model = body.get("model", "qwen2.5:7b")
    messages = body.get("messages", [])
    use_proxy = body.get("use_proxy", False)
    proxy = HTTP_PROXY if (use_proxy and HTTP_PROXY) else None
    # "local" is an alias for ollama (local inference)
    if provider == "local":
        provider = "ollama"

    if not messages:
        return web.Response(text="No messages", status=400)

    session = request.app["session"]

    # --- 配额扣减：chat 算 1 次 LLM ---
    try:
        await quota_consume(session, _client_authorization(request), "llm", 1)
    except QuotaError as qe:
        return _quota_rejected_response(qe)

    log.info("[llm] provider=%s model=%s msgs=%d proxy=%s", provider, model, len(messages), bool(proxy))

    response = web.StreamResponse(
        status=200,
        headers={"Content-Type": "text/plain", "Cache-Control": "no-cache"},
    )
    await response.prepare(request)

    try:
        if provider == "ollama":
            await _stream_ollama(session, response, model, messages)
        elif provider == "openai":
            await _stream_openai(session, response, model, messages, proxy)
        elif provider == "claude":
            await _stream_claude(session, response, model, messages, proxy)
        elif provider == "moonshot":
            await _stream_openai_compat(session, response, model, messages,
                                        MOONSHOT_BASE_URL, MOONSHOT_API_KEY, "Moonshot", proxy)
        elif provider == "openrouter":
            await _stream_openai_compat(session, response, model, messages,
                                        OPENROUTER_BASE_URL, OPENROUTER_API_KEY, "OpenRouter", proxy)
        else:
            await response.write(json.dumps({"content": "Unknown provider: " + provider, "done": True}).encode() + b"\n")
    except (ConnectionResetError, asyncio.CancelledError):
        log.warning("[llm] Client disconnected")
    except Exception as e:
        log.error("[llm] Error: %s", e)
        try:
            await response.write(json.dumps({"content": f"\n[Error: {e}]", "done": True}).encode() + b"\n")
        except Exception:
            pass

    try:
        await response.write_eof()
    except Exception:
        pass
    return response


async def _stream_ollama(session, response, model, messages):
    """Forward to Ollama /api/chat and convert to unified format."""
    payload = json.dumps({"model": model, "messages": messages, "stream": True})
    async with session.post(
        OLLAMA_BASE + "/api/chat",
        data=payload,
        headers={"Content-Type": "application/json"},
    ) as resp:
        async for raw_line in resp.content:
            line = raw_line.decode("utf-8").strip()
            if not line:
                continue
            try:
                obj = json.loads(line)
                content = ""
                if obj.get("message") and obj["message"].get("content"):
                    content = obj["message"]["content"]
                done = obj.get("done", False)
                await response.write(json.dumps({"content": content, "done": done}).encode() + b"\n")
            except json.JSONDecodeError:
                pass


async def _stream_openai(session, response, model, messages, proxy=None):
    """Call OpenAI chat completions API and convert to unified format."""
    payload = json.dumps({"model": model, "messages": messages, "stream": True})
    url = OPENAI_BASE_URL.rstrip("/") + "/chat/completions"
    kwargs = {
        "data": payload,
        "headers": {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + OPENAI_API_KEY,
        },
    }
    if proxy:
        kwargs["proxy"] = proxy
    async with session.post(url, **kwargs) as resp:
        if resp.status != 200:
            err = await resp.text()
            await response.write(json.dumps({"content": f"[OpenAI error {resp.status}: {err[:200]}]", "done": True}).encode() + b"\n")
            return
        async for raw_line in resp.content:
            line = raw_line.decode("utf-8").strip()
            if not line or not line.startswith("data:"):
                continue
            data = line[5:].strip()
            if data == "[DONE]":
                await response.write(json.dumps({"content": "", "done": True}).encode() + b"\n")
                return
            try:
                obj = json.loads(data)
                delta = obj.get("choices", [{}])[0].get("delta", {})
                content = delta.get("content", "")
                if content:
                    await response.write(json.dumps({"content": content, "done": False}).encode() + b"\n")
            except json.JSONDecodeError:
                pass
    await response.write(json.dumps({"content": "", "done": True}).encode() + b"\n")


async def _stream_openai_compat(session, response, model, messages, base_url, api_key, name="API", proxy=None):
    """Call any OpenAI-compatible API (Moonshot, etc.) and convert to unified format."""
    payload = json.dumps({"model": model, "messages": messages, "stream": True})
    url = base_url.rstrip("/") + "/chat/completions"
    kwargs = {
        "data": payload,
        "headers": {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + api_key,
        },
    }
    if proxy:
        kwargs["proxy"] = proxy
    async with session.post(url, **kwargs) as resp:
        if resp.status != 200:
            err = await resp.text()
            await response.write(json.dumps({"content": f"[{name} error {resp.status}: {err[:200]}]", "done": True}).encode() + b"\n")
            return
        async for raw_line in resp.content:
            line = raw_line.decode("utf-8").strip()
            if not line or not line.startswith("data:"):
                continue
            data = line[5:].strip()
            if data == "[DONE]":
                await response.write(json.dumps({"content": "", "done": True}).encode() + b"\n")
                return
            try:
                obj = json.loads(data)
                delta = obj.get("choices", [{}])[0].get("delta", {})
                content = delta.get("content", "")
                if content:
                    await response.write(json.dumps({"content": content, "done": False}).encode() + b"\n")
            except json.JSONDecodeError:
                pass
    await response.write(json.dumps({"content": "", "done": True}).encode() + b"\n")


async def _stream_claude(session, response, model, messages, proxy=None):
    """Call Claude messages API and convert to unified format."""
    # Extract system message
    system_text = ""
    chat_msgs = []
    for m in messages:
        if m["role"] == "system":
            system_text += m["content"] + "\n"
        else:
            chat_msgs.append({"role": m["role"], "content": m["content"]})

    payload = {
        "model": model,
        "max_tokens": 4096,
        "stream": True,
        "messages": chat_msgs,
    }
    if system_text.strip():
        payload["system"] = system_text.strip()

    url = ANTHROPIC_BASE_URL.rstrip("/") + "/v1/messages"
    kwargs = {
        "data": json.dumps(payload),
        "headers": {
            "Content-Type": "application/json",
            "x-api-key": ANTHROPIC_API_KEY,
            "anthropic-version": "2023-06-01",
        },
    }
    if proxy:
        kwargs["proxy"] = proxy
    async with session.post(url, **kwargs) as resp:
        if resp.status != 200:
            err = await resp.text()
            await response.write(json.dumps({"content": f"[Claude error {resp.status}: {err[:200]}]", "done": True}).encode() + b"\n")
            return
        async for raw_line in resp.content:
            line = raw_line.decode("utf-8").strip()
            if not line or not line.startswith("data:"):
                continue
            data = line[5:].strip()
            try:
                obj = json.loads(data)
                evt_type = obj.get("type", "")
                if evt_type == "content_block_delta":
                    text = obj.get("delta", {}).get("text", "")
                    if text:
                        await response.write(json.dumps({"content": text, "done": False}).encode() + b"\n")
                elif evt_type == "message_stop":
                    await response.write(json.dumps({"content": "", "done": True}).encode() + b"\n")
                    return
            except json.JSONDecodeError:
                pass
    await response.write(json.dumps({"content": "", "done": True}).encode() + b"\n")


# ---------- Qwen3-TTS helper (sync, runs in executor) ----------

QWEN_TTS_VOICES = [
    {"name": "Cherry", "gender": "Female", "locale": "zh-CN"},
    {"name": "Serena", "gender": "Female", "locale": "zh-CN"},
    {"name": "Ethan", "gender": "Male", "locale": "en-US"},
    {"name": "Chelsie", "gender": "Female", "locale": "en-US"},
]


def _qwen_tts_sync(text: str, voice: str = "Cherry") -> bytes:
    """Blocking call to Qwen3-TTS-Flash-Realtime, returns raw PCM bytes (24kHz 16-bit mono)."""
    import threading as _th

    pcm_chunks = []
    done_event = _th.Event()
    error_holder = [None]

    class _Cb(QwenTtsRealtimeCallback):
        def on_open(self):
            pass

        def on_close(self, code, msg):
            done_event.set()

        def on_event(self, response):
            try:
                evt_type = response.get("type", "")
                if evt_type == "response.audio.delta":
                    pcm_chunks.append(base64.b64decode(response["delta"]))
                elif evt_type == "session.finished":
                    done_event.set()
            except Exception as exc:
                error_holder[0] = exc
                done_event.set()

    dashscope.api_key = DASHSCOPE_API_KEY
    cb = _Cb()
    tts = QwenTtsRealtime(
        model="qwen3-tts-flash-realtime",
        callback=cb,
        url="wss://dashscope.aliyuncs.com/api-ws/v1/realtime",
    )
    tts.connect()
    tts.update_session(
        voice=voice,
        response_format=AudioFormat.PCM_24000HZ_MONO_16BIT,
        mode="server_commit",
    )
    tts.append_text(text)
    tts.finish()
    done_event.wait(timeout=30)
    if error_holder[0]:
        raise error_holder[0]
    return b"".join(pcm_chunks)


# ---------- TTS: Edge-TTS + Piper ----------

def _scan_vibevoice_voices():
    entries = []
    mapping = {}
    voice_dir = Path(VIBEVOICE_VOICE_DIR)
    if not voice_dir.exists():
        return entries, mapping
    for path in sorted(voice_dir.glob("*.pt")):
        stem = path.stem
        locale = stem.split("-")[0].upper()
        gender = "neutral"
        lower = stem.lower()
        if lower.endswith("_man"):
            gender = "male"
        elif lower.endswith("_woman"):
            gender = "female"
        label = stem if gender == "neutral" else f"{stem} ({gender})"
        entries.append({"name": stem, "label": label, "locale": locale, "gender": gender})
        mapping[lower] = str(path)
    return entries, mapping

def _ensure_vibevoice_voice_cache(app):
    entries = app.get("vibevoice_voice_entries")
    mapping = app.get("vibevoice_voice_map")
    if entries is None or mapping is None:
        entries, mapping = _scan_vibevoice_voices()
        app["vibevoice_voice_entries"] = entries
        app["vibevoice_voice_map"] = mapping
    return entries, mapping

def _resolve_vibevoice_voice(name, app):
    _, mapping = _ensure_vibevoice_voice_cache(app)
    normalized = (name or "").lower()
    path = mapping.get(normalized)
    if path:
        return path
    raise FileNotFoundError(f"Voice preset '{name}' not found in {VIBEVOICE_VOICE_DIR}")

def _auto_vibevoice_device():
    if VIBEVOICE_DEVICE and VIBEVOICE_DEVICE.lower() != "auto":
        return VIBEVOICE_DEVICE
    if torch is None:
        return "cpu"
    if torch.cuda.is_available():
        return "cuda"
    if getattr(torch.backends, "mps", None) and torch.backends.mps.is_available():
        return "mps"
    return "cpu"

def _synthesize_vibevoice_sync(state, voice_path, text, cfg_scale):
    if torch is None:
        raise RuntimeError("PyTorch is required for VibeVoice TTS")
    if sf is None:
        raise RuntimeError("soundfile is required for VibeVoice TTS output")
    processor = state["processor"]
    model = state["model"]
    device = state["device"]
    cached_prompt = torch.load(voice_path, map_location=device, weights_only=False)
    inputs = processor.process_input_with_cached_prompt(
        text=text,
        cached_prompt=cached_prompt,
        padding=True,
        return_tensors="pt",
        return_attention_mask=True,
    )
    for key, value in inputs.items():
        if torch.is_tensor(value):
            inputs[key] = value.to(device)
    outputs = model.generate(
        **inputs,
        cfg_scale=cfg_scale,
        tokenizer=processor.tokenizer,
        verbose=False,
        all_prefilled_outputs=copy.deepcopy(cached_prompt),
    )
    if not outputs.speech_outputs or outputs.speech_outputs[0] is None:
        raise RuntimeError("No speech output generated")
    speech = outputs.speech_outputs[0]
    if torch.is_tensor(speech):
        audio = speech.detach().cpu().numpy()
    else:
        audio = np.asarray(speech)
    if audio.ndim > 1:
        audio = audio[0]
    sample_rate = getattr(getattr(processor, "audio_processor", None), "sampling_rate", 24000)
    buf = io.BytesIO()
    sf.write(buf, audio, sample_rate, format="WAV")
    buf.seek(0)
    return buf.read()

async def _generate_vibevoice_audio(app, text, voice_path, cfg_scale):
    state = app.get("vibevoice")
    if not state:
        raise RuntimeError("VibeVoice engine is not initialized")
    loop = asyncio.get_event_loop()
    lock = app.get("vibevoice_lock")

    def _task():
        return _synthesize_vibevoice_sync(state, voice_path, text, cfg_scale)

    if lock:
        async with lock:
            return await loop.run_in_executor(None, _task)
    return await loop.run_in_executor(None, _task)

_edge_voice_cache = None


async def tts_voices(request):
    engine = request.query.get("engine", "edge")

    if engine == "piper":
        piper_voices = [{"name": "amy (en-US)", "gender": "Female", "locale": "en-US"}]
        return web.json_response(piper_voices)

    if engine == "vibevoice":
        entries, _ = _ensure_vibevoice_voice_cache(request.app)
        return web.json_response(entries)

    lang = request.query.get("lang", "en")  # "en", "zh", "all"
    global _edge_voice_cache, _edge_voice_cache_all
    if not hasattr(tts_voices, '_all_cache') or tts_voices._all_cache is None:
        all_voices = await edge_tts.list_voices()
        tts_voices._all_cache = [
            {"name": v["ShortName"], "gender": v["Gender"], "locale": v["Locale"]}
            for v in all_voices
        ]
        tts_voices._all_cache.sort(key=lambda v: (v["locale"], v["gender"], v["name"]))
        log.info("[tts] Loaded %d Edge voices total", len(tts_voices._all_cache))
    if lang == "all":
        return web.json_response(tts_voices._all_cache)
    prefix = "zh-" if lang == "zh" else "en-"
    filtered = [v for v in tts_voices._all_cache if v["locale"].startswith(prefix)]
    return web.json_response(filtered)


async def tts_speak(request):
    try:
        body = await request.json()
    except Exception:
        return web.Response(text="Invalid JSON", status=400)

    text = body.get("text", "").strip()
    engine = body.get("engine", "edge")

    if not text:
        return web.Response(text="No text provided", status=400)

    # --- 配额扣减：按待合成字符数 ---
    try:
        await quota_consume(
            request.app["session"],
            _client_authorization(request),
            "tts",
            len(text),
        )
    except QuotaError as qe:
        return _quota_rejected_response(qe)

    if engine == "vibevoice":
        vibevoice_state = request.app.get("vibevoice")
        if not vibevoice_state:
            return web.Response(text="VibeVoice TTS not available", status=503)
        voice_name = body.get("voice") or "en-Carter_man"
        try:
            voice_path = _resolve_vibevoice_voice(voice_name, request.app)
        except FileNotFoundError as exc:
            return web.Response(text=str(exc), status=404)
        cfg_scale = float(body.get("cfg_scale") or vibevoice_state.get("cfg_scale", VIBEVOICE_CFG_SCALE))
        log.info("[tts/vibevoice] voice=%s len=%d cfg=%.2f", voice_name, len(text), cfg_scale)
        try:
            audio_bytes = await _generate_vibevoice_audio(request.app, text, voice_path, cfg_scale)
        except Exception as e:
            log.error("[tts/vibevoice] Error: %s", e)
            return web.Response(text=f"VibeVoice error: {e}", status=500)
        return web.Response(
            body=audio_bytes,
            content_type="audio/wav",
            headers={"Cache-Control": "no-cache"},
        )

    # --- Qwen3-TTS (DashScope Realtime) ---
    if engine == "qwen-tts":
        if QwenTtsRealtime is None or not DASHSCOPE_API_KEY:
            return web.Response(text="Qwen TTS not available (dashscope not installed or DASHSCOPE_API_KEY not set)", status=503)
        voice = body.get("voice", "Cherry")
        log.info("[tts/qwen] voice=%s len=%d", voice, len(text))
        try:
            audio_bytes = await asyncio.get_event_loop().run_in_executor(
                None, lambda: _qwen_tts_sync(text, voice)
            )
        except Exception as e:
            log.error("[tts/qwen] Error: %s", e)
            return web.Response(text=f"Qwen TTS error: {e}", status=500)
        # Convert raw PCM 24kHz 16-bit mono to WAV
        buf = io.BytesIO()
        with wave.open(buf, "wb") as wf:
            wf.setnchannels(1)
            wf.setsampwidth(2)
            wf.setframerate(24000)
            wf.writeframes(audio_bytes)
        buf.seek(0)
        return web.Response(
            body=buf.read(),
            content_type="audio/wav",
            headers={"Cache-Control": "no-cache"},
        )

    # --- Piper TTS ---
    if engine == "piper":
        piper_tts = request.app.get("piper_tts")
        if not piper_tts:
            return web.Response(text="Piper TTS not available", status=503)

        speed = float(body.get("speed", 1.0))
        log.info("[tts/piper] len=%d speed=%.1f", len(text), speed)

        try:
            audio = await asyncio.get_event_loop().run_in_executor(
                None, lambda: piper_tts.generate(text, sid=0, speed=speed)
            )
            if not audio.samples or len(audio.samples) == 0:
                return web.Response(text="TTS generation empty", status=500)

            buf = io.BytesIO()
            with wave.open(buf, "wb") as wf:
                wf.setnchannels(1)
                wf.setsampwidth(2)
                wf.setframerate(audio.sample_rate)
                pcm = (np.array(audio.samples) * 32767).astype(np.int16)
                wf.writeframes(pcm.tobytes())
            buf.seek(0)
            return web.Response(
                body=buf.read(),
                content_type="audio/wav",
                headers={"Cache-Control": "no-cache"},
            )
        except Exception as e:
            log.error("[tts/piper] Error: %s", e)
            return web.Response(text=f"Piper TTS error: {e}", status=500)

    # --- Edge-TTS ---
    voice = body.get("voice", "en-US-AnaNeural")
    rate = body.get("rate", "-10%")
    log.info("[tts/edge] voice=%s len=%d", voice, len(text))

    try:
        communicate = edge_tts.Communicate(text, voice, rate=rate)
        response = web.StreamResponse(
            status=200,
            headers={"Content-Type": "audio/mpeg", "Cache-Control": "no-cache"},
        )
        await response.prepare(request)
        async for chunk in communicate.stream():
            if chunk["type"] == "audio":
                await response.write(chunk["data"])
        await response.write_eof()
    except (ConnectionResetError, asyncio.CancelledError):
        log.warning("[tts/edge] Client disconnected")
    except Exception as e:
        log.error("[tts/edge] Error: %s", e)
        return web.Response(text=f"TTS error: {e}", status=500)

    return response


async def init_vibevoice_tts(app):
    _ensure_vibevoice_voice_cache(app)
    if not ENABLE_VIBEVOICE_TTS:
        log.info("VibeVoice TTS disabled via ENABLE_VIBEVOICE_TTS")
        return
    if VibeVoiceStreamingForConditionalGenerationInference is None or VibeVoiceStreamingProcessor is None:
        log.info("VibeVoice packages not installed; skipping TTS init")
        return
    if torch is None:
        log.info("PyTorch not available; skipping VibeVoice TTS init")
        return
    if sf is None:
        log.warning("soundfile not installed; VibeVoice TTS disabled")
        return
    if not os.path.isdir(VIBEVOICE_MODEL_DIR):
        log.info("VibeVoice model dir not found: %s", VIBEVOICE_MODEL_DIR)
        return

    device = _auto_vibevoice_device()
    loop = asyncio.get_event_loop()
    log.info("Loading VibeVoice TTS (%s) on %s", VIBEVOICE_MODEL_DIR, device)

    def _load():
        processor = VibeVoiceStreamingProcessor.from_pretrained(VIBEVOICE_MODEL_DIR)
        dtype = torch.bfloat16 if device == "cuda" else torch.float32
        attn_impl = "flash_attention_2" if device == "cuda" else "sdpa"
        model = VibeVoiceStreamingForConditionalGenerationInference.from_pretrained(
            VIBEVOICE_MODEL_DIR,
            torch_dtype=dtype,
            device_map=(device if device in ("cuda", "cpu") else None),
            attn_implementation=attn_impl,
        )
        if device == "mps":
            model.to("mps")
        model.eval()
        model.set_ddpm_inference_steps(num_steps=5)
        return processor, model

    try:
        processor, model = await loop.run_in_executor(None, _load)
    except Exception as exc:
        log.error("Failed to initialize VibeVoice TTS: %s", exc)
        return

    app["vibevoice"] = {
        "processor": processor,
        "model": model,
        "device": device,
        "cfg_scale": VIBEVOICE_CFG_SCALE,
    }
    app["vibevoice_lock"] = asyncio.Lock()
    log.info(
        "VibeVoice TTS ready (%s, voices=%d)",
        device,
        len(app.get("vibevoice_voice_entries", [])),
    )


# ---------- Piper TTS init ----------

async def init_piper_tts(app):
    if sherpa_onnx is None:
        log.warning("sherpa-onnx not installed, Piper TTS disabled")
        return

    piper_dir = os.path.join(MODELS_DIR, "vits-piper-en_US-amy-low")
    model_path = os.path.join(piper_dir, "en_US-amy-low.onnx")
    tokens_path = os.path.join(piper_dir, "tokens.txt")
    data_dir = os.path.join(piper_dir, "espeak-ng-data")

    if not os.path.exists(model_path):
        log.warning("Piper model not found at %s, TTS disabled", model_path)
        return

    try:
        tts_config = sherpa_onnx.OfflineTtsConfig(
            model=sherpa_onnx.OfflineTtsModelConfig(
                vits=sherpa_onnx.OfflineTtsVitsModelConfig(
                    model=model_path,
                    tokens=tokens_path,
                    data_dir=data_dir,
                ),
                provider="cpu",
                num_threads=2,
            ),
            max_num_sentences=2,
        )
        app["piper_tts"] = sherpa_onnx.OfflineTts(tts_config)
        log.info("Piper TTS initialized: %s", model_path)
    except Exception as e:
        log.error("Failed to init Piper TTS: %s", e)


# ---------- App setup ----------

# ---------- Dictation: save words back to JSON ----------

MY_WORDS_FILE = os.path.join(STATIC_DIR, "my-words.json")


async def dictation_save_words(request):
    """Save word list back to my-words.json."""
    try:
        body = await request.json()
    except Exception:
        return web.Response(text="Invalid JSON", status=400)
    words = body.get("words")
    if not isinstance(words, list):
        return web.Response(text="Missing 'words' array", status=400)
    try:
        with open(MY_WORDS_FILE, "w", encoding="utf-8") as f:
            json.dump(words, f, ensure_ascii=False, indent=2)
        log.info("[dictation] saved %d words to %s", len(words), MY_WORDS_FILE)
        return web.json_response({"ok": True, "count": len(words)})
    except Exception as e:
        log.error("[dictation] save error: %s", e)
        return web.json_response({"ok": False, "error": str(e)}, status=500)


def create_app():
    app = web.Application()
    app.on_startup.append(create_shared_session)
    app.on_startup.append(init_piper_tts)
    app.on_startup.append(init_vibevoice_tts)
    app.on_cleanup.append(close_shared_session)

    app.router.add_route("GET", "/ws", ws_proxy)
    app.router.add_route("GET", "/asr/models", asr_models_handler)
    app.router.add_route("POST", "/asr", asr_offline)
    app.router.add_route("GET", "/llm/models", llm_models_handler)
    app.router.add_route("POST", "/llm/chat", llm_chat)
    app.router.add_route("POST", "/llm/judge", llm_judge)
    app.router.add_route("*", "/api/{path:.*}", ollama_proxy)
    app.router.add_route("GET", "/tts/voices", tts_voices)
    app.router.add_route("POST", "/tts", tts_speak)
    app.router.add_route("POST", "/dictation/words", dictation_save_words)
    app.router.add_static("/", STATIC_DIR, show_index=True)
    return app


def main():
    generate_cert()

    ssl_ctx = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    ssl_ctx.load_cert_chain(CERT_FILE, KEY_FILE)

    app = create_app()

    print(f"\n{'='*55}")
    print(f"  HTTPS server: https://0.0.0.0:{PORT}")
    print(f"  Open https://<your-lan-ip>:{PORT} on any device")
    print(f"{'='*55}")
    print(f"  /ws?model=xxx  → streaming ASR (WebSocket)")
    print(f"  /asr?model=xxx → offline ASR (POST audio)")
    print(f"  /asr/models    → ASR model list")
    print(f"  /llm/chat      → LLM proxy (Ollama/OpenAI/Claude/OpenRouter)")
    print(f"  /llm/models    → LLM model list")
    print(f"  /api/*         → {OLLAMA_BASE}/api/* (Ollama)")
    print(f"  /tts           → Edge/Piper/VibeVoice TTS")
    print(f"  /tts/voices    → TTS voice list")
    print(f"  /*             → {STATIC_DIR} (static files)")
    print(f"{'='*55}")
    print(f"  ASR models:")
    for m in ASR_MODELS:
        print(f"    [{m['port']}] {m['label']}")
    print(f"  LLM providers:")
    providers = set()
    for m in LLM_MODELS:
        providers.add(m["provider"])
        print(f"    [{m['provider']}] {m['label']}")
    if not OPENAI_API_KEY:
        print(f"    [openai] Not configured (set OPENAI_API_KEY in .env)")
    if not ANTHROPIC_API_KEY:
        print(f"    [claude] Not configured (set ANTHROPIC_API_KEY in .env)")
    if not MOONSHOT_API_KEY:
        print(f"    [moonshot] Not configured (set MOONSHOT_API_KEY in .env)")
    if not OPENROUTER_API_KEY:
        print(f"    [openrouter] Not configured (set OPENROUTER_API_KEY in .env)")
    if not VOLC_ASR_APPID or not VOLC_ASR_TOKEN:
        print(f"    [volcano-asr] Not configured (set VOLC_ASR_APPID + VOLC_ASR_TOKEN + VOLC_ASR_CLUSTER in .env)")
    if not DASHSCOPE_API_KEY:
        print(f"    [dashscope-asr] Not configured (set DASHSCOPE_API_KEY in .env)")
    elif dashscope is None:
        print(f"    [dashscope-asr] SDK not installed (pip install dashscope)")
    if HTTP_PROXY:
        print(f"  HTTP Proxy: {HTTP_PROXY}")
    else:
        print(f"  HTTP Proxy: None (set HTTP_PROXY in .env for OpenAI/Claude)")
    print(f"{'='*55}\n")

    web.run_app(app, host=HOST, port=PORT, ssl_context=ssl_ctx)


if __name__ == "__main__":
    main()











