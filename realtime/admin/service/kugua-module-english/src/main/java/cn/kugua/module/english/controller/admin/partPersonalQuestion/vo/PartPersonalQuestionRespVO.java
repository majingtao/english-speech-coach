package cn.kugua.module.english.controller.admin.partPersonalQuestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 个人问答 - 问题 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartPersonalQuestionRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5364")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "22686")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "题号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("题号")
    private Integer qNo;

    @Schema(description = "问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("问题")
    private String questionText;

    @Schema(description = "主题分组")
    @ExcelProperty("主题分组")
    private String topic;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
