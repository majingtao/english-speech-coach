-- esc_quota_default 是跨租户单行配置，BaseDO 只需要 deleted 即可
-- tenant_id 不建，改由 application.yaml 的 tenant.ignore-tables 放行
ALTER TABLE `esc_quota_default`
    ADD COLUMN `deleted` BIT(1) NOT NULL DEFAULT b'0';
