package cn.kugua.module.english.controller.admin.schoolClass.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 班级新增/修改 Request VO")
@Data
public class SchoolClassSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13884")
    private Long id;

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "班级名称不能为空")
    private String name;

    @Schema(description = "班级编码（可选）")
    private String code;

    @Schema(description = "描述", example = "你猜")
    private String description;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "0=启用 1=停用不能为空")
    private Integer status;

}