package cn.kugua.module.english.dal.dataobject.quota;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 英语课单用户配额覆盖。
 * llmDaily / asrDailySec / ttsDailyChars 为 null 时表示该项走默认值。
 */
@TableName("esc_user_quota")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscUserQuotaDO extends BaseDO {

    @TableId
    private Long id;

    /** member_user.id */
    private Long userId;

    /** null = 该资源吃默认值 */
    private Integer llmDaily;
    private Integer asrDailySec;
    private Integer ttsDailyChars;

    /** true=启用；false=冻结，所有调用立刻拒绝 */
    private Boolean enabled;

    private String remark;

}
