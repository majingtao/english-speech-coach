package cn.kugua.module.english.controller.admin.partTellStoryFrame.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 讲故事 - 单帧 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartTellStoryFrameRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "17938")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13064")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "帧序号 1~5", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("帧序号 1~5")
    private Integer frameNo;

    @Schema(description = "该帧图片 URL", example = "https://www.iocoder.cn")
    @ExcelProperty("该帧图片 URL")
    private String imageUrl;

    @Schema(description = "该帧参考句子")
    @ExcelProperty("该帧参考句子")
    private String sentenceText;

    @Schema(description = "提示词 / 关键词")
    @ExcelProperty("提示词 / 关键词")
    private String hint;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
