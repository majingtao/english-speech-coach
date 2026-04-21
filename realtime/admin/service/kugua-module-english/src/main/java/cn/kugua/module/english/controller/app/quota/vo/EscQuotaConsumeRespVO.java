package cn.kugua.module.english.controller.app.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "H5/Server - 配额扣减结果 VO")
@Data
@Builder
public class EscQuotaConsumeRespVO {

    @Schema(description = "本次扣减是否允许")
    private Boolean allowed;

    @Schema(description = "扣减后当日剩余额度")
    private Integer remaining;

    @Schema(description = "当日上限（便于前端展示）")
    private Integer dailyLimit;

}
