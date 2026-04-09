package cn.kugua.module.english.controller.admin.partLongTurnPhoto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 独立长答 - 图片描述新增/修改 Request VO")
@Data
public class PartLongTurnPhotoSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31576")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "748")
    @NotNull(message = "esc_exam_part.id不能为空")
    private Long partId;

    @Schema(description = "面向的考生 A / B", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "面向的考生 A / B不能为空")
    private String candidateRole;

    @Schema(description = "主题")
    private String topic;

    @Schema(description = "图片 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @NotEmpty(message = "图片 URL不能为空")
    private String imageUrl;

    @Schema(description = "考官指令")
    private String instruction;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}