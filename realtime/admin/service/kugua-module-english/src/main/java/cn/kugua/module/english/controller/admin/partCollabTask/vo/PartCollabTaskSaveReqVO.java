package cn.kugua.module.english.controller.admin.partCollabTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 协作任务 - 主体新增/修改 Request VO")
@Data
public class PartCollabTaskSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "8911")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13592")
    @NotNull(message = "esc_exam_part.id不能为空")
    private Long partId;

    @Schema(description = "情境描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "情境描述不能为空")
    private String scenario;

    @Schema(description = "辅助图片 URL", example = "https://www.iocoder.cn")
    private String imageUrl;

    @Schema(description = "任务指令")
    private String instruction;

}