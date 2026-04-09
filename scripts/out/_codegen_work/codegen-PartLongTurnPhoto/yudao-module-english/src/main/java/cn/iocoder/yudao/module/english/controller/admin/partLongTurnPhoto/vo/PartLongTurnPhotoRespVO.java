package cn.iocoder.yudao.module.english.controller.admin.partLongTurnPhoto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 独立长答 - 图片描述 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartLongTurnPhotoRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31576")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "748")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "面向的考生 A / B", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("面向的考生 A / B")
    private String candidateRole;

    @Schema(description = "主题")
    @ExcelProperty("主题")
    private String topic;

    @Schema(description = "图片 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @ExcelProperty("图片 URL")
    private String imageUrl;

    @Schema(description = "考官指令")
    @ExcelProperty("考官指令")
    private String instruction;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}