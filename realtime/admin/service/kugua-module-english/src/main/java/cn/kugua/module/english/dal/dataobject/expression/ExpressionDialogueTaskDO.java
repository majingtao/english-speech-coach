package cn.kugua.module.english.dal.dataobject.expression;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_expression_dialogue_task")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionDialogueTaskDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String levelCode;
    private String titleCn;
    private String titleEn;
    private String topicCode;
    private String promptEn;
    private String promptCn;
    private String configJson;
    private Integer sort;
    private Integer status;
}
