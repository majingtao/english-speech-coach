package cn.iocoder.yudao.module.english.controller.admin.partTellStoryFrame.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 讲故事 - 单帧新增/修改 Request VO")
@Data
public class PartTellStoryFrameSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "17938")
    private Long id;

    @Schema(description = "esc_exam_part.id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13064")
    @NotNull(message = "esc_exam_part.id不能为空")
    private Long partId;

    @Schema(description = "帧序号 1~5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "帧序号 1~5不能为空")
    private Integer frameNo;

    @Schema(description = "该帧图片 URL", example = "https://www.iocoder.cn")
    private String imageUrl;

    @Schema(description = "该帧参考句子")
    private String sentenceText;

    @Schema(description = "提示词 / 关键词")
    private String hint;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}