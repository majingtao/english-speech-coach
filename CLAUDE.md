# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

English AI Speech Coach — helps children (Cambridge Flyers A2, age 7-12) practice spoken English. Two modes:

1. **Free Talk** — open conversation with real-time ASR + LLM feedback
2. **Exam Practice** — structured speaking exam simulation following official Cambridge Flyers format (Parts 1-4)

## Architecture

### Web App (`realtime/web/`)

Single-page app served by `server.py` (Python aiohttp HTTPS server).

- `index.html` — Frontend: Free Talk + Exam modes, TTS (Edge-TTS/Piper/System), persistent mic, LLM/ASR selector
- `server.py` — Backend: HTTPS proxy, `/llm/chat` (unified LLM proxy for Ollama/OpenAI/Claude), `/llm/judge` (exam grading), `/asr` (ASR proxy), `/tts/*` (Edge-TTS/Piper)
- `question-bank.json` — **17 套** Cambridge Flyers speaking exams (5 Go Flyers + 3 Fly4 2022 + 9 official past papers 2018-2019)
- `.env` — API keys for OpenAI/Claude/Moonshot (not committed)
- `prompts/` — System prompts for Free Talk mode

**Question Bank Test IDs:**
| Prefix | Source | Count |
|--------|--------|-------|
| `gf_1`~`gf_5` | Go Flyers Teacher's Notes | 5 |
| `f4_1`~`f4_3` | Flyers 4 (2022) | 3 |
| `aep1_1`~`aep1_3` | Auth Exam Papers 1 (2018) | 3 |
| `aep2_1`~`aep2_3` | Auth Exam Papers 2 (2018) | 3 |
| `aep3_1`~`aep3_3` | Auth Exam Papers 3 (2019) | 3 |

Each test has `label`, `part1` (Find the Differences), `part2` (Information Exchange with phaseA/phaseB), `part3` (Tell the Story with `{text, hint}` sentences), `part4` (Personal Questions with sample answers). Test selector dropdown is dynamically populated from JSON.

**Exam State Machine:** `ES` object with `steps[]`, `stepIdx`, step types: `speak`, `judge`, `judge-question`, `show-hint`, `end`. `buildSteps()` generates flat step list from question bank JSON.

### Legacy Pipeline

- `speech_coach.py` — CLI tool: sherpa-onnx Whisper in Docker → Ollama feedback
- `docker-compose.yaml` — Ollama (port 11434) + Open WebUI (port 3000)

### VibeVoice TTS

- `python -m pip install -e vibevoice_repo[streamingtts]` — editable install of upstream code (repo tracked under `vibevoice_repo/`)
- `download_vibevoice.py` — grabs `microsoft/VibeVoice-Realtime-0.5B` weights into `audio/VibeVoice-Realtime-0.5B` (uses hf-mirror endpoint + resume)
- `assets/vibevoice/voices/` — streamed speaker prompts mounted into `/workspace/vibevoice-voices` for containers
- `vibevoice_tts.py` — offline CLI: `python vibevoice_tts.py --text "Hello" --speaker en-Carter_man --device cuda --output audio/demo.wav`
- Output wavs land in `audio/` (default `tts_output.wav`); voice + cfg parameters are CLI flags

## Common Commands

```bash
# Start the web app (HTTPS on port 8443)
cd realtime/web && python server.py

# Start Ollama + Open WebUI stack
docker compose up -d

# Run legacy speech coach
python speech_coach.py
```

## Key Dependencies

- Python 3.x with `aiohttp` (server.py)
- Ollama (local LLM) and/or OpenAI/Claude API keys in `.env`
- Docker with NVIDIA GPU support (for Ollama GPU acceleration)
- sherpa-onnx ASR models in `realtime/models/`

## Style

- Commits: short imperative subjects (e.g., "Add whisper defaults")
- Never hardcode credentials; use `.env` overrides
- question-bank.json: Test IDs use `source_number` format (e.g., `gf_1`, `f4_2`, `aep1_3`)
