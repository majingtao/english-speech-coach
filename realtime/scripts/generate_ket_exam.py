"""
Generate KET 2020 speaking practice tests via LLM and insert into esc_exam.

Usage:
    python generate_ket_exam.py --count 1
    python generate_ket_exam.py --count 3 --start-index 2 --series ket_practice
    python generate_ket_exam.py --dry-run   # just prints generated JSON, no DB write

Environment variables:
    OPENAI_API_KEY / OPENAI_BASE_URL   (optional)
    MOONSHOT_API_KEY / MOONSHOT_BASE_URL (optional)
    ANTHROPIC_API_KEY                   (optional)
    DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME  (defaults: 127.0.0.1/3306/root/123456/kg_english_coach)

Requires:
    pip install pymysql requests python-dotenv
"""
from __future__ import annotations

import argparse
import json
import os
import random
import re
import sys
from pathlib import Path

try:
    from dotenv import load_dotenv
    load_dotenv(Path(__file__).parent.parent / "web" / ".env")
except ImportError:
    pass

import pymysql
import requests


SCRIPT_DIR = Path(__file__).parent
PROMPT_FILE = SCRIPT_DIR / "prompts" / "ket_exam_generator.md"

PARTNER_NAMES = [
    "Emma", "Oliver", "Sophie", "Liam", "Mia", "Noah",
    "Ava", "Ethan", "Lily", "Jack", "Chloe", "Leo",
]


def load_prompt(index: int, partner_seed: str) -> str:
    tmpl = PROMPT_FILE.read_text(encoding="utf-8")
    return tmpl.replace("{{INDEX}}", str(index)).replace("{{PARTNER_SEED}}", partner_seed)


def call_llm(provider: str, model: str, prompt: str) -> str:
    """Call the selected LLM provider and return raw text."""
    if provider == "openai":
        return _call_openai_compat(
            base=os.environ.get("OPENAI_BASE_URL", "https://api.openai.com/v1"),
            key=os.environ["OPENAI_API_KEY"],
            model=model,
            prompt=prompt,
        )
    if provider == "moonshot":
        return _call_openai_compat(
            base=os.environ.get("MOONSHOT_BASE_URL", "https://api.moonshot.cn/v1"),
            key=os.environ["MOONSHOT_API_KEY"],
            model=model,
            prompt=prompt,
        )
    if provider == "claude":
        return _call_claude(os.environ["ANTHROPIC_API_KEY"], model, prompt)
    raise ValueError(f"Unsupported provider: {provider}")


def _call_openai_compat(base: str, key: str, model: str, prompt: str) -> str:
    url = base.rstrip("/") + "/chat/completions"
    headers = {"Authorization": f"Bearer {key}", "Content-Type": "application/json"}
    body = {
        "model": model,
        "messages": [
            {"role": "system", "content": "You are a precise JSON generator. Output only raw JSON."},
            {"role": "user", "content": prompt},
        ],
        "temperature": 0.8,
        "response_format": {"type": "json_object"},
    }
    r = requests.post(url, json=body, headers=headers, timeout=120)
    r.raise_for_status()
    data = r.json()
    return data["choices"][0]["message"]["content"]


def _call_claude(key: str, model: str, prompt: str) -> str:
    url = "https://api.anthropic.com/v1/messages"
    headers = {
        "x-api-key": key,
        "anthropic-version": "2023-06-01",
        "Content-Type": "application/json",
    }
    body = {
        "model": model,
        "max_tokens": 4096,
        "system": "You are a precise JSON generator. Output only raw JSON.",
        "messages": [{"role": "user", "content": prompt}],
    }
    r = requests.post(url, json=body, headers=headers, timeout=120)
    r.raise_for_status()
    return r.json()["content"][0]["text"]


def extract_json(raw: str) -> dict:
    raw = raw.strip()
    if raw.startswith("```"):
        raw = re.sub(r"^```[a-zA-Z]*\n", "", raw)
        raw = re.sub(r"\n```$", "", raw.rstrip())
    start = raw.find("{")
    end = raw.rfind("}")
    if start < 0 or end < 0:
        raise ValueError("No JSON object found in LLM response")
    return json.loads(raw[start : end + 1])


