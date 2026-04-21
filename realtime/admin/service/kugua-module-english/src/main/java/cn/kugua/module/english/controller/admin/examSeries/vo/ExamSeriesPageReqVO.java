package cn.kugua.module.english.controller.admin.examSeries.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 考试系列字典分页 Request VO")
@Data
public class ExamSeriesPageReqVO extends PageParam {

    @Schema(description = "系列编码")
    private String code;

    @Schema(description = "所属级别编码")
    private String levelCode;

    @Schema(description = "显示名")
    private String name;

    @Schema(description = "出版方")
    private String publisher;

    @Schema(description = "0=启用 1=停用")
    private Integer status;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
