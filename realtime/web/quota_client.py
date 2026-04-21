"""Thin async client for yudao's /app-api/esc/quota/consume.

server.py forwards the Authorization header from the incoming H5 request;
yudao resolves the member userId from the JWT. No local token state.

Business codes (raised as QuotaError.code) — see kugua-module-english/.../ErrorCodeConstants:
  1_040_060_002  账号被冻结
  1_040_060_003  资源类型不合法
  1_040_060_010  LLM 额度已用完
  1_040_060_011  ASR 额度已用完
  1_040_060_012  TTS 额度已用完
"""

import logging
import math
import os

log = logging.getLogger(__name__)

YUDAO_BASE_URL = os.environ.get("YUDAO_BASE_URL", "http://localhost:48080")
YUDAO_TENANT_ID = os.environ.get("YUDAO_TENANT_ID", "1")
QUOTA_TIMEOUT_SEC = float(os.environ.get("QUOTA_TIMEOUT_SEC", "5"))
# 无 Bearer 时是否放行：开发期默认 True，生产可通过 env 关掉
QUOTA_FAIL_OPEN_ANON = os.environ.get("QUOTA_FAIL_OPEN_ANON", "true").lower() not in ("0", "false", "no")


class QuotaError(Exception):
    """yudao returned code != 0 — quota rejected."""
    def __init__(self, code: int, msg: str):
        super().__init__(msg)
        self.code = code
        self.msg = msg


def _is_quota_code(code: int) -> bool:
    return 1_040_060_000 <= code <= 1_040_060_099


async def consume(session, authorization, resource: str, amount) -> dict:
    """POST /app-api/esc/quota/consume. Raises QuotaError on business rejection.

    Fails open on network/timeout errors so outages don't freeze the UX —
    Phase 3 will add proper archival reconciliation.
    """
    if not authorization:
        if QUOTA_FAIL_OPEN_ANON:
            return {"allowed": True, "remaining": None, "dailyLimit": None, "anon": True}
        raise QuotaError(401, "未登录无法消费配额")

    amt = max(int(math.ceil(float(amount or 0))), 1)
    url = f"{YUDAO_BASE_URL}/app-api/esc/quota/consume"
    headers = {
        "Authorization": authorization,
        "Content-Type": "application/json",
        "tenant-id": YUDAO_TENANT_ID,
    }
    body = {"resource": resource, "amount": amt}

    try:
        async with session.post(url, json=body, headers=headers, timeout=QUOTA_TIMEOUT_SEC) as resp:
            data = await resp.json(content_type=None)
    except Exception as e:
        log.warning("[quota] consume network error (%s %s): %s — fail-open", resource, amt, e)
        return {"allowed": True, "remaining": None, "dailyLimit": None, "degraded": True}

    code = int(data.get("code", -1))
    if code == 0:
        d = data.get("data") or {}
        return {
            "allowed": bool(d.get("allowed", True)),
            "remaining": d.get("remaining"),
            "dailyLimit": d.get("dailyLimit"),
        }
    msg = data.get("msg") or f"quota code {code}"
    log.info("[quota] rejected %s amount=%s code=%s msg=%s", resource, amt, code, msg)
    raise QuotaError(code, msg)