def validate_ket_json(data: dict) -> None:
    required_root = [
        "schemaVersion", "label", "format", "defaultUserSeat",
        "virtualCandidate", "part1", "part2",
    ]
    for k in required_root:
        if k not in data:
            raise ValueError(f"missing key: {k}")
    if data["schemaVersion"] != 2:
        raise ValueError("schemaVersion must be 2")
    if data["format"] != "ket":
        raise ValueError(f"format must be 'ket', got {data['format']}")
    if data["defaultUserSeat"] not in ("A", "B"):
        raise ValueError("defaultUserSeat must be A or B")
    for k in ("name", "avatarSeed", "voice"):
        if k not in data["virtualCandidate"]:
            raise ValueError(f"virtualCandidate.{k} missing")

    ids = set()

    def validate_examiner_turn(turn: dict, expected_kind: str | None = None) -> None:
        for key in ("id", "speaker", "target", "text", "response", "virtualAnswer"):
            if not turn.get(key):
                raise ValueError(f"examiner turn missing {key}")
        if turn["id"] in ids:
            raise ValueError(f"duplicate turn id: {turn['id']}")
        ids.add(turn["id"])
        if turn["speaker"] != "examiner" or turn["target"] not in ("A", "B"):
            raise ValueError(f"invalid examiner turn role/target: {turn['id']}")
        response = turn["response"]
        if not isinstance(response, dict) or not response.get("kind"):
            raise ValueError(f"turn response missing kind: {turn['id']}")
        if expected_kind and response["kind"] != expected_kind:
            raise ValueError(f"turn {turn['id']} kind must be {expected_kind}")

    def validate_alternating(items: list, field: str, label: str) -> None:
        values = [item.get(field) for item in items]
        if any(value not in ("A", "B") for value in values):
            raise ValueError(f"{label} contains an invalid {field}")
        if any(values[i] == values[i - 1] for i in range(1, len(values))):
            raise ValueError(f"{label} must alternate A/B")

    p1 = data["part1"]
    if not isinstance(p1.get("turns"), list) or len(p1["turns"]) != 4:
        raise ValueError("part1.turns must contain exactly 4 basic turns")
    validate_alternating(p1["turns"], "target", "part1.turns")
    for turn in p1["turns"]:
        validate_examiner_turn(turn, "basic")
    if not isinstance(p1.get("topics"), list) or len(p1["topics"]) != 1:
        raise ValueError("part1.topics must contain exactly 1 topic")
    topic = p1["topics"][0]
    if not topic.get("id") or not topic.get("title") or not topic.get("intro"):
        raise ValueError("part1 topic missing id/title/intro")
    if not isinstance(topic.get("turns"), list) or len(topic["turns"]) != 6:
        raise ValueError("part1 topic must contain exactly 6 turns")
    validate_alternating(topic["turns"], "target", "part1 topic turns")
    for turn in topic["turns"]:
        validate_examiner_turn(turn)
        if turn["response"]["kind"] not in ("extended", "tell_me_about"):
            raise ValueError(f"invalid Part 1 topic kind: {turn['response']['kind']}")
    tell_targets = {
        turn["target"] for turn in topic["turns"]
        if turn["response"]["kind"] == "tell_me_about"
    }
    if tell_targets != {"A", "B"}:
        raise ValueError("Part 1 topic needs one tell_me_about turn for A and B")

    p2 = data["part2"]
    if not p2.get("examinerSetup"):
        raise ValueError("part2.examinerSetup missing")
    material = p2.get("material")
    if not isinstance(material, dict) or not isinstance(material.get("options"), list):
        raise ValueError("part2.material.options missing")
    if len(material["options"]) != 5:
        raise ValueError("part2.material.options must have exactly 5 items")
    if any(not option.get("id") or not option.get("label") for option in material["options"]):
        raise ValueError("every Part 2 option needs id and label")

    conversation = p2.get("conversation")
    if not isinstance(conversation, dict) or conversation.get("starter") not in ("A", "B"):
        raise ValueError("part2.conversation.starter must be A or B")
    conversation_turns = conversation.get("turns")
    if not isinstance(conversation_turns, list) or len(conversation_turns) != 6:
        raise ValueError("part2.conversation.turns must contain exactly 6 turns")
    validate_alternating(conversation_turns, "speaker", "part2 conversation")
    if conversation_turns[0]["speaker"] != conversation["starter"]:
        raise ValueError("first conversation speaker must match starter")
    for turn in conversation_turns:
        for key in ("goal", "virtualFallback", "responseKind"):
            if not turn.get(key):
                raise ValueError(f"Part 2 conversation turn missing {key}")
        if turn["responseKind"] not in ("partner_opening", "discussion"):
            raise ValueError(f"invalid responseKind: {turn['responseKind']}")

    followups = p2.get("followups")
    if not isinstance(followups, list) or len(followups) != 4:
        raise ValueError("part2.followups must contain exactly 4 turns")
    validate_alternating(followups, "target", "part2 followups")
    for turn in followups:
        validate_examiner_turn(turn, "followup")


