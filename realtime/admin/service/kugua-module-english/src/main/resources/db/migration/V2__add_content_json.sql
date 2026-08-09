-- 试卷表加 content_json 列，存储完整 H5 题库 JSON
ALTER TABLE esc_exam ADD COLUMN content_json TEXT NULL COMMENT '试卷完整 JSON（H5 题库格式）' AFTER `status`;
