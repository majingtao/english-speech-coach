package cn.kugua.module.english.controller.admin.partGeneralConvoTopic.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 一般对话 - 主题分页 Request VO")
@Data
public class PartGeneralConvoTopicPageReqVO extends PageParam {

    @Schema(description = "esc_exam_part.id", example = "18869")
    private Long partId;

    @Schema(description = "对话主题")
    private String topic;

    @Schema(description = "主题引导")
    private String intro;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}