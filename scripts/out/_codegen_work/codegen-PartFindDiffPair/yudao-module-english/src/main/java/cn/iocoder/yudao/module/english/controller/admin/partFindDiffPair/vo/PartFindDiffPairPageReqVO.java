package cn.iocoder.yudao.module.english.controller.admin.partFindDiffPair.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 找不同 - 图对分页 Request VO")
@Data
public class PartFindDiffPairPageReqVO extends PageParam {

    @Schema(description = "esc_exam_part.id", example = "16")
    private Long partId;

    @Schema(description = "图对序号")
    private Integer pairNo;

    @Schema(description = "左图 URL", example = "https://www.iocoder.cn")
    private String imageAUrl;

    @Schema(description = "右图 URL", example = "https://www.iocoder.cn")
    private String imageBUrl;

    @Schema(description = "主题，如 Two pictures of a kitchen")
    private String topic;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}