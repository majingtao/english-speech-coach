package cn.kugua.module.english.controller.app.expression.vo;

import lombok.Data;

@Data
public class AppExpressionItemRespVO {
    private Long id;
    private Long themeId;
    private String code;
    private String promptEn;
    private String promptCn;
    private String functionCode;
    private String practiceMode;
    private Integer difficulty;
    private String answerJson;
    private String imageUrlsJson;
    private Integer sort;
    private Integer progressStatus;
    private Integer repetitions;
    private Integer bestSpeakingScore;
    private Integer bestWritingScore;
    private String nextReviewAt;
}
