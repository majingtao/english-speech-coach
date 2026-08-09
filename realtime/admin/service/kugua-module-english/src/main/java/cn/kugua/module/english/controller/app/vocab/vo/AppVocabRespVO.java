package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "H5 - 词汇详情 Response VO")
@Data
public class AppVocabRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "单词")
    private String word;

    @Schema(description = "级别")
    private String levelCode;

    @Schema(description = "词性")
    private String pos;

    @Schema(description = "难度 1=A2 2=B1")
    private Integer difficulty;

    @Schema(description = "内容 JSON：{definition_cn, definition_en, ipa, examples, ...}")
    private String contentJson;

    @Schema(description = "词形变化 JSON：{past, past_participle, ing, third_person, plural, comparative, superlative}")
    private String formsJson;

    @Schema(description = "英式发音 URL（懒生成；null 表示尚未生成，需调 /vocab/{id}/audio 触发）")
    private String audioUkUrl;

    @Schema(description = "美式发音 URL（懒生成）")
    private String audioUsUrl;

    @Schema(description = "主题 code 列表")
    private List<String> themeCodes;

    @Schema(description = "学习进度 - 状态 0=新 1=学习中 2=已掌握 null=未学")
    private Integer progressStatus;

    @Schema(description = "学习进度 - 连续答对次数")
    private Integer repetitions;

    @Schema(description = "下一次复习时间 ISO 字符串")
    private String nextReviewAt;

}
