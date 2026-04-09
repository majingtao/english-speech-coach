package cn.kugua.module.english.controller.admin.partInfoExchangeCard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 信息互换 - 卡片新增/修改 Request VO")
@Data
public class PartInfoExchangeCardSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7615")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21763")
    @NotNull(message = "esc_exam_part.id不能为空")
    private Long partId;

    @Schema(description = "阶段：A=学生提问 B=学生回答", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "阶段：A=学生提问 B=学生回答不能为空")
    private String phase;

    @Schema(description = "卡片标题，如 Football Match")
    private String cardTitle;

    @Schema(description = "卡片图片 URL", example = "https://www.iocoder.cn")
    private String cardImageUrl;

    @Schema(description = "卡片说明")
    private String intro;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}