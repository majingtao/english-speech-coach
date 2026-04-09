#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
kugua-module-english 数据迁移脚本
--------------------------------
读取  realtime/web/question-bank.json
输出  sql/kugua-module-english/esc_seed_flyers.sql

将 17 套 Cambridge Flyers 题目按 esc_init.sql v2 (Option U) 结构灌库。
特点：
- 不依赖 pymysql，纯文本生成 SQL，先 review 再执行
- 使用 MySQL 用户变量 @xxx_id := LAST_INSERT_ID() 串联父子表 ID
- 全部公共题库：tenant_id = 0
- exam_code 取 JSON 里的 test id (gf_1 / f4_2 / aep1_3 ...)
- version=1, is_active=1, status=1 (已发布)
- 来源 (source) 由 test id 前缀推断

执行：
    cd E:/project/englishAi
    python sql/kugua-module-english/migrate_question_bank.py
然后：
    mysql -u root -p ruoyi-vue-pro < sql/kugua-module-english/esc_init.sql
    mysql -u root -p ruoyi-vue-pro < sql/kugua-module-english/esc_seed_flyers.sql
"""

import json
import os
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC_JSON = ROOT / "realtime" / "web" / "question-bank.json"
OUT_SQL = Path(__file__).resolve().parent / "esc_seed_flyers.sql"

SOURCE_MAP = {
    "gf":   ("Go Flyers Teacher's Notes", None),
    "f4":   ("Flyers 4",                  "2022"),
    "aep1": ("Authentic Exam Papers 1",   "2018"),
    "aep2": ("Authentic Exam Papers 2",   "2018"),
    "aep3": ("Authentic Exam Papers 3",   "2019"),
}


def esc(s):
    """SQL string escape (single quotes + backslashes)."""
    if s is None:
        return "NULL"
    s = str(s).replace("\\", "\\\\").replace("'", "''")
    return f"'{s}'"


def n(x):
    return "NULL" if x is None else str(x)


def source_for(test_id):
    prefix = test_id.rsplit("_", 1)[0]
    return SOURCE_MAP.get(prefix, (prefix, None))


def emit_exam(out, test_id, test):
    label = test.get("label", test_id)
    source, year = source_for(test_id)
    out.append("-- ============================================================")
    out.append(f"-- {test_id}  {label}")
    out.append("-- ============================================================")
    out.append(
        "INSERT INTO `esc_exam` "
        "(`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) "
        f"VALUES ({esc(test_id)},1,1,'flyers',{esc(label)},{esc(source)},{esc(year)},1,0);"
    )
    out.append("SET @exam_id := LAST_INSERT_ID();")
    out.append("")


def emit_part(out, part_no, part_type, title, intro):
    out.append(
        "INSERT INTO `esc_exam_part` "
        "(`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) "
        f"VALUES (@exam_id,{part_no},{esc(part_type)},{esc(title)},{esc(intro)},0);"
    )
    out.append("SET @part_id := LAST_INSERT_ID();")


def emit_part1(out, p1):
    """Find the Differences."""
    intro_parts = [p1.get("intro"), p1.get("example"), p1.get("instruction")]
    intro = "\n".join([x for x in intro_parts if x])
    emit_part(out, 1, "find_diff", p1.get("title"), intro)

    # 一个 part 一个 pair（题库里只有一组图）
    out.append(
        "INSERT INTO `esc_part_find_diff_pair` "
        "(`part_id`,`pair_no`,`topic`,`tenant_id`) "
        f"VALUES (@part_id,1,{esc(p1.get('scene'))},0);"
    )
    out.append("SET @pair_id := LAST_INSERT_ID();")

    for i, q in enumerate(p1.get("questions", []), start=1):
        # 用 expected 当作差异描述，examiner 句子放 keyword 字段（用于评分参考）
        out.append(
            "INSERT INTO `esc_part_find_diff_difference` "
            "(`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) "
            f"VALUES (@pair_id,{i},{esc(q.get('expected'))},{esc(q.get('examiner'))},{i},0);"
        )
    out.append("")


def emit_part2(out, p2):
    """Information Exchange."""
    intro_parts = [p2.get("context"), p2.get("intro")]
    intro = "\n".join([x for x in intro_parts if x])
    emit_part(out, 2, "info_exchange", p2.get("title"), intro)

    # phaseA card：考官提问，学生答
    out.append(
        "INSERT INTO `esc_part_info_exchange_card` "
        "(`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) "
        f"VALUES (@part_id,'A',{esc(p2.get('context'))},{esc(p2.get('intro'))},1,0);"
    )
    out.append("SET @cardA_id := LAST_INSERT_ID();")
    for i, qa in enumerate(p2.get("phaseA", []), start=1):
        out.append(
            "INSERT INTO `esc_part_info_exchange_qa` "
            "(`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) "
            f"VALUES (@cardA_id,{i},NULL,{esc(qa.get('examiner'))},{esc(qa.get('expected'))},{i},0);"
        )

    # phaseB card：学生提问，考官答
    out.append(
        "INSERT INTO `esc_part_info_exchange_card` "
        "(`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) "
        f"VALUES (@part_id,'B',{esc(p2.get('context'))},{esc(p2.get('transition'))},2,0);"
    )
    out.append("SET @cardB_id := LAST_INSERT_ID();")
    for i, qa in enumerate(p2.get("phaseB", []), start=1):
        out.append(
            "INSERT INTO `esc_part_info_exchange_qa` "
            "(`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) "
            f"VALUES (@cardB_id,{i},{esc(qa.get('hint'))},{esc(qa.get('expected_question'))},{esc(qa.get('answer'))},{i},0);"
        )
    out.append("")


def emit_part3(out, p3):
    """Tell the Story."""
    intro_parts = [p3.get("story_name"), p3.get("intro"), p3.get("instruction")]
    intro = "\n".join([x for x in intro_parts if x])
    emit_part(out, 3, "tell_story", p3.get("title"), intro)

    # frame_no=1 是考官给出的开头 (pic1)
    out.append(
        "INSERT INTO `esc_part_tell_story_frame` "
        "(`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) "
        f"VALUES (@part_id,1,{esc(p3.get('pic1'))},NULL,1,0);"
    )

    # 后续帧：每张图的 sentences 拍平（一帧可能有多个参考句子，合并成一行）
    sort = 2
    for pic in p3.get("pictures", []):
        frame_no = pic.get("pic", sort)
        sentences = pic.get("sentences", [])
        sentence_text = " ".join(s.get("text", "") for s in sentences).strip() or None
        # 合并 hint：图整体 prompt + 各句 hint
        hint_bits = []
        if pic.get("prompt"):
            hint_bits.append(pic["prompt"])
        for s in sentences:
            if s.get("hint"):
                hint_bits.append(s["hint"])
        hint = " | ".join(hint_bits) if hint_bits else None
        out.append(
            "INSERT INTO `esc_part_tell_story_frame` "
            "(`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) "
            f"VALUES (@part_id,{frame_no},{esc(sentence_text)},{esc(hint)},{sort},0);"
        )
        sort += 1
    out.append("")


def emit_part4(out, p4):
    """Personal Questions (含 followups)."""
    intro = p4.get("intro")
    emit_part(out, 4, "personal_qa", p4.get("title"), intro)

    main_topic = p4.get("topic")
    sort = 1

    def emit_question(q, topic):
        nonlocal sort
        out.append(
            "INSERT INTO `esc_part_personal_question` "
            "(`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) "
            f"VALUES (@part_id,{sort},{esc(q.get('examiner'))},{esc(topic)},{sort},0);"
        )
        out.append("SET @q_id := LAST_INSERT_ID();")
        sample = q.get("sample")
        if sample:
            out.append(
                "INSERT INTO `esc_part_personal_qa_sample` "
                "(`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) "
                f"VALUES (@q_id,1,{esc(sample)},NULL,1,0);"
            )
        sort += 1

    for q in p4.get("questions", []):
        emit_question(q, main_topic)
    for q in p4.get("followups", []):
        emit_question(q, (main_topic + " (followup)") if main_topic else "followup")
    out.append("")


def main():
    if not SRC_JSON.exists():
        raise SystemExit(f"source not found: {SRC_JSON}")
    data = json.loads(SRC_JSON.read_text(encoding="utf-8"))
    tests = data.get("tests", {})

    out = [
        "-- =====================================================================",
        "-- esc_seed_flyers.sql   (auto-generated, do not hand-edit)",
        "-- 由 sql/kugua-module-english/migrate_question_bank.py 从",
        "--   realtime/web/question-bank.json 生成",
        f"-- 共 {len(tests)} 套 Cambridge Flyers 试卷，全部 tenant_id=0 公共题库",
        "-- =====================================================================",
        "",
        "SET NAMES utf8mb4;",
        "START TRANSACTION;",
        "",
    ]

    for test_id, test in tests.items():
        emit_exam(out, test_id, test)
        if "part1" in test:
            emit_part1(out, test["part1"])
        if "part2" in test:
            emit_part2(out, test["part2"])
        if "part3" in test:
            emit_part3(out, test["part3"])
        if "part4" in test:
            emit_part4(out, test["part4"])

    out.append("COMMIT;")
    out.append("")

    OUT_SQL.write_text("\n".join(out), encoding="utf-8")
    print(f"[ok] wrote {OUT_SQL}  ({len(tests)} tests, {sum(1 for l in out if l.startswith('INSERT'))} INSERTs)")


if __name__ == "__main__":
    main()
