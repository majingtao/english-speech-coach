package cn.kugua.module.english.controller.admin.examPart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 试卷 Part 多态头新增/修改 Request VO")
@Data
public class ExamPartSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26366")
    private Long id;

    @Schema(description = "所属试卷 esc_exam.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "11875")
    @NotNull(message = "所属试卷 esc_exam.id不能为空")
    private Long examId;

    @Schema(description = "题段序号 1/2/3/4", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "题段序号 1/2/3/4不能为空")
    private Integer partNo;

    @Schema(description = "题型编码，引用 esc_exam_part_type.code", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "题型编码，引用 esc_exam_part_type.code不能为空")
    private String partType;

    @Schema(description = "Part 显示标题")
    private String title;

    @Schema(description = "Part 引导语 / 考官口述")
    private String intro;

    @Schema(description = "时间限制（秒），可为空")
    private Integer timeLimitSec;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}