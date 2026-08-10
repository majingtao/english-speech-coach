package cn.kugua.module.english.controller.app.writing.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppKetWritingAttemptRespVO {

    private Long attemptId;
    private String taskId;
    private String levelCode;
    private Integer part;
    private String practiceMode;
    private String promptSnapshotJson;
    private String learnerInfoJson;
    private String aiDraftText;
    private String responseText;
    private Integer wordCount;
    private String feedbackJson;
    private String scoreLabel;
    private LocalDateTime createTime;
    private Integer attemptCount;
}
