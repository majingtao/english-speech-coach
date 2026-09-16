-- =====================================================================
-- V1_0_22: Repeat-reading spelling priority and split practice stats
-- =====================================================================

ALTER TABLE esc_reading_material
    ADD COLUMN must_spell TINYINT NOT NULL DEFAULT 0 COMMENT '1 must-spell material' AFTER high_frequency;

ALTER TABLE esc_user_reading_material_progress
    ADD COLUMN read_correct_count INT NOT NULL DEFAULT 0 COMMENT 'Read/recognition correct count' AFTER material_id,
    ADD COLUMN read_wrong_count INT NOT NULL DEFAULT 0 COMMENT 'Read/recognition wrong count' AFTER read_correct_count,
    ADD COLUMN spell_correct_count INT NOT NULL DEFAULT 0 COMMENT 'Spelling correct count' AFTER read_wrong_count,
    ADD COLUMN spell_wrong_count INT NOT NULL DEFAULT 0 COMMENT 'Spelling wrong count' AFTER spell_correct_count,
    ADD COLUMN last_mode VARCHAR(16) DEFAULT '' COMMENT 'read/spell' AFTER last_result;

UPDATE esc_user_reading_material_progress
SET read_correct_count = correct_count,
    read_wrong_count = wrong_count
WHERE correct_count > 0 OR wrong_count > 0;
