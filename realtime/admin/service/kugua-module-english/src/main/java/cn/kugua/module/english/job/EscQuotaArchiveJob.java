package cn.kugua.module.english.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.kugua.module.english.service.quota.EscQuotaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 英语课配额每日归档。
 *
 * 建议配置：cron 每天 01:00 执行（Redis key 保留 48h，保证昨天的计数还在）。
 * param 可以留空（走昨天），也可以传 yyyy-MM-dd 强制补跑某一天。
 *
 * 在 yudao 后台 "基础设施 → 定时任务" 注册：
 *   handlerName  = EscQuotaArchiveJob
 *   cronExpression = 0 0 1 * * ?
 *   handlerParam = （留空 / yyyy-MM-dd）
 */
@Slf4j
@Component
public class EscQuotaArchiveJob implements JobHandler {

    @Resource
    private EscQuotaService quotaService;

    @Override
    public String execute(String param) {
        LocalDate date;
        if (param != null && !param.isBlank()) {
            try {
                date = LocalDate.parse(param.trim());
            } catch (Exception e) {
                return "param 非法，需为 yyyy-MM-dd 或留空: " + param;
            }
            int n = quotaService.archiveUsageForDate(date);
            return String.format("manual archive date=%s users=%d", date, n);
        }
        int n = quotaService.archiveYesterdayUsage();
        return String.format("archived users=%d", n);
    }

}
