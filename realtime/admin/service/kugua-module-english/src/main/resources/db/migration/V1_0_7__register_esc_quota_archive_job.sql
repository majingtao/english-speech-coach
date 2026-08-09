-- 注册英语课配额归档定时任务（默认停止，管理员在后台"基础设施 → 定时任务"页开启）
-- cron: 每天 01:00，此时昨日 Redis 计数仍在 48h TTL 窗口内
INSERT INTO infra_job (name, status, handler_name, handler_param, cron_expression, retry_count, retry_interval, monitor_timeout)
VALUES ('英语课配额每日归档', 2, 'EscQuotaArchiveJob', '', '0 0 1 * * ?', 0, 0, 0);
