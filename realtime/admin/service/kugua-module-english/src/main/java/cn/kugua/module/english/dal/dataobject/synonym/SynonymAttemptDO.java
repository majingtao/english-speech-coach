package cn.kugua.module.english.dal.dataobject.synonym;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("esc_synonym_attempt")
@Data
@EqualsAndHashCode(callSuper = true)
public class SynonymAttemptDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long pointId;
    private String answerText;
    private Boolean correct;
    private Integer durationSeconds;
}
