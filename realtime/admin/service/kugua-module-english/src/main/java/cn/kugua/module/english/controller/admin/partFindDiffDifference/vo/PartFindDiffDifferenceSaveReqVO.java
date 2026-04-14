package cn.kugua.module.english.controller.admin.partFindDiffDifference.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 找不同 - 差异点新增/修改 Request VO")
@Data
public class PartFindDiffDifferenceSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "esc_part_find_diff_pair.id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "图对ID不能为空")
    private Long pairId;

    @Schema(description = "差异点序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "差异点序号不能为空")
    private Integer diffNo;

    @Schema(description = "差异描述（英文）")
    private String description;

    @Schema(description = "关键词，便于评分匹配")
    private String keyword;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
