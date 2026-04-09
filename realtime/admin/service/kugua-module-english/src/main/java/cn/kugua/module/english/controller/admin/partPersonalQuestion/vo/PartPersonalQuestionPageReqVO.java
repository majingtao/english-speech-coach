package cn.kugua.module.english.controller.admin.partPersonalQuestion.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 个人问答 - 问题分页 Request VO")
@Data
public class PartPersonalQuestionPageReqVO extends PageParam {

    @Schema(description = "esc_exam_part.id", example = "22686")
    private Long partId;

    @Schema(description = "题号")
    private Integer qNo;

    @Schema(description = "问题")
    private String questionText;

    @Schema(description = "主题分组")
    private String topic;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}