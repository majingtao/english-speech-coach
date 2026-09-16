package cn.kugua.module.english.dal.dataobject.synonym;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@TableName("esc_user_synonym_progress")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSynonymProgressDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long pointId;
    private Integer seenCount;
    private Integer correctCount;
    private Integer wrongCount;
    private Boolean mastered;
    private LocalDateTime lastAnsweredAt;
}
