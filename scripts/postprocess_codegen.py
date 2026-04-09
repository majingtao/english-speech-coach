#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
yudao codegen 后处理脚本
-----------------------
yudao 代码生成器默认按 `cn.iocoder.yudao.module.<moduleName>` 出包，
但 kugua 自研模块统一用 `cn.kugua.module.<moduleName>`。本脚本：

  1. 解压 codegen 下载的 zip（支持批量：一次处理 scripts/ 目录下所有 codegen-*.zip）
  2. 把所有 .java 文件里的 `cn.iocoder.yudao.module.english` 全局替换为 `cn.kugua.module.english`
  3. Java 文件 → kugua-module-english/src/main/java/cn/kugua/module/english/...
  4. Mapper XML → kugua-module-english/src/main/resources/mapper/...
  5. .vue / .ts 前端文件 → scripts/out/_frontend/...（按相对路径）
  6. menu SQL (sql/sql.sql) → scripts/out/_sql_menu/<zip_basename>.sql
  7. ErrorCodeConstants 追加片段 → scripts/out/_append/errcode/<zip_basename>.txt
     （不覆盖 kugua 里手写的 ErrorCodeConstants.java，需人工合并）

用法：
    python scripts/postprocess_codegen.py                    # 处理 scripts/codegen-*.zip
    python scripts/postprocess_codegen.py path/to/xxx.zip    # 处理单个
    python scripts/postprocess_codegen.py a.zip b.zip        # 处理多个
