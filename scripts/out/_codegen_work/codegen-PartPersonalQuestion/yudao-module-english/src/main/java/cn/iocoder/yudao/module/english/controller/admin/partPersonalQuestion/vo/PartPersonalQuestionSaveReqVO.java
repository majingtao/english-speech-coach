package cn.iocoder.yudao.module.english.controller.admin.partPersonalQuestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 个人问答 - 问题新增/修改 Request VO")
@Data
public class PartPersonalQuestionSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5364")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "22686")
    @NotNull(message = "esc_exam_part.id不能为空")
    private Long partId;

    @Schema(description = "题号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "题号不能为空")
    private Integer qNo;

    @Schema(description = "问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "问题不能为空")
    private String questionText;

    @Schema(description = "主题分组")
    private String topic;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}