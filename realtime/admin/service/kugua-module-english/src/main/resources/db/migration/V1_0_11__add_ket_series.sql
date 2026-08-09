-- =====================================================================
-- V1_0_11: KET 系列种子
--   KET 2020 改革后为 2 部分（Part 1 Interview + Part 2 Collaborative Task）
--   esc_exam_level 里 'ket' 已在 V1_0_9 里 seed，无需重复
--   此处只加系列（esc_exam_series）；具体试卷由 Python 生成脚本写入 esc_exam
-- =====================================================================

INSERT INTO `esc_exam_series` (`code`, `level_code`, `name`, `publisher`, `sort`, `status`, `description`)
SELECT * FROM (
    SELECT 'ket_practice' AS code, 'ket' AS level_code, 'KET 练习题' AS name,
           'AI Generated' AS publisher, 10 AS sort, 0 AS status,
           'AI 按 KET 2020 Speaking 标准生成的练习题' AS description
) AS seed
WHERE NOT EXISTS (
    SELECT 1 FROM `esc_exam_series` S WHERE S.`code` = seed.code
);
