package cn.kugua.module.english.controller.admin.partGeneralConvoTopic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 一般对话 - 主题 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartGeneralConvoTopicRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "236")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18869")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "对话主题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("对话主题")
    private String topic;

    @Schema(description = "主题引导")
    @ExcelProperty("主题引导")
    private String intro;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
