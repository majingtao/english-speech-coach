package cn.kugua.module.english.service.auth;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 主备熔断降级 MailSender。
 * <p>
 * 逻辑：优先走 primary（Resend）；如果 primary 连续失败 {@link #FAIL_THRESHOLD} 次，
 * 切换到 fallback（阿里云 DirectMail）并进入熔断冷却期 {@link #COOLDOWN_MS}。
 * 冷却期结束后自动重试 primary（半开状态）。
 */
@Slf4j
public class FailoverMailSender implements MailSender {

    private static final int FAIL_THRESHOLD = 3;
    private static final long COOLDOWN_MS = 60_000;

    private final MailSender primary;
    private final MailSender fallback;

    private final AtomicInteger failCount = new AtomicInteger(0);
    private final AtomicLong openSince = new AtomicLong(0);

    public FailoverMailSender(MailSender primary, MailSender fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public void sendVerificationCode(String to, String code, String scene) {
        if (shouldUsePrimary()) {
            try {
                primary.sendVerificationCode(to, code, scene);
                failCount.set(0);
                openSince.set(0);
                return;
            }
            catch (Exception e) {
                int fails = failCount.incrementAndGet();
                log.warn("[FailoverMail] primary 失败 ({}/{}), 降级到 fallback", fails, FAIL_THRESHOLD);
                if (fails >= FAIL_THRESHOLD) {
                    openSince.set(System.currentTimeMillis());
                }
            }
        }
        fallback.sendVerificationCode(to, code, scene);
    }

    private boolean shouldUsePrimary() {
        int fails = failCount.get();
        if (fails < FAIL_THRESHOLD) return true;
        long elapsed = System.currentTimeMillis() - openSince.get();
        if (elapsed >= COOLDOWN_MS) {
            // 半开：再给 primary 一次机会
            failCount.set(FAIL_THRESHOLD - 1);
            return true;
        }
        return false;
    }

}
