package cn.kugua.module.english.controller.admin.partPersonalQaSample.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 个人问答 - 示例答案新增/修改 Request VO")
@Data
public class PartPersonalQaSampleSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "esc_part_personal_question.id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "问题ID不能为空")
    private Long questionId;

    @Schema(description = "示例序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "示例序号不能为空")
    private Integer sampleNo;

    @Schema(description = "示例答案")
    private String sampleText;

    @Schema(description = "水平标签")
    private String levelTag;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
