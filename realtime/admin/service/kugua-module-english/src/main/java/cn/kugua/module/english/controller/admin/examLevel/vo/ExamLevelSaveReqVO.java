package cn.kugua.module.english.controller.admin.examLevel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 考试级别字典新增/修改 Request VO")
@Data
public class ExamLevelSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "22681")
    private Long id;

    @Schema(description = "级别编码：flyers / ket / pet", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "级别编码：flyers / ket / pet不能为空")
    private String code;

    @Schema(description = "显示名：Cambridge Flyers / KET / PET", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "显示名：Cambridge Flyers / KET / PET不能为空")
    private String name;

    @Schema(description = "CEFR 等级：A2 / B1 ...")
    private String cefr;

    @Schema(description = "说明", example = "随便")
    private String description;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "0=启用 1=停用不能为空")
    private Integer status;

}