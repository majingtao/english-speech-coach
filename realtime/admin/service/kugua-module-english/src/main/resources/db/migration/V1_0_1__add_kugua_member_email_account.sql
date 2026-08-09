-- 邮箱/账号 扩展登录表（与 yudao member_user 一对一）
CREATE TABLE IF NOT EXISTS `kugua_member_email_account` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`         BIGINT       NOT NULL COMMENT '关联 member_user.id',
    `email`           VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `username`        VARCHAR(64)  DEFAULT NULL COMMENT '用户名/账号',
    `password`        VARCHAR(128) DEFAULT NULL COMMENT 'BCrypt 密码',
    `email_verified`  BIT(1)       NOT NULL DEFAULT b'0' COMMENT '邮箱是否已验证',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '0=启用 1=停用',
    `tenant_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '租户 ID',
    `creator`         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email`    (`email`, `deleted`),
    UNIQUE KEY `uk_username` (`username`, `deleted`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员邮箱/账号扩展登录表';
