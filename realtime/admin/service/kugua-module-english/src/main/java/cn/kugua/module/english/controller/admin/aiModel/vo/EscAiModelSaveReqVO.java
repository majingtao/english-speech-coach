package cn.kugua.module.english.controller.admin.aiModel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI模型配置新增/修改 Request VO")
@Data
public class EscAiModelSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "类型：llm / asr / tts", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "类型不能为空")
    private String type;

    @Schema(description = "提供商")
    private String provider;

    @Schema(description = "模型标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模型标识不能为空")
    private String modelKey;

    @Schema(description = "显示名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "显示名不能为空")
    private String label;

    @Schema(description = "扩展配置JSON")
    private String configJson;

    @Schema(description = "是否默认选中")
    @NotNull(message = "是否默认不能为空")
    private Integer isDefault;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "0=启用 1=停用")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
