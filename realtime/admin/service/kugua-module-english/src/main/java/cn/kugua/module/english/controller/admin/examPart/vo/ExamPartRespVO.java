package cn.kugua.module.english.controller.admin.examPart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 试卷 Part 多态头 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExamPartRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26366")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "所属试卷 esc_exam.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "11875")
    @ExcelProperty("所属试卷 esc_exam.id")
    private Long examId;

    @Schema(description = "题段序号 1/2/3/4", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("题段序号 1/2/3/4")
    private Integer partNo;

    @Schema(description = "题型编码，引用 esc_exam_part_type.code", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("题型编码，引用 esc_exam_part_type.code")
    private String partType;

    @Schema(description = "Part 显示标题")
    @ExcelProperty("Part 显示标题")
    private String title;

    @Schema(description = "Part 引导语 / 考官口述")
    @ExcelProperty("Part 引导语 / 考官口述")
    private String intro;

    @Schema(description = "时间限制（秒），可为空")
    @ExcelProperty("时间限制（秒），可为空")
    private Integer timeLimitSec;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
