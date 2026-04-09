-- =====================================================================
-- esc_patch_comments.sql
-- 补齐 esc_* 表 20 张子表的 creator/create_time/updater/update_time/deleted
-- 审计字段 COMMENT —— yudao codegen 对字段 COMMENT 是必填校验。
--
-- 适用于：已经跑过 esc_init.sql v2（24 张表）的库。
-- 安全性：只 MODIFY COLUMN 加 COMMENT，不改类型、默认值、NULL 约束。
-- 幂等：重复执行无副作用（MySQL MODIFY 会用新定义覆盖旧定义）。
--
-- 涉及表：tables 5~24（前 4 张 esc_exam_level / esc_exam_part_type /
-- esc_exam / esc_exam_part 在 init 里已经写全，不需要补）
-- =====================================================================

SET NAMES utf8mb4;

-- ---------- 5. esc_part_find_diff_pair ----------
ALTER TABLE `esc_part_find_diff_pair`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 6. esc_part_find_diff_difference ----------
ALTER TABLE `esc_part_find_diff_difference`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 7. esc_part_info_exchange_card ----------
ALTER TABLE `esc_part_info_exchange_card`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 8. esc_part_info_exchange_qa ----------
ALTER TABLE `esc_part_info_exchange_qa`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 9. esc_part_tell_story_frame ----------
ALTER TABLE `esc_part_tell_story_frame`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 10. esc_part_personal_question ----------
ALTER TABLE `esc_part_personal_question`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 11. esc_part_personal_qa_sample ----------
ALTER TABLE `esc_part_personal_qa_sample`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 12. esc_part_collab_task ----------
ALTER TABLE `esc_part_collab_task`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 13. esc_part_collab_option ----------
ALTER TABLE `esc_part_collab_option`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 14. esc_part_long_turn_photo ----------
ALTER TABLE `esc_part_long_turn_photo`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 15. esc_part_long_turn_keyword ----------
ALTER TABLE `esc_part_long_turn_keyword`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 16. esc_part_general_convo_topic ----------
ALTER TABLE `esc_part_general_convo_topic`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 17. esc_part_general_convo_followup ----------
ALTER TABLE `esc_part_general_convo_followup`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 18. esc_class ----------
ALTER TABLE `esc_class`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 19. esc_class_teacher ----------
ALTER TABLE `esc_class_teacher`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 20. esc_student ----------
ALTER TABLE `esc_student`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 21. esc_practice_session ----------
ALTER TABLE `esc_practice_session`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 22. esc_practice_part_score ----------
ALTER TABLE `esc_practice_part_score`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 23. esc_practice_answer ----------
ALTER TABLE `esc_practice_answer`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';

-- ---------- 24. esc_ai_call_log ----------
ALTER TABLE `esc_ai_call_log`
  MODIFY COLUMN `creator`     VARCHAR(64) NULL DEFAULT ''  COMMENT '创建人',
  MODIFY COLUMN `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN `updater`     VARCHAR(64) NULL DEFAULT ''  COMMENT '更新人',
  MODIFY COLUMN `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  MODIFY COLUMN `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除';
