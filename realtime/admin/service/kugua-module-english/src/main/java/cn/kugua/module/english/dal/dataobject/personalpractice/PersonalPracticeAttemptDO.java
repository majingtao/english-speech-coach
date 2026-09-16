package cn.kugua.module.english.dal.dataobject.personalpractice;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_personal_practice_attempt")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalPracticeAttemptDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long practiceId;
    private String responseText;
    private Integer grammarScore;
    private Integer contentScore;
    private Integer totalScore;
    private String feedbackJson;
    private Integer durationSeconds;
}
