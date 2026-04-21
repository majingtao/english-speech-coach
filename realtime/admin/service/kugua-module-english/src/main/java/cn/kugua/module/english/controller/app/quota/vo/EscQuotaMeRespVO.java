package cn.kugua.module.english.controller.app.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "H5 - 我的配额 Response VO")
@Data
@Builder
public class EscQuotaMeRespVO {

    @Schema(description = "是否启用，false 表示账号被冻结")
    private Boolean enabled;

    // ---- 每日上限（用户覆盖 > 默认合并后）----
    private Integer llmDaily;
    private Integer asrDailySec;
    private Integer ttsDailyChars;

    // ---- 今日已用 ----
    private Integer llmUsed;
    private Integer asrUsedSec;
    private Integer ttsUsedChars;

    // ---- 今日剩余 = daily - used，且 ≥ 0 ----
    private Integer llmRemaining;
    private Integer asrRemainingSec;
    private Integer ttsRemainingChars;

}