"""

import shutil
import sys
import zipfile
from pathlib import Path

SCRIPTS = Path(__file__).resolve().parent
ROOT = SCRIPTS.parent
MODULE_ROOT = ROOT / "realtime" / "admin" / "service" / "kugua-module-english"
JAVA_ROOT = MODULE_ROOT / "src" / "main" / "java"
RES_ROOT = MODULE_ROOT / "src" / "main" / "resources"
OUT = SCRIPTS / "out"
FRONTEND_STAGE = OUT / "_frontend"
SQL_MENU_STAGE = OUT / "_sql_menu"
ERRCODE_STAGE = OUT / "_append" / "errcode"
WORK_ROOT = OUT / "_codegen_work"

OLD_PKG = "cn.iocoder.yudao.module.english"
NEW_PKG = "cn.kugua.module.english"
OLD_PATH = "cn/iocoder/yudao/module/english"
NEW_PATH = "cn/kugua/module/english"


def decode_name(name: str) -> str:
    """yudao codegen 的 zip 里中文文件名是 GBK 编码，zipfile 按 cp437 读出来是乱码。还原之。"""
    try:
        return name.encode("cp437").decode("gbk")
    except Exception:
        return name


def extract_all(zip_path: Path, dest: Path) -> None:
    dest.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(zip_path, "r") as zf:
        for info in zf.infolist():
            name = decode_name(info.filename)
            target = dest / name
            if info.is_dir():
                target.mkdir(parents=True, exist_ok=True)
                continue
            target.parent.mkdir(parents=True, exist_ok=True)
            with zf.open(info) as src, open(target, "wb") as dst:
                shutil.copyfileobj(src, dst)


def handle_java(src_file: Path, stats: dict) -> None:
    s = str(src_file).replace("\\", "/")
    name = src_file.name
    # ErrorCodeConstants_字段追加.java  是 codegen 追加片段，不覆盖手写版本
    if name.startswith("ErrorCodeConstants"):
        ERRCODE_STAGE.mkdir(parents=True, exist_ok=True)
        dest = ERRCODE_STAGE / f"{stats['current_zip']}.txt"
        text = src_file.read_text(encoding="utf-8", errors="replace")
        dest.write_text(text, encoding="utf-8")
        stats["errcode"] += 1
        return
    if OLD_PATH not in s:
        stats["skipped"] += 1
        return
    tail = s.split(OLD_PATH, 1)[1].lstrip("/")
    dest = JAVA_ROOT / NEW_PATH / tail
    dest.parent.mkdir(parents=True, exist_ok=True)
    text = src_file.read_text(encoding="utf-8")
    text = text.replace(OLD_PKG, NEW_PKG)
    text = fix_localdate_imports(text)
    dest.write_text(text, encoding="utf-8")
    stats["java"] += 1


def fix_localdate_imports(text: str) -> str:
    """yudao codegen bug 修补：本该写 `import java.time.LocalDate;` 的地方
    被错写成了 `import java.time.LocalDateTime;`，且每个 LocalDate 字段重复一次。
    本函数：(1) 把 java.time.LocalDateTime 的 import 去重；
            (2) 若文件正文用了 LocalDate（非 LocalDateTime），补一行 LocalDate import。
    """
    import re
    lines = text.split("\n")
    seen_ldt = False
    out = []
    for ln in lines:
        if ln.strip() == "import java.time.LocalDateTime;":
            if seen_ldt:
                continue
            seen_ldt = True
        out.append(ln)
    new_text = "\n".join(out)
    # 是否用到了 LocalDate（不算 LocalDateTime）
    body_uses_localdate = re.search(r"\bLocalDate\b(?!Time)", new_text) is not None
    has_localdate_import = "import java.time.LocalDate;" in new_text
    if body_uses_localdate and not has_localdate_import:
        if "import java.time.LocalDateTime;" in new_text:
            new_text = new_text.replace(
                "import java.time.LocalDateTime;",
                "import java.time.LocalDate;\nimport java.time.LocalDateTime;",
                1,
            )
        else:
            # 没有任何 java.time import：插在 java.util.* 之后，否则插在第一个 import 之后
            anchor = "import java.util.*;"
            if anchor in new_text:
                new_text = new_text.replace(
                    anchor, anchor + "\nimport java.time.LocalDate;", 1
                )
            else:
                new_text = re.sub(
                    r"(^import [^\n]+;\n)",
                    r"\1import java.time.LocalDate;\n",
                    new_text,
                    count=1,
                    flags=re.MULTILINE,
                )
    return new_text


def handle_xml(src_file: Path, stats: dict) -> None:
    """mapper xml 放到 resources/mapper/<group>/xxx.xml，并改 namespace 包名。"""
    s = str(src_file).replace("\\", "/")
    marker = "src/main/resources/"
    if marker not in s:
        stats["skipped"] += 1
        return
    tail = s.split(marker, 1)[1]
    dest = RES_ROOT / tail
    dest.parent.mkdir(parents=True, exist_ok=True)
    text = src_file.read_text(encoding="utf-8")
    text = text.replace(OLD_PKG, NEW_PKG)
    dest.write_text(text, encoding="utf-8")
    stats["xml"] += 1


def handle_frontend(src_file: Path, work_dir: Path, stats: dict) -> None:
    # 兼容两种 codegen 前端模板：
    #   yudao-ui-admin-vue3/  (Element Plus 标准模板)
    #   yudao-ui-admin-vben/  (vben5 + Ant Design Vue, schema 模板)
    s = str(src_file).replace("\\", "/")
    for marker in ("yudao-ui-admin-vben/", "yudao-ui-admin-vue3/"):
        if marker in s:
            tail = s.split(marker, 1)[1]
            break
    else:
        stats["skipped"] += 1
        return
    dest = FRONTEND_STAGE / tail
    dest.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src_file, dest)
    stats["frontend"] += 1


def handle_sql(src_file: Path, stats: dict) -> None:
    """每个 zip 只有一份 sql/sql.sql，按 zip 名重命名归档。"""
    SQL_MENU_STAGE.mkdir(parents=True, exist_ok=True)
    dest = SQL_MENU_STAGE / f"{stats['current_zip']}.sql"
    shutil.copy2(src_file, dest)
    stats["sql"] += 1


def process_one(zip_path: Path, stats: dict) -> None:
    zip_name = zip_path.stem  # e.g. codegen-ExamLevel
    stats["current_zip"] = zip_name
    work = WORK_ROOT / zip_name
    if work.exists():
        shutil.rmtree(work)
    print(f"\n[zip] {zip_path.name}")
    extract_all(zip_path, work)

    for f in work.rglob("*"):
        if not f.is_file():
            continue
        suffix = f.suffix.lower()
        if suffix == ".java":
            handle_java(f, stats)
        elif suffix == ".xml":
            handle_xml(f, stats)
        elif suffix in {".vue", ".ts", ".tsx", ".js"}:
            handle_frontend(f, work, stats)
        elif suffix == ".sql":
            handle_sql(f, stats)


def main() -> None:
    # 收集要处理的 zip
    if len(sys.argv) >= 2:
        zips = [Path(p).resolve() for p in sys.argv[1:]]
    else:
        zips = sorted(SCRIPTS.glob("codegen-*.zip"))
    if not zips:
        print("no codegen-*.zip found in scripts/")
        sys.exit(1)

    # 清理工作区（保留之前 _append / _sql_menu / _frontend 不自动清，避免误删）
    if WORK_ROOT.exists():
        shutil.rmtree(WORK_ROOT)
    WORK_ROOT.mkdir(parents=True)

    stats = {
        "java": 0, "xml": 0, "frontend": 0, "sql": 0, "errcode": 0,
        "skipped": 0, "current_zip": "",
    }

    for z in zips:
        if not z.exists():
            print(f"[skip] not found: {z}")
            continue
        process_one(z, stats)

    print("\n" + "=" * 60)
    print(f"zips processed     : {len(zips)}")
    print(f"java -> kugua       : {stats['java']}")
    print(f"xml  -> kugua res   : {stats['xml']}")
    print(f"vue/ts -> frontend  : {stats['frontend']}")
    print(f"sql  menu  archived : {stats['sql']}")
    print(f"errcode snippets    : {stats['errcode']}")
    print(f"skipped             : {stats['skipped']}")
    print("=" * 60)
    print(f"kugua java  : {JAVA_ROOT / NEW_PATH}")
    print(f"kugua res   : {RES_ROOT / 'mapper'}")
    print(f"frontend    : {FRONTEND_STAGE}")
    print(f"sql menu    : {SQL_MENU_STAGE}")
    print(f"errcode     : {ERRCODE_STAGE}")
    print()
    print("下一步：")
    print("  1. IDEA 里刷新 kugua-module-english 模块，查看是否有红色 import")
    print("  2. 把 scripts/out/_append/errcode/*.txt 里的新错误码合并到")
    print("     kugua-module-english/.../enums/ErrorCodeConstants.java")
    print("  3. 手工把 scripts/out/_sql_menu/*.sql 里的菜单 INSERT 跑到 MySQL")
    print("     （先把里面的父菜单 ID 改成'英语口语'那个目录菜单的 ID）")
    print("  4. 前端文件 scripts/out/_frontend/src/views/english/ 贴到 vben-admin")
    print("  5. mvn -pl kugua-module-english -am clean install -DskipTests")


if __name__ == "__main__":
    main()
