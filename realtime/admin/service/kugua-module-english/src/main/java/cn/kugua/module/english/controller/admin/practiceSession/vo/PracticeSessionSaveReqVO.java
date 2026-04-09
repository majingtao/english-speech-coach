package cn.kugua.module.english.controller.admin.practiceSession.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 练习会话新增/修改 Request VO")
@Data
public class PracticeSessionSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19108")
    private Long id;

    @Schema(description = "esc_student.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13728")
    @NotNull(message = "esc_student.id不能为空")
    private Long studentId;

    @Schema(description = "esc_exam.id（具体版本）", requiredMode = Schema.RequiredMode.REQUIRED, example = "22965")
    @NotNull(message = "esc_exam.id（具体版本）不能为空")
    private Long examId;

    @Schema(description = "冗余 exam_code 便于跨版本统计", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "冗余 exam_code 便于跨版本统计不能为空")
    private String examCode;

    @Schema(description = "模式：exam / free_talk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模式：exam / free_talk不能为空")
    private String mode;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "总时长（秒）")
    private Integer durationSec;

    @Schema(description = "0=进行中 1=已完成 2=已放弃", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "0=进行中 1=已完成 2=已放弃不能为空")
    private Integer status;

    @Schema(description = "整卷综合分 0-100")
    private Integer finalOverallScore;

    @Schema(description = "整卷综合评语")
    private String finalComment;

}