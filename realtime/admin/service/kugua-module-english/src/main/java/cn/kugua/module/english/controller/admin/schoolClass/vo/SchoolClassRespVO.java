package cn.kugua.module.english.controller.admin.schoolClass.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 班级 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SchoolClassRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13884")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("班级名称")
    private String name;

    @Schema(description = "班级编码（可选）")
    @ExcelProperty("班级编码（可选）")
    private String code;

    @Schema(description = "描述", example = "你猜")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("0=启用 1=停用")
    private Integer status;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
