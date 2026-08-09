-- =====================================================================
-- V1_0_20: Free repeat-reading material library
-- =====================================================================

CREATE TABLE IF NOT EXISTS esc_reading_material (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    text_en          VARCHAR(500) NOT NULL COMMENT 'Word, phrase or sentence to play',
    text_cn          VARCHAR(500) DEFAULT '' COMMENT 'Chinese translation',
    description      VARCHAR(500) DEFAULT '' COMMENT 'Optional teacher-facing or learner-facing note',
    material_type    VARCHAR(16)  NOT NULL DEFAULT 'word' COMMENT 'word/phrase/sentence',
    part_of_speech   VARCHAR(24)  NOT NULL DEFAULT 'unknown' COMMENT 'noun/verb/adjective/adverb/phrase/sentence/unknown',
    level_code       VARCHAR(32)  NOT NULL DEFAULT 'ket',
    tags_json        TEXT         DEFAULT NULL COMMENT 'JSON array of tags',
    examples_json    LONGTEXT     DEFAULT NULL COMMENT 'JSON array of KET example sentences',
    word_forms_json  TEXT         DEFAULT NULL COMMENT 'JSON object of inflections',
    sort             INT          NOT NULL DEFAULT 0,
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '0 draft, 1 published, 2 archived',
    tenant_id        BIGINT       NOT NULL DEFAULT 0,
    creator          VARCHAR(64)  DEFAULT '',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)  DEFAULT '',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted          BIT(1)       NOT NULL DEFAULT 0,
    UNIQUE KEY uk_reading_material_text (tenant_id, level_code, text_en, deleted),
    INDEX idx_reading_material_level (level_code, status, sort),
    INDEX idx_reading_material_type (material_type, part_of_speech)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Free repeat-reading materials';

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT '自由跟读素材', '', 2, 43, 5047, 'reading-material', '#', 'english/readingMaterial/index', 0, b'1', b'1', b'1'
WHERE NOT EXISTS (
  SELECT 1 FROM system_menu WHERE parent_id = 5047 AND path = 'reading-material' AND deleted = b'0'
);

SET @reading_material_menu_id := (
    SELECT id FROM system_menu WHERE parent_id = 5047 AND path = 'reading-material' AND deleted = b'0'
    ORDER BY id LIMIT 1
);

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT p.name, p.permission, 3, p.sort, @reading_material_menu_id, '', '#', NULL, 0, b'1', b'1', b'1'
FROM (
    SELECT '自由跟读查询' name, 'english:reading-material:query' permission, 1 sort
    UNION ALL SELECT '自由跟读创建', 'english:reading-material:create', 2
    UNION ALL SELECT '自由跟读更新', 'english:reading-material:update', 3
    UNION ALL SELECT '自由跟读删除', 'english:reading-material:delete', 4
) p
WHERE NOT EXISTS (
    SELECT 1 FROM system_menu m WHERE m.permission = p.permission AND m.deleted = b'0'
);
