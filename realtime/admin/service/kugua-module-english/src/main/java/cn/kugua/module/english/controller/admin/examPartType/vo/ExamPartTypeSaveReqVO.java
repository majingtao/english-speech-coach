package cn.kugua.module.english.controller.admin.examPartType.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 题型字典新增/修改 Request VO")
@Data
public class ExamPartTypeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13109")
    private Long id;

    @Schema(description = "题型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "题型编码不能为空")
    private String code;

    @Schema(description = "显示名", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "显示名不能为空")
    private String name;

    @Schema(description = "说明", example = "你说的对")
    private String description;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "0=启用 1=停用不能为空")
    private Integer status;

}