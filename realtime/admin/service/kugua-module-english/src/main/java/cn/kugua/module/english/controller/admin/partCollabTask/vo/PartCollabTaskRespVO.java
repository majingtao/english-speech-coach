package cn.kugua.module.english.controller.admin.partCollabTask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 协作任务 - 主体 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartCollabTaskRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "8911")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13592")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "情境描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("情境描述")
    private String scenario;

    @Schema(description = "辅助图片 URL", example = "https://www.iocoder.cn")
    @ExcelProperty("辅助图片 URL")
    private String imageUrl;

    @Schema(description = "任务指令")
    @ExcelProperty("任务指令")
    private String instruction;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
