#!/usr/bin/env python3
"""Build dictations.json with curated write-required flags."""

from __future__ import annotations

import json
import math
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, List, Set

BASE_DIR = Path(__file__).parent
WORDLIST_PATH = BASE_DIR / "flyers-wordlist.json"
OUTPUT_PATH = BASE_DIR / "dictations.json"
TARGET_LIMIT = 320

ALWAYS_INCLUDE = {
    "restaurant", "hospital", "ambulance", "butterfly", "strawberry",
    "arrive", "believe", "decide", "delicious", "expensive",
    "beautiful", "competition", "environment", "photographer",
    "because", "favorite", "holiday", "kitchen", "bedroom",
    "bicycle", "homework", "sandwich", "library", "computer",
    "airport", "ticket", "platform", "passenger", "letter", "story",
    "picture", "camera", "guitar", "visit", "teacher", "friend",
    "family", "sister", "brother", "grandma", "grandpa", "parent",
    "school", "student", "winter", "summer", "breakfast", "lunch",
    "dinner", "party", "practice",
}

ALWAYS_INCLUDE_PHRASES = {
    "fire station", "police officer", "take photos", "play the guitar",
    "do homework", "have breakfast", "have lunch", "have dinner",
    "go camping", "go shopping", "ride a bike", "take a bus",
}

ALWAYS_EXCLUDE = {
    "go away!", "if you want!", "no problem!", "you're welcome!", "in a minute!",
    "actually", "perhaps", "instead", "several", "without", "astronaut",
    "skyscraper", "pyramid", "octopus", "spaceship", "saxophone",
    "binoculars", "microscope", "electricity", "creature", "extinct",
    "chemist's", "businessman/woman", "railway", "timetable",
    "maybe", "somebody", "anybody", "someone", "anyone", "everyone",
    "something", "anything", "everything", "everywhere", "somewhere",
    "nowhere", "such", "while", "since", "until", "might", "could",
    "would", "should", "shall", "ought", "herself", "himself",
    "myself", "yourself", "themselves", "ourselves",
}

LOW_PRIORITY = {
    "adventure", "castle", "cave", "desert", "dinosaur", "extinct",
    "spaceship", "planet", "rocket", "meteor", "galaxy", "scientist",
    "laboratory", "astronaut", "pyramid", "skyscraper", "saxophone",
    "microscope", "octopus", "pirate", "treasure", "volcano",
}

TRICKY_WORDS = {"beautiful", "competition", "environment", "photographer", "delicious", "expensive"}
MONTHS = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"}
NUMBERS = {
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
    "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
    "eighteen", "nineteen", "twenty", "thirty", "forty", "fifty", "sixty", "seventy",
    "eighty", "ninety", "hundred", "thousand", "first", "second", "third", "fourth",
    "fifth", "sixth", "seventh", "eighth", "ninth", "tenth", "twentieth", "thirtieth",
}

TARGET_WORDS = {
    "people": {"mother", "father", "sister", "brother", "grandma", "grandpa", "teacher", "friend"},
    "places": {"school", "garden", "kitchen", "bedroom", "playground", "park", "restaurant", "hospital", "airport"},
    "actions": {"play", "read", "write", "draw", "swim", "ride", "cook", "clean", "listen", "watch", "travel"},
    "feelings": {"happy", "funny", "busy", "tired", "hungry", "excited", "worried"},
    "weather": {"sunny", "windy", "rainy", "cloudy", "snowy", "storm"},
    "food": {"breakfast", "lunch", "dinner", "sandwich", "salad", "banana", "orange", "juice"},
}
TARGET_FLAT = {w.lower() for values in TARGET_WORDS.values() for w in values}

NAMES = [
    "Betty", "David", "Emma", "Frank", "George", "Harry", "Helen",
    "Holly", "Katy", "Michael", "Oliver", "Richard", "Robert",
    "Sarah", "Sophia", "William",
]

CORE_POS = {"n", "v", "adj"}
SUPPORT_POS = {"adv"}
SKIP_POS = {"pron", "det", "conj", "prep", "int", "excl"}


