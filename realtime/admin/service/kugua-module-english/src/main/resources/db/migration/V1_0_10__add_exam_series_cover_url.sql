-- esc_exam_series 增加封面图 URL
ALTER TABLE `esc_exam_series`
    ADD COLUMN `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图 URL' AFTER `name`;
