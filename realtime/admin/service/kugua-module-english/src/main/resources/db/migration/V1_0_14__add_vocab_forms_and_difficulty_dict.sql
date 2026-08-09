-- =====================================================================
-- V1_0_14: 词汇模块补强
--   1. esc_vocab 新增 forms_json 列，存词形变化（如 decide → decided/deciding/decides）
--   2. 新增字典 english_vocab_difficulty（替代前端硬编码 1=A2 必备 / 2=B1 延展）
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) 词形变化列（结构灵活，按词性可填 past / ing / plural / comparative ...）
-- ---------------------------------------------------------------------
ALTER TABLE esc_vocab
    ADD COLUMN forms_json TEXT DEFAULT NULL COMMENT '词形变化 JSON：动词 past/past_participle/ing/third_person，名词 plural，形容词/副词 comparative/superlative';

-- ---------------------------------------------------------------------
-- 2) 字典：英语词汇难度
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `system_dict_type`
    (`name`, `type`, `status`, `remark`, `creator`)
VALUES
    ('英语词汇难度', 'english_vocab_difficulty', 0, 'KET：1=A2 必备 / 2=B1 延展', 'admin');

INSERT IGNORE INTO `system_dict_data`
    (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`)
VALUES
    (1, 'A2 必备', '1', 'english_vocab_difficulty', 0, 'primary', '', 'KET 核心词',  'admin'),
    (2, 'B1 延展', '2', 'english_vocab_difficulty', 0, 'warning', '', 'KET 延展词',  'admin');
