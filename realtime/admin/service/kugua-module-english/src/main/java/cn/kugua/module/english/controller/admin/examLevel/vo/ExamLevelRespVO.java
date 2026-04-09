package cn.kugua.module.english.controller.admin.examLevel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 考试级别字典 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExamLevelRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "22681")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "级别编码：flyers / ket / pet", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("级别编码：flyers / ket / pet")
    private String code;

    @Schema(description = "显示名：Cambridge Flyers / KET / PET", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("显示名：Cambridge Flyers / KET / PET")
    private String name;

    @Schema(description = "CEFR 等级：A2 / B1 ...")
    @ExcelProperty("CEFR 等级：A2 / B1 ...")
    private String cefr;

    @Schema(description = "说明", example = "随便")
    @ExcelProperty("说明")
    private String description;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("0=启用 1=停用")
    private Integer status;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
