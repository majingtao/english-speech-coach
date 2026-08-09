package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "H5 - 提交 SRS 复习结果 Request VO")
@Data
public class AppVocabReviewReqVO {

    @Schema(description = "是否记住", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "remembered 必填")
    private Boolean remembered;

}
