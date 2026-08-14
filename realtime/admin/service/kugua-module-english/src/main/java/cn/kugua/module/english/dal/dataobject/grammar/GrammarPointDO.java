package cn.kugua.module.english.dal.dataobject.grammar;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("esc_grammar_point")
@Data
@EqualsAndHashCode(callSuper = true)
public class GrammarPointDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String parentCode;
    private String nameCn;
    private String nameEn;
    private String description;
    private String levelCode;
    private String difficultyConfigJson;
    private Integer sort;
    private Integer status;
}
