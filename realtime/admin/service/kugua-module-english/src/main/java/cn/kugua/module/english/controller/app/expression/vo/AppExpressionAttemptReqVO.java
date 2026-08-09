package cn.kugua.module.english.controller.app.expression.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AppExpressionAttemptReqVO {
    @NotEmpty(message = "练习模式不能为空")
    private String practiceMode;
    @NotEmpty(message = "回答不能为空")
    private String responseText;
    @Min(value = 0, message = "分数不能小于 0")
    @Max(value = 100, message = "分数不能大于 100")
    private Integer score;
    private String feedbackJson;
    private Integer durationSeconds;
}
