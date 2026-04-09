package cn.kugua.module.english.controller.admin.practiceSession.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 练习会话分页 Request VO")
@Data
public class PracticeSessionPageReqVO extends PageParam {

    @Schema(description = "esc_student.id", example = "13728")
    private Long studentId;

    @Schema(description = "esc_exam.id（具体版本）", example = "22965")
    private Long examId;

    @Schema(description = "冗余 exam_code 便于跨版本统计")
    private String examCode;

    @Schema(description = "模式：exam / free_talk")
    private String mode;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "总时长（秒）")
    private Integer durationSec;

    @Schema(description = "0=进行中 1=已完成 2=已放弃", example = "2")
    private Integer status;

    @Schema(description = "整卷综合分 0-100")
    private Integer finalOverallScore;

    @Schema(description = "整卷综合评语")
    private String finalComment;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}