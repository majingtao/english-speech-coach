package cn.kugua.module.english.controller.admin.partGeneralConvoTopic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 一般对话 - 主题新增/修改 Request VO")
@Data
public class PartGeneralConvoTopicSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "236")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18869")
    @NotNull(message = "esc_exam_part.id不能为空")
    private Long partId;

    @Schema(description = "对话主题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "对话主题不能为空")
    private String topic;

    @Schema(description = "主题引导")
    private String intro;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}