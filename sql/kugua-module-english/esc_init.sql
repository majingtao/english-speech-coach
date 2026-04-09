-- =====================================================================
-- kugua-module-english 初始化建表脚本  v2
-- 设计要点：
--   1. Option U 多态题型：esc_exam_part 为 part 头表，part_type 区分子表
--   2. 支持多级别（Flyers / KET / PET）通过 esc_exam_level 字典
--   3. 所有 JSON 字段全部拆为结构化字段或子表，便于后台单字段维护
--   4. 试卷版本化：exam_code 跨版本稳定，version 递增，is_active 标识当前生效
--   5. 多租户：tenant_id=0 表示公共题库，>0 表示租户私有
--   6. 软删除 + yudao 审计字段（creator/create_time/updater/update_time/deleted/tenant_id）
--
-- 共 24 张表：
--   题库公共  (4) : esc_exam_level / esc_exam_part_type / esc_exam / esc_exam_part
--   题型子表 (13) : find_diff (2) / info_exchange (2) / tell_story (1)
--                    personal_qa (2) / collab_task (2) / long_turn_photo (2) / general_convo (2)
--   班级学生  (3) : esc_class / esc_class_teacher / esc_student
--   练习记录  (3) : esc_practice_session / esc_practice_part_score / esc_practice_answer
--   调用日志  (1) : esc_ai_call_log
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 一、题库公共表
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. esc_exam_level   考试级别字典（flyers / ket / pet ...）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_exam_level`;
CREATE TABLE `esc_exam_level` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code`           VARCHAR(32)  NOT NULL                COMMENT '级别编码：flyers / ket / pet',
  `name`           VARCHAR(64)  NOT NULL                COMMENT '显示名：Cambridge Flyers / KET / PET',
  `cefr`           VARCHAR(8)   NULL                    COMMENT 'CEFR 等级：A2 / B1 ...',
  `description`    VARCHAR(255) NULL                    COMMENT '说明',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0=启用 1=停用',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试级别字典';

-- ---------------------------------------------------------------------
-- 2. esc_exam_part_type   题型字典（多态 part 的鉴别字段值）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_exam_part_type`;
CREATE TABLE `esc_exam_part_type` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code`           VARCHAR(32)  NOT NULL                COMMENT '题型编码',
  `name`           VARCHAR(64)  NOT NULL                COMMENT '显示名',
  `description`    VARCHAR(255) NULL                    COMMENT '说明',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0=启用 1=停用',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题型字典';

-- ---------------------------------------------------------------------
-- 3. esc_exam   试卷主表（V2 版本化、多级别、多租户）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_exam`;
CREATE TABLE `esc_exam` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exam_code`      VARCHAR(64)  NOT NULL                COMMENT '试卷稳定编码（跨版本不变），如 gf_1 / aep1_2',
  `version`        INT          NOT NULL DEFAULT 1      COMMENT '版本号，递增',
  `is_active`      TINYINT      NOT NULL DEFAULT 0      COMMENT '是否当前生效版本：0=否 1=是',
  `level_code`     VARCHAR(32)  NOT NULL                COMMENT '级别编码，引用 esc_exam_level.code',
  `label`          VARCHAR(128) NOT NULL                COMMENT '试卷显示名',
  `source`         VARCHAR(128) NULL                    COMMENT '来源（Go Flyers / Authentic Exam Papers ...）',
  `year`           VARCHAR(16)  NULL                    COMMENT '出题年份',
  `description`    VARCHAR(512) NULL                    COMMENT '描述',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0=草稿 1=发布 2=下架',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号，0=公共题库',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code_version_tenant` (`exam_code`, `version`, `tenant_id`),
  KEY `idx_level_active` (`level_code`, `is_active`),
  KEY `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷主表';

-- ---------------------------------------------------------------------
-- 4. esc_exam_part   试卷 Part 多态头表（Option U）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_exam_part`;
CREATE TABLE `esc_exam_part` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exam_id`        BIGINT       NOT NULL                COMMENT '所属试卷 esc_exam.id',
  `part_no`        TINYINT      NOT NULL                COMMENT '题段序号 1/2/3/4',
  `part_type`      VARCHAR(32)  NOT NULL                COMMENT '题型编码，引用 esc_exam_part_type.code',
  `title`          VARCHAR(128) NULL                    COMMENT 'Part 显示标题',
  `intro`          TEXT         NULL                    COMMENT 'Part 引导语 / 考官口述',
  `time_limit_sec` INT          NULL                    COMMENT '时间限制（秒），可为空',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_part_no` (`exam_id`, `part_no`),
  KEY `idx_part_type` (`part_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷 Part 多态头表';

