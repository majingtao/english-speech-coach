package cn.kugua.module.english.controller.admin.schoolClass.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 班级分页 Request VO")
@Data
public class SchoolClassPageReqVO extends PageParam {

    @Schema(description = "班级名称", example = "王五")
    private String name;

    @Schema(description = "班级编码（可选）")
    private String code;

    @Schema(description = "描述", example = "你猜")
    private String description;

    @Schema(description = "0=启用 1=停用", example = "2")
    private Integer status;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}