package cn.kugua.module.english.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * English Speech Coach 错误码
 * <p>
 * kugua-module-english 错误码区间：1-040-000-000 ~ 1-040-999-999
 */
public interface ErrorCodeConstants {

    // ========== 试卷 1-040-001-000 ==========
    ErrorCode EXAM_NOT_EXISTS           = new ErrorCode(1_040_001_000, "试卷不存在");
    ErrorCode EXAM_CODE_DUPLICATE       = new ErrorCode(1_040_001_001, "相同 exam_code + version 的试卷已存在");
    ErrorCode EXAM_LEVEL_NOT_EXISTS     = new ErrorCode(1_040_001_002, "考试级别不存在");

    // ========== 题型 Part 1-040-002-000 ==========
    ErrorCode EXAM_PART_NOT_EXISTS      = new ErrorCode(1_040_002_000, "试卷题段不存在");
    ErrorCode EXAM_PART_TYPE_MISMATCH   = new ErrorCode(1_040_002_001, "题型与题段类型不匹配");
    ErrorCode EXAM_PART_TYPE_NOT_EXISTS = new ErrorCode(1_040_002_002, "题型字典不存在");

    // ========== 题型子表 - 找不同 1-040-003-000 ==========
    ErrorCode PART_FIND_DIFF_PAIR_NOT_EXISTS       = new ErrorCode(1_040_003_000, "找不同 - 图对不存在");
    ErrorCode PART_FIND_DIFF_DIFFERENCE_NOT_EXISTS = new ErrorCode(1_040_003_001, "找不同 - 差异点不存在");

    // ========== 题型子表 - 信息互换 1-040-004-000 ==========
    ErrorCode PART_INFO_EXCHANGE_CARD_NOT_EXISTS   = new ErrorCode(1_040_004_000, "信息互换 - 卡片不存在");
    ErrorCode PART_INFO_EXCHANGE_QA_NOT_EXISTS     = new ErrorCode(1_040_004_001, "信息互换 - 问答条目不存在");

    // ========== 题型子表 - 讲故事 1-040-005-000 ==========
    ErrorCode PART_TELL_STORY_FRAME_NOT_EXISTS     = new ErrorCode(1_040_005_000, "讲故事 - 单帧不存在");

    // ========== 题型子表 - 个人问答 1-040-006-000 ==========
    ErrorCode PART_PERSONAL_QUESTION_NOT_EXISTS    = new ErrorCode(1_040_006_000, "个人问答 - 问题不存在");
    ErrorCode PART_PERSONAL_QA_SAMPLE_NOT_EXISTS   = new ErrorCode(1_040_006_001, "个人问答 - 示例答案不存在");

    // ========== 题型子表 - 协作任务 1-040-007-000 ==========
    ErrorCode PART_COLLAB_TASK_NOT_EXISTS          = new ErrorCode(1_040_007_000, "协作任务 - 主体不存在");

    // ========== 题型子表 - 独立长答 1-040-008-000 ==========
    ErrorCode PART_LONG_TURN_PHOTO_NOT_EXISTS      = new ErrorCode(1_040_008_000, "独立长答 - 图片描述不存在");

    // ========== 题型子表 - 一般对话 1-040-009-000 ==========
    ErrorCode PART_GENERAL_CONVO_TOPIC_NOT_EXISTS  = new ErrorCode(1_040_009_000, "一般对话 - 主题不存在");

    // ========== 班级 1-040-010-000 ==========
    ErrorCode SCHOOL_CLASS_NOT_EXISTS   = new ErrorCode(1_040_010_000, "班级不存在");

    // ========== 学生 1-040-011-000 ==========
    ErrorCode STUDENT_NOT_EXISTS        = new ErrorCode(1_040_011_000, "学生不存在");
    ErrorCode STUDENT_USERNAME_DUPLICATE = new ErrorCode(1_040_011_001, "学生账号已存在");
    ErrorCode STUDENT_PASSWORD_WRONG    = new ErrorCode(1_040_011_002, "学生账号或密码错误");

    // ========== 练习 1-040-020-000 ==========
    ErrorCode PRACTICE_SESSION_NOT_EXISTS = new ErrorCode(1_040_020_000, "练习会话不存在");
    ErrorCode PRACTICE_ANSWER_NOT_EXISTS  = new ErrorCode(1_040_020_001, "练习单题作答不存在");

    // ========== AI 配额 / 调用日志 1-040-030-000 ==========
    ErrorCode AI_QUOTA_EXCEEDED         = new ErrorCode(1_040_030_000, "当日 AI 调用次数已达上限");
    ErrorCode AI_CALL_LOG_NOT_EXISTS    = new ErrorCode(1_040_030_001, "AI 调用日志不存在");

}
