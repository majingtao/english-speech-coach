package cn.kugua.module.english.controller.admin.practiceAnswer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 练习单题作答 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PracticeAnswerRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1740")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_practice_session.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "9594")
    @ExcelProperty("esc_practice_session.id")
    private Long sessionId;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "2400")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "引用题目所在子表名")
    @ExcelProperty("引用题目所在子表名")
    private String itemRefTable;

    @Schema(description = "引用题目主键", example = "719")
    @ExcelProperty("引用题目主键")
    private Long itemRefId;

    @Schema(description = "该 part 内的顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("该 part 内的顺序")
    private Integer seq;

    @Schema(description = "题目快照（防止题库后续变动）")
    @ExcelProperty("题目快照（防止题库后续变动）")
    private String questionSnapshot;

    @Schema(description = "学生录音 URL", example = "https://www.iocoder.cn")
    @ExcelProperty("学生录音 URL")
    private String audioUrl;

    @Schema(description = "ASR 转写文本")
    @ExcelProperty("ASR 转写文本")
    private String asrText;

    @Schema(description = "ASR 引擎")
    @ExcelProperty("ASR 引擎")
    private String asrEngine;

    @Schema(description = "ASR 处理耗时（毫秒）")
    @ExcelProperty("ASR 处理耗时（毫秒）")
    private Integer asrDurationMs;

    @Schema(description = "语法词汇 0-100")
    @ExcelProperty("语法词汇 0-100")
    private Integer scoreGrammarVocab;

    @Schema(description = "发音 0-100")
    @ExcelProperty("发音 0-100")
    private Integer scorePronunciation;

    @Schema(description = "互动 0-100")
    @ExcelProperty("互动 0-100")
    private Integer scoreInteraction;

    @Schema(description = "篇章组织 0-100（PET 及以上）")
    @ExcelProperty("篇章组织 0-100（PET 及以上）")
    private Integer scoreDiscourse;

    @Schema(description = "综合分 0-100")
    @ExcelProperty("综合分 0-100")
    private Integer scoreOverall;

    @Schema(description = "LLM 中文反馈")
    @ExcelProperty("LLM 中文反馈")
    private String feedbackText;

    @Schema(description = "LLM 修正版本")
    @ExcelProperty("LLM 修正版本")
    private String correctedText;

    @Schema(description = "LLM 引擎 / 模型名")
    @ExcelProperty("LLM 引擎 / 模型名")
    private String llmEngine;

    @Schema(description = "原始 LLM 响应（调试 / 审计）")
    @ExcelProperty("原始 LLM 响应（调试 / 审计）")
    private String llmRawResponse;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