def db_connect():
    return pymysql.connect(
        host=os.environ.get("DB_HOST", "127.0.0.1"),
        port=int(os.environ.get("DB_PORT", 3306)),
        user=os.environ.get("DB_USER", "root"),
        password=os.environ.get("DB_PASSWORD", "123456"),
        database=os.environ.get("DB_NAME", "kg_english_coach"),
        charset="utf8mb4",
    )


def insert_exam(conn, *, exam_code: str, series_code: str, label: str, content_json: str) -> int:
    with conn.cursor() as cur:
        cur.execute("SELECT id FROM esc_exam WHERE exam_code = %s AND is_active = 1", (exam_code,))
        existing = cur.fetchone()
        if existing:
            cur.execute(
                "UPDATE esc_exam SET content_json = %s, label = %s, update_time = NOW() WHERE id = %s",
                (content_json, label, existing[0]),
            )
            conn.commit()
            return existing[0]

        cur.execute(
            """
            INSERT INTO esc_exam
                (exam_code, version, is_active, level_code, series_code, label, source, status,
                 content_json, tenant_id, creator, updater, create_time, update_time, deleted)
            VALUES (%s, 1, 1, 'ket', %s, %s, 'AI Generated', 1, %s, 0, 'script', 'script', NOW(), NOW(), 0)
            """,
            (exam_code, series_code, label, content_json),
        )
        conn.commit()
        return cur.lastrowid


def pick_default_model():
    if os.environ.get("MOONSHOT_API_KEY"):
        return "moonshot", "kimi-k2-0905-preview"
    if os.environ.get("OPENAI_API_KEY"):
        return "openai", "gpt-4o-mini"
    if os.environ.get("ANTHROPIC_API_KEY"):
        return "claude", "claude-sonnet-4-20250514"
    raise RuntimeError("No LLM API key found. Set OPENAI_API_KEY, MOONSHOT_API_KEY, or ANTHROPIC_API_KEY.")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--count", type=int, default=1)
    parser.add_argument("--start-index", type=int, default=1)
    parser.add_argument("--series", default="ket_practice")
    parser.add_argument("--provider", default=None)
    parser.add_argument("--model", default=None)
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    if args.provider and args.model:
        provider, model = args.provider, args.model
    else:
        provider, model = pick_default_model()
    print(f"[ket-gen] provider={provider} model={model}")

    conn = None if args.dry_run else db_connect()

    for offset in range(args.count):
        idx = args.start_index + offset
        exam_code = f"ket_{idx}"
        partner_seed = random.choice(PARTNER_NAMES).lower() + f"-{idx}"
        prompt = load_prompt(idx, partner_seed)

        print(f"\n[ket-gen] generating {exam_code} ...")
        try:
            raw = call_llm(provider, model, prompt)
            data = extract_json(raw)
            validate_ket_json(data)
        except Exception as e:
            print(f"[ket-gen]  ERROR: {e}")
            continue

        label = data.get("label") or f"KET Practice Test {idx}"
        content_json = json.dumps(data, ensure_ascii=False)

        if args.dry_run:
            print(f"[ket-gen] would insert exam_code={exam_code}")
            print(json.dumps(data, ensure_ascii=False, indent=2))
            continue

        row_id = insert_exam(conn, exam_code=exam_code, series_code=args.series, label=label, content_json=content_json)
        print(f"[ket-gen]  inserted/updated id={row_id} exam_code={exam_code}")

    if conn:
        conn.close()


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        sys.exit(130)
