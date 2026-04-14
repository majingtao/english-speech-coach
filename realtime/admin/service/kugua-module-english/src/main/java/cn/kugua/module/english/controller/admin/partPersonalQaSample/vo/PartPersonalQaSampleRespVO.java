package cn.kugua.module.english.controller.admin.partPersonalQaSample.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 个人问答 - 示例答案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartPersonalQaSampleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_part_personal_question.id", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("问题ID")
    private Long questionId;

    @Schema(description = "示例序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("示例序号")
    private Integer sampleNo;

    @Schema(description = "示例答案")
    @ExcelProperty("示例答案")
    private String sampleText;

    @Schema(description = "水平标签")
    @ExcelProperty("水平标签")
    private String levelTag;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
