package cn.kugua.module.english.controller.admin.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 每日用量归档 Response VO")
@Data
public class EscQuotaUsageRespVO {

    private Long id;
    private Long userId;
    private String nickname;
    private LocalDate statDate;

    private Integer llmUsed;
    private Integer asrUsedSec;
    private Integer ttsUsedChars;

    private LocalDateTime createTime;

}
