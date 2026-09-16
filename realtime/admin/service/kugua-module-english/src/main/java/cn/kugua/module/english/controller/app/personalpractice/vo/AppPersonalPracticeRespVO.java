package cn.kugua.module.english.controller.app.personalpractice.vo;

import lombok.Data;

@Data
public class AppPersonalPracticeRespVO {
    private Long id;
    private String practiceType;
    private String title;
    private String promptEn;
    private String promptCn;
    private String referenceJson;
    private String contentPointsJson;
    private Integer minSentences;
    private Integer minWords;
    private Integer sort;
    private Integer progressStatus;
    private Integer attemptCount;
    private Integer bestScore;
    private Integer lastScore;
    private String lastPracticeAt;
}
