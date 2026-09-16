package cn.kugua.module.english.controller.app.personalpractice.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppPersonalPracticeAttemptReqVO {
    @NotEmpty(message = "回答不能为空")
    private String responseText;
    @NotNull
    @Min(0)
    @Max(60)
    private Integer grammarScore;
    @NotNull
    @Min(0)
    @Max(40)
    private Integer contentScore;
    @NotNull
    @Min(0)
    @Max(100)
    private Integer totalScore;
    private String feedbackJson;
    private Integer durationSeconds;
}
