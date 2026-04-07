import json
import subprocess
import sys
from pathlib import Path

import requests


# ====== 这里改成你的本地路径 ======
PROJECT_DIR = Path(r"E:\project\englishAi")
AUDIO_FILE = PROJECT_DIR / "audio" / "test.wav"

WHISPER_DIR = PROJECT_DIR / "whisper-tiny-en" / "sherpa-onnx-whisper-tiny.en"
WHISPER_ENCODER = "/workspace/whisper-tiny-en/sherpa-onnx-whisper-tiny.en/tiny.en-encoder.onnx"
WHISPER_DECODER = "/workspace/whisper-tiny-en/sherpa-onnx-whisper-tiny.en/tiny.en-decoder.onnx"
WHISPER_TOKENS = "/workspace/whisper-tiny-en/sherpa-onnx-whisper-tiny.en/tiny.en-tokens.txt"

OLLAMA_URL = "http://localhost:11434/api/generate"
OLLAMA_MODEL = "qwen2.5:3b"   # 没装 3b 就改成你已有的模型名
# ===============================


def run_whisper(audio_file: Path) -> str:
    if not audio_file.exists():
        raise FileNotFoundError(f"音频不存在: {audio_file}")

    docker_cmd = [
        "docker", "run", "--rm",
        "-v", f"{PROJECT_DIR}:/workspace",
        "python:3.10",
        "bash", "-lc",
        (
            "pip install sherpa-onnx sherpa-onnx-bin "
            "-i https://pypi.tuna.tsinghua.edu.cn/simple >/tmp/pip.log 2>&1 && "
            "sherpa-onnx-offline "
            f"--whisper-encoder={WHISPER_ENCODER} "
            f"--whisper-decoder={WHISPER_DECODER} "
            f"--tokens={WHISPER_TOKENS} "
            f"/workspace/audio/{audio_file.name}"
        )
    ]

    result = subprocess.run(
        docker_cmd,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )

    if result.returncode != 0:
        raise RuntimeError(
            "Whisper 识别失败。\n"
            f"STDOUT:\n{result.stdout}\n\nSTDERR:\n{result.stderr}"
        )

    transcript = extract_transcript(result.stdout)
    if not transcript:
        raise RuntimeError(
            "没有从 Whisper 输出中提取到 transcript。\n"
            f"完整输出:\n{result.stdout}"
        )
    return transcript.strip()


def extract_transcript(output: str) -> str:
    """
    从 sherpa-onnx-offline 输出里提取 JSON 中的 text 字段
    """
    for line in output.splitlines():
        line = line.strip()
        if line.startswith("{") and '"text"' in line:
            try:
                data = json.loads(line)
                return data.get("text", "")
            except json.JSONDecodeError:
                continue
    return ""


def call_ollama(transcript: str) -> str:
    prompt = f"""
You are an English speaking coach for a child learner preparing for Cambridge Flyers or KET.

Please analyze the following spoken English transcript carefully.
The transcript may contain recognition errors, grammar mistakes, or unnatural spoken English.

Transcript:
{transcript}

Please respond in exactly this format:

1. Transcript clean version:
2. Corrected sentence:
3. More natural English version:
4. Key mistakes:
5. Chinese explanation:
6. Difficulty level (A1/A2/B1):
7. One short practice sentence:
""".strip()

    payload = {
        "model": OLLAMA_MODEL,
        "prompt": prompt,
        "stream": False
    }

    resp = requests.post(OLLAMA_URL, json=payload, timeout=300)
    resp.raise_for_status()
    data = resp.json()
    return data.get("response", "").strip()


def main():
    print("1) 正在识别音频...")
    transcript = run_whisper(AUDIO_FILE)
    print("\n=== Whisper 识别结果 ===")
    print(transcript)

    print("\n2) 正在调用 Qwen 批改...")
    feedback = call_ollama(transcript)

    print("\n=== AI 批改结果 ===")
    print(feedback)


if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"\n[ERROR] {e}")
        sys.exit(1)