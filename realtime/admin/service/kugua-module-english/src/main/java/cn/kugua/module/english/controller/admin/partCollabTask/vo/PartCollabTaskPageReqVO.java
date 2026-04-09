package cn.kugua.module.english.controller.admin.partCollabTask.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 协作任务 - 主体分页 Request VO")
@Data
public class PartCollabTaskPageReqVO extends PageParam {

    @Schema(description = "esc_exam_part.id", example = "13592")
    private Long partId;

    @Schema(description = "情境描述")
    private String scenario;

    @Schema(description = "辅助图片 URL", example = "https://www.iocoder.cn")
    private String imageUrl;

    @Schema(description = "任务指令")
    private String instruction;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}