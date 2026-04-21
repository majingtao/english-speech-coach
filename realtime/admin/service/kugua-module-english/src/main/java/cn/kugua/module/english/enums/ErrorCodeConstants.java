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
    ErrorCode EXAM_SERIES_NOT_EXISTS    = new ErrorCode(1_040_001_003, "考试系列不存在");

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

    // ========== AI 模型配置 1-040-035-000 ==========
    ErrorCode AI_MODEL_NOT_EXISTS         = new ErrorCode(1_040_035_000, "AI 模型配置不存在");

    // ========== 听写 1-040-040-000 ==========
    ErrorCode DICTATION_WORD_NOT_EXISTS     = new ErrorCode(1_040_040_000, "听写单词不存在");
    ErrorCode DICTATION_WORD_DUPLICATE      = new ErrorCode(1_040_040_001, "单词已存在");
    ErrorCode DICTATION_WORDLIST_NOT_EXISTS = new ErrorCode(1_040_040_010, "词书不存在");

    // ========== 配额（LLM/ASR/TTS）1-040-060-000 ==========
    ErrorCode ESC_QUOTA_DEFAULT_NOT_EXISTS = new ErrorCode(1_040_060_000, "默认配额记录不存在");
    ErrorCode ESC_USER_QUOTA_NOT_EXISTS    = new ErrorCode(1_040_060_001, "用户配额覆盖不存在");
    ErrorCode ESC_QUOTA_USER_DISABLED      = new ErrorCode(1_040_060_002, "账号已被禁用，无法使用");
    ErrorCode ESC_QUOTA_RESOURCE_INVALID   = new ErrorCode(1_040_060_003, "资源类型非法（应为 llm/asr/tts）");
    ErrorCode ESC_QUOTA_LLM_EXCEEDED       = new ErrorCode(1_040_060_010, "当日 LLM 次数已用完");
    ErrorCode ESC_QUOTA_ASR_EXCEEDED       = new ErrorCode(1_040_060_011, "当日 ASR 秒数已用完");
    ErrorCode ESC_QUOTA_TTS_EXCEEDED       = new ErrorCode(1_040_060_012, "当日 TTS 字符数已用完");

    // ========== 邮箱/账号认证 1-040-050-000 ==========
    ErrorCode EMAIL_CODE_SEND_TOO_FAST    = new ErrorCode(1_040_050_000, "验证码发送太频繁，请稍后再试");
    ErrorCode EMAIL_CODE_EXPIRED          = new ErrorCode(1_040_050_001, "验证码已过期");
    ErrorCode EMAIL_CODE_NOT_MATCH        = new ErrorCode(1_040_050_002, "验证码不正确");
    ErrorCode EMAIL_ALREADY_REGISTERED    = new ErrorCode(1_040_050_003, "该邮箱已注册");
    ErrorCode USERNAME_ALREADY_REGISTERED = new ErrorCode(1_040_050_004, "该用户名已注册");
    ErrorCode AUTH_EMAIL_NOT_REGISTERED   = new ErrorCode(1_040_050_005, "该邮箱未注册");
    ErrorCode AUTH_BAD_CREDENTIALS        = new ErrorCode(1_040_050_006, "账号或密码错误");
    ErrorCode AUTH_ACCOUNT_DISABLED       = new ErrorCode(1_040_050_007, "账号已停用");

}
