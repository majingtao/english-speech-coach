-- =====================================================================
-- V1_0_21: Reading material priority flags and learner self-check stats
-- =====================================================================

ALTER TABLE esc_reading_material
    ADD COLUMN must_know TINYINT NOT NULL DEFAULT 0 COMMENT '1 must-know material' AFTER word_forms_json,
    ADD COLUMN high_frequency TINYINT NOT NULL DEFAULT 0 COMMENT '1 high-frequency material' AFTER must_know;

CREATE TABLE IF NOT EXISTS esc_user_reading_material_progress (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT      NOT NULL,
    material_id        BIGINT      NOT NULL,
    correct_count      INT         NOT NULL DEFAULT 0,
    wrong_count        INT         NOT NULL DEFAULT 0,
    last_result        VARCHAR(16) DEFAULT '' COMMENT 'correct/wrong',
    last_practice_at   DATETIME    DEFAULT NULL,
    tenant_id          BIGINT      NOT NULL DEFAULT 0,
    creator            VARCHAR(64) DEFAULT '',
    create_time        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater            VARCHAR(64) DEFAULT '',
    update_time        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted            BIT(1)      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_reading_material (user_id, material_id, deleted),
    INDEX idx_user_reading_material_user (user_id, update_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Learner self-check stats for repeat-reading materials';
