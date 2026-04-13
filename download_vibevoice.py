"""Utility to download VibeVoice-Realtime-0.5B weights into the local audio folder."""
from __future__ import annotations

import argparse
import os
from pathlib import Path

from huggingface_hub import snapshot_download


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Fetch VibeVoice-Realtime weights")
    parser.add_argument(
        "--repo-id",
        default="microsoft/VibeVoice-Realtime-0.5B",
        help="Hugging Face repository ID",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("audio") / "VibeVoice-Realtime-0.5B",
        help="Local directory to store the snapshot",
    )
    parser.add_argument(
        "--endpoint",
        default=os.environ.get("HF_ENDPOINT", "https://hf-mirror.com"),
        help="Hugging Face endpoint, defaults to hf-mirror.com for faster CN access",
    )
    parser.add_argument(
        "--token",
        default=os.environ.get("HF_TOKEN"),
        help="Optional Hugging Face access token",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    target = args.output
    target.parent.mkdir(parents=True, exist_ok=True)

    snapshot_download(
        repo_id=args.repo_id,
        repo_type="model",
        local_dir=target,
        local_dir_use_symlinks=False,
        token=args.token,
        endpoint=args.endpoint,
        resume_download=True,
    )

    print(f"Model downloaded to {target}")


if __name__ == "__main__":
    main()