-- =====================================================================
-- 二、题型子表（13 张）
-- =====================================================================

-- ---------------------------------------------------------------------
-- 5. esc_part_find_diff_pair   找不同 - 图对（Flyers Part1）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_find_diff_pair`;
CREATE TABLE `esc_part_find_diff_pair` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `pair_no`        INT          NOT NULL DEFAULT 1      COMMENT '图对序号',
  `image_a_url`    VARCHAR(512) NULL                    COMMENT '左图 URL',
  `image_b_url`    VARCHAR(512) NULL                    COMMENT '右图 URL',
  `topic`          VARCHAR(128) NULL                    COMMENT '主题，如 Two pictures of a kitchen',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_part` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='找不同 - 图对';

-- ---------------------------------------------------------------------
-- 6. esc_part_find_diff_difference   找不同 - 差异点（拆自 differences JSON）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_find_diff_difference`;
CREATE TABLE `esc_part_find_diff_difference` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pair_id`        BIGINT       NOT NULL                COMMENT 'esc_part_find_diff_pair.id',
  `diff_no`        INT          NOT NULL                COMMENT '差异点序号',
  `description`    VARCHAR(512) NOT NULL                COMMENT '差异描述（英文）',
  `keyword`        VARCHAR(128) NULL                    COMMENT '关键词，便于评分匹配',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_pair` (`pair_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='找不同 - 差异点';

