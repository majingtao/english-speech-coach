package cn.kugua.module.english.enums;

/**
 * English Speech Coach 字典常量
 */
public interface DictTypeConstants {

    /** 考试级别（flyers / ket / pet），对应 esc_exam_level.code */
    String EXAM_LEVEL = "esc_exam_level";

    /** 题型（find_diff / info_exchange / ...），对应 esc_exam_part_type.code */
    String EXAM_PART_TYPE = "esc_exam_part_type";

    /** 练习模式：exam / free_talk */
    String PRACTICE_MODE = "esc_practice_mode";

    /** AI 服务类型：llm / asr / tts */
    String AI_SERVICE_TYPE = "esc_ai_service_type";

}
