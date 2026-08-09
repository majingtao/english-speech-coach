-- ========== 英语课配额（LLM/ASR/TTS）==========

-- 全局默认配额（单行，后台可编辑）
CREATE TABLE IF NOT EXISTS `esc_quota_default` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键（固定为 1）',
    `llm_daily`       INT          NOT NULL DEFAULT 0 COMMENT 'LLM 每日调用次数',
    `asr_daily_sec`   INT          NOT NULL DEFAULT 0 COMMENT 'ASR 每日音频秒数',
    `tts_daily_chars` INT          NOT NULL DEFAULT 0 COMMENT 'TTS 每日合成字符数',
    `creator`         VARCHAR(64)  DEFAULT '',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater`         VARCHAR(64)  DEFAULT '',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='英语课全局默认配额';

-- 初始化一行默认值（管理员后台可改）
INSERT INTO `esc_quota_default` (`id`, `llm_daily`, `asr_daily_sec`, `tts_daily_chars`)
VALUES (1, 30, 600, 5000)
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 单用户覆盖（null 字段 = 走默认；enabled=0 时该用户被冻结）
CREATE TABLE IF NOT EXISTS `esc_user_quota` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`         BIGINT       NOT NULL COMMENT '会员 ID',
    `llm_daily`       INT          DEFAULT NULL COMMENT 'LLM 每日次数；null=用默认',
    `asr_daily_sec`   INT          DEFAULT NULL COMMENT 'ASR 每日秒数；null=用默认',
    `tts_daily_chars` INT          DEFAULT NULL COMMENT 'TTS 每日字符数；null=用默认',
    `enabled`         BIT(1)       NOT NULL DEFAULT b'1' COMMENT '是否启用，0 = 冻结',
    `remark`          VARCHAR(255) DEFAULT '' COMMENT '备注',
    `creator`         VARCHAR(64)  DEFAULT '',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater`         VARCHAR(64)  DEFAULT '',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0',
    `tenant_id`       BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='英语课单用户配额覆盖';

-- Phase 3 归档表（本期预建，暂无写入逻辑）
CREATE TABLE IF NOT EXISTS `esc_quota_usage_daily` (
    `id`             BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT   NOT NULL,
    `stat_date`      DATE     NOT NULL,
    `llm_used`       INT      NOT NULL DEFAULT 0,
    `asr_used_sec`   INT      NOT NULL DEFAULT 0,
    `tts_used_chars` INT      NOT NULL DEFAULT 0,
    `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='英语课每日用量归档（Phase 3 使用）';
