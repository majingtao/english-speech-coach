package cn.kugua.module.english.controller.admin.partFindDiffPair.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 找不同 - 图对 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartFindDiffPairRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7516")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "16")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "图对序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("图对序号")
    private Integer pairNo;

    @Schema(description = "左图 URL", example = "https://www.iocoder.cn")
    @ExcelProperty("左图 URL")
    private String imageAUrl;

    @Schema(description = "右图 URL", example = "https://www.iocoder.cn")
    @ExcelProperty("右图 URL")
    private String imageBUrl;

    @Schema(description = "主题，如 Two pictures of a kitchen")
    @ExcelProperty("主题，如 Two pictures of a kitchen")
    private String topic;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
