package cn.kugua.module.english.controller.admin.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 单用户配额 Save Request VO（按 userId upsert）")
@Data
public class EscUserQuotaSaveReqVO {

    @Schema(description = "会员 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "userId 不能为空")
    private Long userId;

    @Schema(description = "LLM 每日次数；null = 使用默认")
    @Min(value = 0, message = "llmDaily 不能为负")
    private Integer llmDaily;

    @Schema(description = "ASR 每日秒数；null = 使用默认")
    @Min(value = 0, message = "asrDailySec 不能为负")
    private Integer asrDailySec;

    @Schema(description = "TTS 每日字符数；null = 使用默认")
    @Min(value = 0, message = "ttsDailyChars 不能为负")
    private Integer ttsDailyChars;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "enabled 不能为空")
    private Boolean enabled;

    @Schema(description = "备注")
    private String remark;

}
