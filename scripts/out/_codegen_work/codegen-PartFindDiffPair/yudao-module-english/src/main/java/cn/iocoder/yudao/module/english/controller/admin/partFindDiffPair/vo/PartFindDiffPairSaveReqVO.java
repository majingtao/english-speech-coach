package cn.iocoder.yudao.module.english.controller.admin.partFindDiffPair.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 找不同 - 图对新增/修改 Request VO")
@Data
public class PartFindDiffPairSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7516")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "16")
    @NotNull(message = "esc_exam_part.id不能为空")
    private Long partId;

    @Schema(description = "图对序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "图对序号不能为空")
    private Integer pairNo;

    @Schema(description = "左图 URL", example = "https://www.iocoder.cn")
    private String imageAUrl;

    @Schema(description = "右图 URL", example = "https://www.iocoder.cn")
    private String imageBUrl;

    @Schema(description = "主题，如 Two pictures of a kitchen")
    private String topic;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}