class Entry(dict):
    """Custom dict to carry helper flags."""

    eligible: bool
    manual_lock: bool
    score: float


def load_wordlist() -> Dict:
    return json.loads(WORDLIST_PATH.read_text(encoding="utf-8"))


def determine_category(pos_tags: List[str]) -> str:
    if "n" in pos_tags:
        return "noun"
    if "v" in pos_tags:
        return "verb"
    if "adj" in pos_tags:
        return "adjective"
    if "adv" in pos_tags:
        return "adverb"
    if "prep" in pos_tags:
        return "preposition"
    if "pron" in pos_tags:
        return "pronoun"
    return "other"


def compute_score(word: str, base: str, pos_set: Set[str], topics: List[str]) -> float:
    score = 0.0
    if pos_set & CORE_POS:
        score += 2.0
    if "adj" in pos_set:
        score += 0.5
    if "adv" in pos_set:
        score += 0.3
    if len(base) <= 6:
        score += 1.0
    elif len(base) <= 8:
        score += 0.5
    if base in TARGET_FLAT:
        score += 2.0
    if base in TRICKY_WORDS:
        score += 1.5
    if base in MONTHS:
        score += 2.5
    if base in NUMBERS:
        score += 1.2
    if topics:
        score += 0.3
    if " " in word:
        score -= 0.3
    if base.endswith("ing"):
        score += 0.2
    if base.endswith("ed"):
        score += 0.1
    if base in LOW_PRIORITY:
        score -= 1.5
    return score


def add_or_update(entries: Dict[str, Entry], *, word: str, pos: str, letter: str | None,
                  category: str | None, topics: List[str] | None, tags: List[str] | None,
                  manual_lock: bool, source: str) -> Entry:
    key = word.lower()
    entry = entries.get(key)
    if entry is None:
        entry = Entry(word=word, pos=pos, letter=letter or (word[0].upper() if word else ""),
                      category=category or "other", topics=topics or [], tags=tags or [],
                      _pos_tags=[p.strip() for p in pos.split(",") if p.strip()],
                      _source=source)
        entries[key] = entry
    else:
        if category and entry.get("category") == "other":
            entry["category"] = category
        if topics:
            merged = sorted(set(entry.get("topics", [])) | set(topics))
            entry["topics"] = merged
        if tags:
            merged = sorted(set(entry.get("tags", [])) | set(tags))
            entry["tags"] = merged
        if pos and not entry.get("pos"):
            entry["pos"] = pos
        if not entry.get("letter"):
            entry["letter"] = letter or (word[0].upper() if word else "")
        entry.setdefault("_pos_tags", [p.strip() for p in pos.split(",") if p.strip()])
    entry["_manual_lock"] = entry.get("_manual_lock", False) or manual_lock
    return entry


