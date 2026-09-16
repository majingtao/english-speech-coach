-- Personal speaking and writing practice for the currently enrolled child.

CREATE TABLE IF NOT EXISTS esc_personal_practice (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    practice_type       VARCHAR(16)  NOT NULL COMMENT 'speaking/writing',
    title               VARCHAR(128) NOT NULL,
    prompt_en           VARCHAR(1000) NOT NULL,
    prompt_cn           VARCHAR(1000) DEFAULT '',
    reference_json      LONGTEXT NOT NULL COMMENT 'Ordered bilingual reference lines',
    content_points_json TEXT DEFAULT NULL COMMENT 'KET task points to cover',
    min_sentences       INT NOT NULL DEFAULT 2,
    min_words           INT NOT NULL DEFAULT 0,
    sort                INT NOT NULL DEFAULT 0,
    status              TINYINT NOT NULL DEFAULT 1 COMMENT '0 draft, 1 published, 2 archived',
    tenant_id           BIGINT NOT NULL DEFAULT 0,
    creator             VARCHAR(64) DEFAULT '',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64) DEFAULT '',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             BIT(1) NOT NULL DEFAULT 0,
    INDEX idx_personal_practice_type (practice_type, status, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Personal fixed speaking and writing practice';

CREATE TABLE IF NOT EXISTS esc_personal_practice_progress (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    practice_id         BIGINT NOT NULL,
    status              TINYINT NOT NULL DEFAULT 0 COMMENT '0 new, 1 learning, 2 mastered',
    attempt_count       INT NOT NULL DEFAULT 0,
    best_score          INT NOT NULL DEFAULT 0,
    last_score          INT DEFAULT NULL,
    last_practice_at    DATETIME DEFAULT NULL,
    tenant_id           BIGINT NOT NULL DEFAULT 0,
    creator             VARCHAR(64) DEFAULT '',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64) DEFAULT '',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             BIT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_personal_practice_progress (user_id, practice_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Personal practice progress';

CREATE TABLE IF NOT EXISTS esc_personal_practice_attempt (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    practice_id         BIGINT NOT NULL,
    response_text       TEXT NOT NULL,
    grammar_score       INT NOT NULL DEFAULT 0 COMMENT '0-60',
    content_score       INT NOT NULL DEFAULT 0 COMMENT '0-40',
    total_score         INT NOT NULL DEFAULT 0,
    feedback_json       LONGTEXT DEFAULT NULL,
    duration_seconds    INT DEFAULT NULL,
    tenant_id           BIGINT NOT NULL DEFAULT 0,
    creator             VARCHAR(64) DEFAULT '',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64) DEFAULT '',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             BIT(1) NOT NULL DEFAULT 0,
    INDEX idx_personal_attempt_user (user_id, create_time),
    INDEX idx_personal_attempt_practice (practice_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Personal practice AI grading attempts';

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT '专属练习', '', 2, 43, 5047, 'personal-practice', '#', 'english/personalPractice/index', 0, b'1', b'1', b'1'
WHERE NOT EXISTS (
  SELECT 1 FROM system_menu WHERE parent_id = 5047 AND path = 'personal-practice' AND deleted = b'0'
);

SET @personal_practice_menu_id := (
    SELECT id FROM system_menu WHERE parent_id = 5047 AND path = 'personal-practice' AND deleted = b'0'
    ORDER BY id LIMIT 1
);

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT p.name, p.permission, 3, p.sort, @personal_practice_menu_id, '', '#', NULL, 0, b'1', b'1', b'1'
FROM (
    SELECT '专属练习查询' name, 'english:personal-practice:query' permission, 1 sort
    UNION ALL SELECT '专属练习创建', 'english:personal-practice:create', 2
    UNION ALL SELECT '专属练习更新', 'english:personal-practice:update', 3
    UNION ALL SELECT '专属练习删除', 'english:personal-practice:delete', 4
) p
WHERE NOT EXISTS (
    SELECT 1 FROM system_menu m WHERE m.permission = p.permission AND m.deleted = b'0'
);
