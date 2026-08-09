package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "H5 - 生词本加词 Request VO")
@Data
public class AppWordbookAddReqVO {

    @Schema(description = "词条 ID（必须是词库中已发布的词）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "vocabId 不能为空")
    private Long vocabId;

}
