package cn.kugua.module.english.dal.dataobject.quota;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 英语课每日用量归档（每日凌晨由定时任务从 Redis 快照）。
 * 不 extends BaseDO：没有逻辑删除 / 多租户 / updater 概念。
 */
@TableName("esc_quota_usage_daily")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscQuotaUsageDailyDO {

    @TableId
    private Long id;

    /** 会员 ID */
    private Long userId;
    /** 统计日（昨天） */
    private LocalDate statDate;

    /** 当日 LLM 调用次数 */
    private Integer llmUsed;
    /** 当日 ASR 音频秒数 */
    private Integer asrUsedSec;
    /** 当日 TTS 合成字符数 */
    private Integer ttsUsedChars;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
