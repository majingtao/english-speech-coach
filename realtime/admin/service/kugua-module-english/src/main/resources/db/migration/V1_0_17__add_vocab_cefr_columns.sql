-- =====================================================================
-- V1_0_17: 词汇 CEFR 字段
--   - cefr        主级别（如 "A1" / "A2" / "B1"），用于卡片显示
--   - cefr_list   所有出现级别，逗号分隔（如 "A1,A2"），用于按 CEFR 多级筛选
-- =====================================================================

ALTER TABLE esc_vocab
    ADD COLUMN cefr VARCHAR(8) DEFAULT NULL COMMENT 'CEFR 主级别 A1/A2/B1',
    ADD COLUMN cefr_list VARCHAR(64) DEFAULT NULL COMMENT 'CEFR 全部级别，逗号分隔';
