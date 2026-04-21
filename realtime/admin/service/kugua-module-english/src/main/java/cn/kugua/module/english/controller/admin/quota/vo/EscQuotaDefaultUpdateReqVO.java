package cn.kugua.module.english.controller.admin.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 全局默认配额 Update Request VO")
@Data
public class EscQuotaDefaultUpdateReqVO {

    @Schema(description = "LLM 每日调用次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "llmDaily 不能为空")
    @Min(value = 0, message = "llmDaily 不能为负")
    private Integer llmDaily;

    @Schema(description = "ASR 每日音频秒数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "asrDailySec 不能为空")
    @Min(value = 0, message = "asrDailySec 不能为负")
    private Integer asrDailySec;

    @Schema(description = "TTS 每日合成字符数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ttsDailyChars 不能为空")
    @Min(value = 0, message = "ttsDailyChars 不能为负")
    private Integer ttsDailyChars;

}
