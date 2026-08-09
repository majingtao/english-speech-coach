package cn.kugua.module.english.dal.dataobject.expression;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_expression_theme")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionThemeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String nameCn;
    private String nameEn;
    private String description;
    private String levelCode;
    private String coverUrl;
    private Integer sort;
    private Integer status;
}
