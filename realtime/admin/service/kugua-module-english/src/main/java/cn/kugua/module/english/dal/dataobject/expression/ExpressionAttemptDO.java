package cn.kugua.module.english.dal.dataobject.expression;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_expression_attempt")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionAttemptDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long expressionItemId;
    private String practiceMode;
    private String responseText;
    private Integer score;
    private String feedbackJson;
    private Integer durationSeconds;
}
