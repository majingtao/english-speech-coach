package cn.kugua.module.english.controller.app.personalpractice.vo;

import lombok.Data;

@Data
public class AppPersonalPracticeAttemptRespVO {
    private Long attemptId;
    private Integer progressStatus;
    private Integer attemptCount;
    private Integer bestScore;
    private Integer lastScore;
}
