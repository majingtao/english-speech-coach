package cn.kugua.module.english.controller.admin.partLongTurnPhoto.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 独立长答 - 图片描述分页 Request VO")
@Data
public class PartLongTurnPhotoPageReqVO extends PageParam {

    @Schema(description = "esc_exam_part.id", example = "748")
    private Long partId;

    @Schema(description = "面向的考生 A / B")
    private String candidateRole;

    @Schema(description = "主题")
    private String topic;

    @Schema(description = "图片 URL", example = "https://www.iocoder.cn")
    private String imageUrl;

    @Schema(description = "考官指令")
    private String instruction;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}