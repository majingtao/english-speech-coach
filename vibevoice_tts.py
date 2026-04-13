"""Generate speech from text using VibeVoice-Realtime-0.5B."""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

import torch
from vibevoice.modular.modeling_vibevoice_streaming_inference import (
    VibeVoiceStreamingForConditionalGenerationInference,
)
from vibevoice.processor.vibevoice_streaming_processor import VibeVoiceStreamingProcessor

VOICE_DIR = Path("assets") / "vibevoice" / "voices"
DEFAULT_MODEL_DIR = Path("audio") / "VibeVoice-Realtime-0.5B"
DEFAULT_OUTPUT = Path("audio") / "tts_output.wav"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Run VibeVoice-Realtime TTS")
    parser.add_argument(
        "--text",
        type=str,
        help="Text to synthesize. Overrides --text-file when provided.",
    )
    parser.add_argument(
        "--text-file",
        type=Path,
        help="Path to a UTF-8 text file.",
    )
    parser.add_argument(
        "--speaker",
        default="en-Carter_man",
        help="Speaker preset name (matches .pt files in assets/vibevoice/voices)",
    )
    parser.add_argument(
        "--model-path",
        default=str(DEFAULT_MODEL_DIR),
        help="Local directory or Hugging Face repo id for the TTS model.",
    )
    parser.add_argument(
        "--device",
        choices=["auto", "cuda", "mps", "cpu"],
        default="auto",
        help="Device to run inference.",
    )
    parser.add_argument(
        "--cfg-scale",
        type=float,
        default=1.5,
        help="Classifier-free guidance scale.",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=DEFAULT_OUTPUT,
        help="Where to save the synthesized wav file.",
    )
    return parser.parse_args()


def resolve_text(args: argparse.Namespace) -> str:
    if args.text:
        return args.text.strip()
    if args.text_file:
        return args.text_file.read_text(encoding="utf-8").strip()
    raise SystemExit("Use --text or --text-file to provide input.")


def resolve_device(target: str) -> str:
    if target == "auto":
        if torch.cuda.is_available():
            return "cuda"
        if getattr(torch.backends, "mps", None) and torch.backends.mps.is_available():
            return "mps"
        return "cpu"
    return target


def find_voice_file(name: str) -> Path:
    if not VOICE_DIR.exists():
        raise FileNotFoundError(f"Voice directory not found: {VOICE_DIR}")

    normalized = name.lower().replace(".pt", "")
    matches = [f for f in VOICE_DIR.glob("*.pt") if f.stem.lower() == normalized]
    if matches:
        return matches[0]

    partial = [f for f in VOICE_DIR.glob("*.pt") if normalized in f.stem.lower()]
    if len(partial) == 1:
        return partial[0]
    if not partial:
        raise FileNotFoundError(
            f"Voice preset '{name}' not found. Files: {[f.stem for f in VOICE_DIR.glob('*.pt')]}"
        )
    raise ValueError(
        f"Voice preset '{name}' is ambiguous. Candidates: {[f.stem for f in partial]}"
    )


def load_model(model_path: str, device: str) -> VibeVoiceStreamingForConditionalGenerationInference:
    load_dtype = torch.bfloat16 if device == "cuda" else torch.float32
    attn_primary = "flash_attention_2" if device == "cuda" else "sdpa"

    def _do_load(attn_impl: str):
        model = VibeVoiceStreamingForConditionalGenerationInference.from_pretrained(
            model_path,
            torch_dtype=load_dtype,
            device_map=(device if device in ("cuda", "cpu") else None),
            attn_implementation=attn_impl,
        )
        if device == "mps":
            model.to("mps")
        return model

    try:
        model = _do_load(attn_primary)
    except Exception as exc:
        if attn_primary == "flash_attention_2":
            print("[WARN] flash_attention_2 unavailable, falling back to SDPA", file=sys.stderr)
            model = _do_load("sdpa")
        else:
            raise exc

    model.eval()
    model.set_ddpm_inference_steps(num_steps=5)
    return model


def main() -> None:
    args = parse_args()
    text = resolve_text(args)
    device = resolve_device(args.device)

    model_root = Path(args.model_path)
    model_path = str(model_root) if model_root.exists() else args.model_path

    print(f"[INFO] Using model: {model_path}")
    print(f"[INFO] Target device: {device}")

    processor = VibeVoiceStreamingProcessor.from_pretrained(model_path)
    model = load_model(model_path, device)

    voice_file = find_voice_file(args.speaker)
    print(f"[INFO] Using voice preset: {voice_file}")
    cached_prompt = torch.load(voice_file, map_location=device, weights_only=False)

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
        cfg_scale=args.cfg_scale,
        tokenizer=processor.tokenizer,
        verbose=True,
        all_prefilled_outputs=cached_prompt,
    )

    if not outputs.speech_outputs or outputs.speech_outputs[0] is None:
        raise RuntimeError("No speech output was generated.")

    args.output.parent.mkdir(parents=True, exist_ok=True)
    processor.save_audio(outputs.speech_outputs[0], output_path=str(args.output))
    print(f"[DONE] Saved audio to {args.output}")


if __name__ == "__main__":
    main()