def build_entries() -> List[Entry]:
    data = load_wordlist()
    themes = data.get("themes", {})
    theme_map: Dict[str, Set[str]] = {}
    for theme, items in themes.items():
        for w in items:
            theme_map.setdefault(w.lower(), set()).add(theme)

    entries: Dict[str, Entry] = {}
    for raw in data["words"]:
        word = raw["word"]
        pos = raw.get("pos", "")
        pos_tags = [p.strip() for p in pos.split(",") if p.strip()]
        category = determine_category(pos_tags)
        topics = sorted(theme_map.get(word.lower(), []))
        tags: List[str] = []
        base = word.lower()
        if base in TARGET_FLAT:
            tags.append("target")
        if base in TRICKY_WORDS:
            tags.append("tricky")
        if base in MONTHS:
            tags.append("month")
        if base in NUMBERS:
            tags.append("number")
        entry = add_or_update(entries, word=word, pos=pos, letter=raw.get("letter"),
                              category=category, topics=topics, tags=tags,
                              manual_lock=False, source="wordlist")
        entry["_pos_tags"] = pos_tags

    # manual phrases
    for phrase in sorted(ALWAYS_INCLUDE_PHRASES):
        add_or_update(entries, word=phrase, pos="phrase", letter=phrase[0].upper(),
                      category="phrase", topics=[], tags=["phrase"], manual_lock=True,
                      source="manual-phrase")

    # months ensure lock
    for month in MONTHS:
        title = month.capitalize()
        add_or_update(entries, word=title, pos="n", letter=title[0], category="month",
                      topics=["calendar"], tags=["month"], manual_lock=True,
                      source="month")

    # numbers ensure lock
    for num in NUMBERS:
        add_or_update(entries, word=num, pos="n", letter=num[0].upper(), category="number",
                      topics=["numbers"], tags=["number"], manual_lock=True, source="number")

    # tricky/highlight words
    for tricky in TRICKY_WORDS:
        add_or_update(entries, word=tricky, pos="adj", letter=tricky[0].upper(),
                      category="adjective", topics=[], tags=["tricky"], manual_lock=True,
                      source="tricky")

    # listening names
    for name in NAMES:
        add_or_update(entries, word=name, pos="name", letter=name[0], category="name",
                      topics=["listening-part5"], tags=["name"], manual_lock=True,
                      source="name")

    # always include items
    for item in ALWAYS_INCLUDE:
        add_or_update(entries, word=item, pos="", letter=item[0].upper(), category="noun",
                      topics=[], tags=["core"], manual_lock=True, source="manual-core")

    return list(entries.values())


def evaluate_entries(entries: List[Entry]):
    always_exclude_lower = {w.lower() for w in ALWAYS_EXCLUDE}
    manual_phrase_set = {p.lower() for p in ALWAYS_INCLUDE_PHRASES}
    for entry in entries:
        word = entry["word"]
        base = word.lower()
        pos_tags = entry.get("_pos_tags", [])
        pos_set = set(pos_tags)
        manual_lock = entry.get("_manual_lock", False)
        if base in manual_phrase_set or base in {w.lower() for w in ALWAYS_INCLUDE}:
            manual_lock = True
        entry["_manual_lock"] = manual_lock
        if base in always_exclude_lower:
            entry["_eligible"] = False
            entry["_score"] = -math.inf
            continue
        if not pos_set:
            pos_set = set()
        if pos_set and pos_set.issubset(SKIP_POS) and not manual_lock:
            entry["_eligible"] = False
            entry["_score"] = -math.inf
            continue
        if not pos_set and not manual_lock:
            entry["_eligible"] = False
            entry["_score"] = -math.inf
            continue
        entry["_eligible"] = True
        entry["_score"] = compute_score(word, base, pos_set, entry.get("topics", []))


def select_required(entries: List[Entry]):
    evaluate_entries(entries)
    eligible = [e for e in entries if e.get("_eligible")]
    locked = [e for e in eligible if e.get("_manual_lock")]
    others = [e for e in eligible if not e.get("_manual_lock")]
    others.sort(key=lambda e: e.get("_score", 0), reverse=True)
    selected_ids = {id(e) for e in locked}
    limit = max(TARGET_LIMIT - len(locked), 0)
    for entry in others[:limit]:
        selected_ids.add(id(entry))
    for entry in entries:
        entry["write_required"] = id(entry) in selected_ids
        entry.pop("_manual_lock", None)
        entry.pop("_eligible", None)
        entry.pop("_score", None)
        entry.pop("_pos_tags", None)
        entry.pop("_source", None)


def main():
    entries = build_entries()
    select_required(entries)
    entries.sort(key=lambda e: (e.get("word", "").lower()))
    stats = sum(1 for e in entries if e.get("write_required"))
    result = {
        "version": 1,
        "generated_at": datetime.now(timezone.utc).isoformat(timespec="seconds"),
        "limit": TARGET_LIMIT,
        "words": entries,
        "metadata": {
            "write_required_count": stats,
            "names": len(NAMES),
            "phrases": len(ALWAYS_INCLUDE_PHRASES),
        },
    }
    OUTPUT_PATH.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Wrote {OUTPUT_PATH} with {len(entries)} entries; {stats} marked write_required")


if __name__ == "__main__":
    main()
