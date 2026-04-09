package cn.kugua.module.english.controller.admin.examPart.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 试卷 Part 多态头分页 Request VO")
@Data
public class ExamPartPageReqVO extends PageParam {

    @Schema(description = "所属试卷 esc_exam.id", example = "11875")
    private Long examId;

    @Schema(description = "题段序号 1/2/3/4")
    private Integer partNo;

    @Schema(description = "题型编码，引用 esc_exam_part_type.code", example = "1")
    private String partType;

    @Schema(description = "Part 显示标题")
    private String title;

    @Schema(description = "Part 引导语 / 考官口述")
    private String intro;

    @Schema(description = "时间限制（秒），可为空")
    private Integer timeLimitSec;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}