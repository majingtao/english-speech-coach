package cn.kugua.module.english.controller.app.expression.vo;

import lombok.Data;

@Data
public class AppExpressionAttemptRespVO {
    private Long attemptId;
    private Integer status;
    private Integer repetitions;
    private Integer intervalDays;
    private Integer bestSpeakingScore;
    private Integer bestWritingScore;
    private String nextReviewAt;
}
