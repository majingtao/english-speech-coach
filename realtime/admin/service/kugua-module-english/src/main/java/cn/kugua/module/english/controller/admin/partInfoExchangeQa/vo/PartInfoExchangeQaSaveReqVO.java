package cn.kugua.module.english.controller.admin.partInfoExchangeQa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 信息互换 - 问答条目新增/修改 Request VO")
@Data
public class PartInfoExchangeQaSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "esc_part_info_exchange_card.id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "卡片ID不能为空")
    private Long cardId;

    @Schema(description = "问答序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "问答序号不能为空")
    private Integer qaNo;

    @Schema(description = "提示项")
    private String promptLabel;

    @Schema(description = "完整问句")
    private String questionText;

    @Schema(description = "参考答案")
    private String answerText;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
