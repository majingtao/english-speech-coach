package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "H5 - 用户词库 Save Request VO")
@Data
public class AppUserVocabListSaveReqVO {

    @Schema(description = "词库ID（更新时必填）")
    private Long id;

    @Schema(description = "词库名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name 必填")
    @Size(max = 64)
    private String name;

    @Schema(description = "描述")
    @Size(max = 255)
    private String description;

}
