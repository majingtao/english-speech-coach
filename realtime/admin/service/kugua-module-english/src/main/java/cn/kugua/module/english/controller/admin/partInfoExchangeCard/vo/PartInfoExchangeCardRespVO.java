package cn.kugua.module.english.controller.admin.partInfoExchangeCard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 信息互换 - 卡片 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartInfoExchangeCardRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7615")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21763")
    @ExcelProperty("esc_exam_part.id")
    private Long partId;

    @Schema(description = "阶段：A=学生提问 B=学生回答", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("阶段：A=学生提问 B=学生回答")
    private String phase;

    @Schema(description = "卡片标题，如 Football Match")
    @ExcelProperty("卡片标题，如 Football Match")
    private String cardTitle;

    @Schema(description = "卡片图片 URL", example = "https://www.iocoder.cn")
    @ExcelProperty("卡片图片 URL")
    private String cardImageUrl;

    @Schema(description = "卡片说明")
    @ExcelProperty("卡片说明")
    private String intro;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
