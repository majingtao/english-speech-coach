package cn.kugua.module.english.controller.app.writing.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppKetWritingAttemptReqVO {

    @NotEmpty(message = "题目 ID 不能为空")
    private String taskId;
    private String levelCode;
    @NotNull(message = "写作部分不能为空")
    private Integer part;
    @NotEmpty(message = "练习模式不能为空")
    private String practiceMode;
    private String promptSnapshotJson;
    private String learnerInfoJson;
    private String aiDraftText;
    @NotEmpty(message = "作文内容不能为空")
    private String responseText;
    private Integer wordCount;
    private String feedbackJson;
    private String scoreLabel;
}
