package cn.kugua.module.english.controller.app.expression.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppExpressionDialogueAttemptReqVO {
    @NotEmpty(message = "练习角色不能为空")
    private String selectedRole;
    @NotEmpty(message = "对话记录不能为空")
    private String transcriptJson;
    @NotNull(message = "分数不能为空")
    @Min(value = 0, message = "分数不能小于 0")
    @Max(value = 100, message = "分数不能大于 100")
    private Integer score;
    private String feedbackJson;
    private Integer durationSeconds;
}
