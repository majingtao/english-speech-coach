-- =====================================================================
-- V1_0_18: 词汇能力要求字段
--   - mastery_level：1=三会(receptive，听说读)，2=四会(productive，听说读写)
--   - 听音拼写题源 = mastery_level=2 的词
--   - 默认 1（保守）：未明确标记的词不会进听写题
--   - 同时新增字典 english_vocab_mastery，admin 后台用 RadioGroup
-- =====================================================================

ALTER TABLE esc_vocab
    ADD COLUMN mastery_level TINYINT NOT NULL DEFAULT 1
    COMMENT '能力要求：1=三会(receptive)，2=四会(productive)';

-- 字典类型
INSERT IGNORE INTO `system_dict_type`
    (`name`, `type`, `status`, `remark`, `creator`)
VALUES
    ('英语词汇能力要求', 'english_vocab_mastery', 0, '1=三会(认读) / 2=四会(必须会拼写)', 'admin');

-- 字典数据
INSERT IGNORE INTO `system_dict_data`
    (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`)
VALUES
    (1, '三会（认读）', '1', 'english_vocab_mastery', 0, 'default', '', '只需听说读，不要求拼写', 'admin'),
    (2, '四会（必须会拼写）', '2', 'english_vocab_mastery', 0, 'success', '', '听写题源', 'admin');
