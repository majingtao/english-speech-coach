package cn.kugua.module.english.controller.admin.partFindDiffDifference.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 找不同 - 差异点分页 Request VO")
@Data
public class PartFindDiffDifferencePageReqVO extends PageParam {

    @Schema(description = "esc_part_find_diff_pair.id")
    private Long pairId;

    @Schema(description = "差异点序号")
    private Integer diffNo;

    @Schema(description = "差异描述")
    private String description;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
