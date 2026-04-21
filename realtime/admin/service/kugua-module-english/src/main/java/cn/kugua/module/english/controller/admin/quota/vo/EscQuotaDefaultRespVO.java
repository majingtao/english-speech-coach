package cn.kugua.module.english.controller.admin.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 全局默认配额 Response VO")
@Data
public class EscQuotaDefaultRespVO {

    @Schema(description = "LLM 每日调用次数")
    private Integer llmDaily;

    @Schema(description = "ASR 每日音频秒数")
    private Integer asrDailySec;

    @Schema(description = "TTS 每日合成字符数")
    private Integer ttsDailyChars;

}
