package cn.kugua.module.english.controller.admin.partFindDiffDifference.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 找不同 - 差异点 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartFindDiffDifferenceRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_part_find_diff_pair.id", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("图对ID")
    private Long pairId;

    @Schema(description = "差异点序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("差异点序号")
    private Integer diffNo;

    @Schema(description = "差异描述（英文）")
    @ExcelProperty("差异描述")
    private String description;

    @Schema(description = "关键词，便于评分匹配")
    @ExcelProperty("关键词")
    private String keyword;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
