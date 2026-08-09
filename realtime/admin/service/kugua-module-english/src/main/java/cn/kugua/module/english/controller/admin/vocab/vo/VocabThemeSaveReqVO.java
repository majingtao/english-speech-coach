package cn.kugua.module.english.controller.admin.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 词汇主题创建/修改 Request VO")
@Data
public class VocabThemeSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "编码不能为空")
    private String code;

    @Schema(description = "中文名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "中文名不能为空")
    private String nameCn;

    @Schema(description = "英文名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "英文名不能为空")
    private String nameEn;

    @Schema(description = "级别")
    private String levelCode;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "0=禁用 1=启用")
    private Integer status;

}
