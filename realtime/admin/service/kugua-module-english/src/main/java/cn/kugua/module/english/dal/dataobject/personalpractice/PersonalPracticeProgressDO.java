package cn.kugua.module.english.dal.dataobject.personalpractice;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@TableName("esc_personal_practice_progress")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalPracticeProgressDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long practiceId;
    private Integer status;
    private Integer attemptCount;
    private Integer bestScore;
    private Integer lastScore;
    private LocalDateTime lastPracticeAt;
}
