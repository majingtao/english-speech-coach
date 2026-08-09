package cn.kugua.module.english.dal.dataobject.expression;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_expression_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionItemDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long themeId;
    private String code;
    private String promptEn;
    private String promptCn;
    private String functionCode;
    private String practiceMode;
    private Integer difficulty;
    private String answerJson;
    private String imageUrlsJson;
    private Integer sort;
    private Integer status;
}
