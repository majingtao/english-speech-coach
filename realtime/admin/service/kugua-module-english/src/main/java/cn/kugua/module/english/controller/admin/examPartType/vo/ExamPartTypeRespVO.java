package cn.kugua.module.english.controller.admin.examPartType.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 题型字典 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExamPartTypeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13109")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "题型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("题型编码")
    private String code;

    @Schema(description = "显示名", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("显示名")
    private String name;

    @Schema(description = "说明", example = "你说的对")
    @ExcelProperty("说明")
    private String description;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("0=启用 1=停用")
    private Integer status;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
