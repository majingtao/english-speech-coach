package cn.kugua.module.english.controller.admin.examSeries.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 考试系列字典 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExamSeriesRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "22681")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "系列编码：go_flyers / flyers_1 / aep_1", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("系列编码")
    private String code;

    @Schema(description = "所属级别编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("所属级别")
    private String levelCode;

    @Schema(description = "显示名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("显示名")
    private String name;

    @Schema(description = "封面图 URL")
    @ExcelProperty("封面图")
    private String coverUrl;

    @Schema(description = "出版方")
    @ExcelProperty("出版方")
    private String publisher;

    @Schema(description = "说明")
    @ExcelProperty("说明")
    private String description;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
