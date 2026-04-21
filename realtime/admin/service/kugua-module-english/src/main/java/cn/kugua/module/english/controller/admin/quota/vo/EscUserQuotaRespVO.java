package cn.kugua.module.english.controller.admin.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 单用户配额 Response VO")
@Data
public class EscUserQuotaRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "会员 ID")
    private Long userId;

    @Schema(description = "会员昵称（关联查询）")
    private String nickname;

    @Schema(description = "LLM 每日次数；null = 使用默认")
    private Integer llmDaily;

    @Schema(description = "ASR 每日秒数；null = 使用默认")
    private Integer asrDailySec;

    @Schema(description = "TTS 每日字符数；null = 使用默认")
    private Integer ttsDailyChars;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
