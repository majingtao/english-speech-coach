package cn.kugua.module.english.controller.admin.practiceSession.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 练习会话 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PracticeSessionRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19108")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_student.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13728")
    @ExcelProperty("esc_student.id")
    private Long studentId;

    @Schema(description = "esc_exam.id（具体版本）", requiredMode = Schema.RequiredMode.REQUIRED, example = "22965")
    @ExcelProperty("esc_exam.id（具体版本）")
    private Long examId;

    @Schema(description = "冗余 exam_code 便于跨版本统计", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("冗余 exam_code 便于跨版本统计")
    private String examCode;

    @Schema(description = "模式：exam / free_talk", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("模式：exam / free_talk")
    private String mode;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @Schema(description = "总时长（秒）")
    @ExcelProperty("总时长（秒）")
    private Integer durationSec;

    @Schema(description = "0=进行中 1=已完成 2=已放弃", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("0=进行中 1=已完成 2=已放弃")
    private Integer status;

    @Schema(description = "整卷综合分 0-100")
    @ExcelProperty("整卷综合分 0-100")
    private Integer finalOverallScore;

    @Schema(description = "整卷综合评语")
    @ExcelProperty("整卷综合评语")
    private String finalComment;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
