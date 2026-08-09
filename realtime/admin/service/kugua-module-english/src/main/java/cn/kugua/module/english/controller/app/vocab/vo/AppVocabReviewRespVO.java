package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "H5 - SRS 复习提交响应")
@Data
public class AppVocabReviewRespVO {

    @Schema(description = "新状态 0=新 1=学习中 2=已掌握")
    private Integer status;

    @Schema(description = "连续答对次数")
    private Integer repetitions;

    @Schema(description = "当前间隔天数")
    private Integer intervalDays;

    @Schema(description = "下次复习时间 ISO 字符串")
    private String nextReviewAt;

    @Schema(description = "累计答对")
    private Integer correctCount;

    @Schema(description = "累计答错")
    private Integer wrongCount;

}
