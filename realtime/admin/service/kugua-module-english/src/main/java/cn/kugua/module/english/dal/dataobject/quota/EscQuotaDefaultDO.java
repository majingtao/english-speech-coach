package cn.kugua.module.english.dal.dataobject.quota;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 英语课全局默认配额（单行，id 固定为 1）
 */
@TableName("esc_quota_default")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscQuotaDefaultDO extends BaseDO {

    public static final Long SINGLETON_ID = 1L;

    @TableId
    private Long id;

    /** LLM 每日调用次数 */
    private Integer llmDaily;
    /** ASR 每日音频秒数 */
    private Integer asrDailySec;
    /** TTS 每日合成字符数 */
    private Integer ttsDailyChars;

}
