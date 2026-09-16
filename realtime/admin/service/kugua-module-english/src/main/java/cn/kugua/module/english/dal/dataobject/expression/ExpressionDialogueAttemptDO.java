package cn.kugua.module.english.dal.dataobject.expression;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_expression_dialogue_attempt")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionDialogueAttemptDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long taskId;
    private String selectedRole;
    private String transcriptJson;
    private Integer score;
    private String feedbackJson;
    private Integer durationSeconds;
}