-- ---------------------------------------------------------------------
-- 7. esc_part_info_exchange_card   信息互换 - 卡片（Flyers Part2）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_info_exchange_card`;
CREATE TABLE `esc_part_info_exchange_card` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `phase`          VARCHAR(8)   NOT NULL                COMMENT '阶段：A=学生提问 B=学生回答',
  `card_title`     VARCHAR(128) NULL                    COMMENT '卡片标题，如 Football Match',
  `card_image_url` VARCHAR(512) NULL                    COMMENT '卡片图片 URL',
  `intro`          TEXT         NULL                    COMMENT '卡片说明',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_part_phase` (`part_id`, `phase`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信息互换 - 卡片';

-- ---------------------------------------------------------------------
-- 8. esc_part_info_exchange_qa   信息互换 - 问答条目
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_info_exchange_qa`;
CREATE TABLE `esc_part_info_exchange_qa` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `card_id`        BIGINT       NOT NULL                COMMENT 'esc_part_info_exchange_card.id',
  `qa_no`          INT          NOT NULL                COMMENT '问答序号',
  `prompt_label`   VARCHAR(128) NULL                    COMMENT '提示项（如 Place / Date / Cost）',
  `question_text`  VARCHAR(512) NULL                    COMMENT '完整问句（可空，由 prompt_label 推断）',
  `answer_text`    VARCHAR(512) NULL                    COMMENT '参考答案',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_card` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信息互换 - 问答条目';

-- ---------------------------------------------------------------------
-- 9. esc_part_tell_story_frame   讲故事 - 单帧（Flyers Part3）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_tell_story_frame`;
CREATE TABLE `esc_part_tell_story_frame` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `frame_no`       INT          NOT NULL                COMMENT '帧序号 1~5',
  `image_url`      VARCHAR(512) NULL                    COMMENT '该帧图片 URL',
  `sentence_text`  VARCHAR(512) NULL                    COMMENT '该帧参考句子',
  `hint`           VARCHAR(512) NULL                    COMMENT '提示词 / 关键词',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_part` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讲故事 - 单帧';

-- ---------------------------------------------------------------------
-- 10. esc_part_personal_question   个人问答 - 问题（Flyers Part4 / KET Part2）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_personal_question`;
CREATE TABLE `esc_part_personal_question` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `q_no`           INT          NOT NULL                COMMENT '题号',
  `question_text`  VARCHAR(512) NOT NULL                COMMENT '问题',
  `topic`          VARCHAR(128) NULL                    COMMENT '主题分组',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_part` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='个人问答 - 问题';

-- ---------------------------------------------------------------------
-- 11. esc_part_personal_qa_sample   个人问答 - 示例答案
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_personal_qa_sample`;
CREATE TABLE `esc_part_personal_qa_sample` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `question_id`    BIGINT       NOT NULL                COMMENT 'esc_part_personal_question.id',
  `sample_no`      INT          NOT NULL DEFAULT 1      COMMENT '示例序号',
  `sample_text`    VARCHAR(1024) NOT NULL               COMMENT '示例答案',
  `level_tag`      VARCHAR(32)  NULL                    COMMENT '水平标签，如 basic / good',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='个人问答 - 示例答案';

-- ---------------------------------------------------------------------
-- 12. esc_part_collab_task   协作任务 - 主体（PET Part2 决策类）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_collab_task`;
CREATE TABLE `esc_part_collab_task` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `scenario`       VARCHAR(512) NOT NULL                COMMENT '情境描述',
  `image_url`      VARCHAR(512) NULL                    COMMENT '辅助图片 URL',
  `instruction`    TEXT         NULL                    COMMENT '任务指令',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_part` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='协作任务 - 主体';

-- ---------------------------------------------------------------------
-- 13. esc_part_collab_option   协作任务 - 备选项
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_collab_option`;
CREATE TABLE `esc_part_collab_option` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_id`        BIGINT       NOT NULL                COMMENT 'esc_part_collab_task.id',
  `option_no`      INT          NOT NULL                COMMENT '选项序号',
  `option_label`   VARCHAR(128) NOT NULL                COMMENT '选项文字',
  `option_image_url` VARCHAR(512) NULL                  COMMENT '选项图片',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='协作任务 - 备选项';

-- ---------------------------------------------------------------------
-- 14. esc_part_long_turn_photo   独立长答 - 图片描述（PET Part3）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_long_turn_photo`;
CREATE TABLE `esc_part_long_turn_photo` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `candidate_role` VARCHAR(16)  NOT NULL DEFAULT 'A'    COMMENT '面向的考生 A / B',
  `topic`          VARCHAR(128) NULL                    COMMENT '主题',
  `image_url`      VARCHAR(512) NOT NULL                COMMENT '图片 URL',
  `instruction`    TEXT         NULL                    COMMENT '考官指令',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_part` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='独立长答 - 图片描述';

-- ---------------------------------------------------------------------
-- 15. esc_part_long_turn_keyword   独立长答 - 提示关键词（拆自 hint_keywords JSON）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_long_turn_keyword`;
CREATE TABLE `esc_part_long_turn_keyword` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `photo_id`       BIGINT       NOT NULL                COMMENT 'esc_part_long_turn_photo.id',
  `keyword`        VARCHAR(128) NOT NULL                COMMENT '关键词',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_photo` (`photo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='独立长答 - 提示关键词';

-- ---------------------------------------------------------------------
-- 16. esc_part_general_convo_topic   一般对话 - 主题（PET Part4）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_general_convo_topic`;
CREATE TABLE `esc_part_general_convo_topic` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `topic`          VARCHAR(128) NOT NULL                COMMENT '对话主题',
  `intro`          TEXT         NULL                    COMMENT '主题引导',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_part` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='一般对话 - 主题';

-- ---------------------------------------------------------------------
-- 17. esc_part_general_convo_followup   一般对话 - 追问（拆自 follow_up_questions JSON）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_part_general_convo_followup`;
CREATE TABLE `esc_part_general_convo_followup` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `topic_id`       BIGINT       NOT NULL                COMMENT 'esc_part_general_convo_topic.id',
  `q_no`           INT          NOT NULL                COMMENT '追问序号',
  `question_text`  VARCHAR(512) NOT NULL                COMMENT '追问内容',
  `sample_answer`  VARCHAR(1024) NULL                   COMMENT '示例答案',
  `sort`           INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_topic` (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='一般对话 - 追问';

-- =====================================================================
-- 三、班级 / 学生
-- =====================================================================

-- ---------------------------------------------------------------------
-- 18. esc_class   班级
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_class`;
CREATE TABLE `esc_class` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`           VARCHAR(128) NOT NULL                COMMENT '班级名称',
  `code`           VARCHAR(64)  NULL                    COMMENT '班级编码（可选）',
  `description`    VARCHAR(512) NULL                    COMMENT '描述',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0=启用 1=停用',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级';

-- ---------------------------------------------------------------------
-- 19. esc_class_teacher   班级 ↔ 老师 多对多
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_class_teacher`;
CREATE TABLE `esc_class_teacher` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `class_id`       BIGINT       NOT NULL                COMMENT 'esc_class.id',
  `teacher_user_id` BIGINT      NOT NULL                COMMENT 'yudao 系统用户 system_users.id',
  `is_owner`       TINYINT      NOT NULL DEFAULT 0      COMMENT '是否班主任 0=否 1=是',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_teacher` (`class_id`, `teacher_user_id`),
  KEY `idx_teacher` (`teacher_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级老师关联';

-- ---------------------------------------------------------------------
-- 20. esc_student   学生（独立体系，不复用 yudao member）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_student`;
CREATE TABLE `esc_student` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`       VARCHAR(64)  NOT NULL                COMMENT '登录账号（短账号）',
  `password`       VARCHAR(128) NOT NULL                COMMENT '密码（BCrypt）',
  `nickname`       VARCHAR(64)  NULL                    COMMENT '昵称 / 真实姓名',
  `avatar`         VARCHAR(512) NULL                    COMMENT '头像 URL',
  `gender`         TINYINT      NULL                    COMMENT '性别 1=男 2=女',
  `birthday`       DATE         NULL                    COMMENT '生日',
  `class_id`       BIGINT       NULL                    COMMENT '所属班级 esc_class.id',
  `level_code`     VARCHAR(32)  NULL                    COMMENT '当前学习级别 flyers/ket/pet',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0=启用 1=停用',
  `last_login_time` DATETIME    NULL                    COMMENT '最近登录时间',
  `last_login_ip`  VARCHAR(64)  NULL                    COMMENT '最近登录 IP',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username_tenant` (`username`, `tenant_id`),
  KEY `idx_class` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- =====================================================================
-- 四、练习记录
-- =====================================================================

-- ---------------------------------------------------------------------
-- 21. esc_practice_session   一次练习会话
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_practice_session`;
CREATE TABLE `esc_practice_session` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id`     BIGINT       NOT NULL                COMMENT 'esc_student.id',
  `exam_id`        BIGINT       NOT NULL                COMMENT 'esc_exam.id（具体版本）',
  `exam_code`      VARCHAR(64)  NOT NULL                COMMENT '冗余 exam_code 便于跨版本统计',
  `mode`           VARCHAR(16)  NOT NULL                COMMENT '模式：exam / free_talk',
  `start_time`     DATETIME     NOT NULL                COMMENT '开始时间',
  `end_time`       DATETIME     NULL                    COMMENT '结束时间',
  `duration_sec`   INT          NULL                    COMMENT '总时长（秒）',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '0=进行中 1=已完成 2=已放弃',
  `final_overall_score` TINYINT NULL                    COMMENT '整卷综合分 0-100',
  `final_comment`  TEXT         NULL                    COMMENT '整卷综合评语',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_exam` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习会话';

-- ---------------------------------------------------------------------
-- 22. esc_practice_part_score   分 Part 得分（替代 score_detail JSON）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_practice_part_score`;
CREATE TABLE `esc_practice_part_score` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id`     BIGINT       NOT NULL                COMMENT 'esc_practice_session.id',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `part_no`        TINYINT      NOT NULL                COMMENT '冗余 part 序号',
  `score_grammar_vocab` TINYINT NULL                    COMMENT '语法词汇 0-100',
  `score_pronunciation` TINYINT NULL                    COMMENT '发音 0-100',
  `score_interaction`   TINYINT NULL                    COMMENT '互动 0-100',
  `score_discourse`     TINYINT NULL                    COMMENT '篇章组织 0-100（PET 及以上）',
  `score_overall`       TINYINT NULL                    COMMENT 'Part 综合分 0-100',
  `comment`        TEXT         NULL                    COMMENT 'Part 评语',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_part` (`session_id`, `part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习分 Part 得分';

-- ---------------------------------------------------------------------
-- 23. esc_practice_answer   单次问答记录（替代 llm_score JSON）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_practice_answer`;
CREATE TABLE `esc_practice_answer` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id`     BIGINT       NOT NULL                COMMENT 'esc_practice_session.id',
  `part_id`        BIGINT       NOT NULL                COMMENT 'esc_exam_part.id',
  `item_ref_table` VARCHAR(64)  NULL                    COMMENT '引用题目所在子表名',
  `item_ref_id`    BIGINT       NULL                    COMMENT '引用题目主键',
  `seq`            INT          NOT NULL                COMMENT '该 part 内的顺序',
  `question_snapshot` TEXT      NULL                    COMMENT '题目快照（防止题库后续变动）',
  `audio_url`      VARCHAR(512) NULL                    COMMENT '学生录音 URL',
  `asr_text`       TEXT         NULL                    COMMENT 'ASR 转写文本',
  `asr_engine`     VARCHAR(32)  NULL                    COMMENT 'ASR 引擎',
  `asr_duration_ms` INT         NULL                    COMMENT 'ASR 处理耗时（毫秒）',
  `score_grammar_vocab` TINYINT NULL                    COMMENT '语法词汇 0-100',
  `score_pronunciation` TINYINT NULL                    COMMENT '发音 0-100',
  `score_interaction`   TINYINT NULL                    COMMENT '互动 0-100',
  `score_discourse`     TINYINT NULL                    COMMENT '篇章组织 0-100（PET 及以上）',
  `score_overall`       TINYINT NULL                    COMMENT '综合分 0-100',
  `feedback_text`  TEXT         NULL                    COMMENT 'LLM 中文反馈',
  `corrected_text` TEXT         NULL                    COMMENT 'LLM 修正版本',
  `llm_engine`     VARCHAR(64)  NULL                    COMMENT 'LLM 引擎 / 模型名',
  `llm_raw_response` TEXT       NULL                    COMMENT '原始 LLM 响应（调试 / 审计）',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_session_part` (`session_id`, `part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习单题作答';

-- =====================================================================
-- 五、AI 调用日志
-- =====================================================================

-- ---------------------------------------------------------------------
-- 24. esc_ai_call_log   LLM / ASR / TTS 调用日志（用于配额、审计）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `esc_ai_call_log`;
CREATE TABLE `esc_ai_call_log` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id`     BIGINT       NULL                    COMMENT 'esc_student.id（学生发起时）',
  `user_id`        BIGINT       NULL                    COMMENT 'system_users.id（管理员/老师发起时）',
  `service_type`   VARCHAR(16)  NOT NULL                COMMENT '服务类型 llm / asr / tts',
  `engine`         VARCHAR(64)  NULL                    COMMENT '具体引擎名',
  `endpoint`       VARCHAR(255) NULL                    COMMENT '调用端点',
  `request_size`   INT          NULL                    COMMENT '请求字节数',
  `response_size`  INT          NULL                    COMMENT '响应字节数',
  `duration_ms`    INT          NULL                    COMMENT '耗时（毫秒）',
  `status_code`    INT          NULL                    COMMENT '响应码',
  `success`        TINYINT      NOT NULL DEFAULT 1      COMMENT '是否成功 1=成功 0=失败',
  `error_message`  VARCHAR(512) NULL                    COMMENT '错误信息',
  `client_ip`      VARCHAR(64)  NULL                    COMMENT '客户端 IP',
  `call_date`      DATE         NOT NULL                COMMENT '调用日期（用于按天配额）',
  `tenant_id`      BIGINT       NOT NULL DEFAULT 0      COMMENT '租户编号',
  `creator`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '创建人',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                  COMMENT '创建时间',
  `updater`        VARCHAR(64)  NULL DEFAULT ''         COMMENT '更新人',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIT(1)       NOT NULL DEFAULT b'0'   COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_student_date` (`student_id`, `call_date`),
  KEY `idx_user_date` (`user_id`, `call_date`),
  KEY `idx_tenant_date` (`tenant_id`, `call_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 调用日志';

-- =====================================================================
-- 六、字典种子数据
-- =====================================================================

INSERT INTO `esc_exam_level` (`code`, `name`, `cefr`, `description`, `sort`) VALUES
  ('flyers', 'Cambridge Flyers', 'A2', '剑桥少儿英语三级 A2 口语', 10),
  ('ket',    'Cambridge KET',    'A2', 'Key English Test A2 口语',  20),
  ('pet',    'Cambridge PET',    'B1', 'Preliminary English Test B1 口语', 30);

INSERT INTO `esc_exam_part_type` (`code`, `name`, `description`, `sort`) VALUES
  ('find_diff',       'Find the Differences',  'Flyers Part1 找不同',                       10),
  ('info_exchange',   'Information Exchange',  'Flyers Part2 信息互换',                     20),
  ('tell_story',      'Tell the Story',        'Flyers Part3 看图讲故事',                   30),
  ('personal_qa',     'Personal Questions',    'Flyers Part4 / KET Part2 个人问答',         40),
  ('collab_task',     'Collaborative Task',    'PET Part2 协作决策',                        50),
  ('long_turn_photo', 'Long Turn Photo',       'PET Part3 看图独立长答',                    60),
  ('general_convo',   'General Conversation',  'PET Part4 一般对话 / KET Part1 信息交换',   70);

SET FOREIGN_KEY_CHECKS = 1;
