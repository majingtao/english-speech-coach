package cn.kugua.module.english.dal.dataobject.synonym;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("esc_synonym_point")
@Data
@EqualsAndHashCode(callSuper = true)
public class SynonymPointDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String mode;
    private String levelCode;
    private String source;
    private String sectionName;
    private String leftText;
    private String leftCn;
    private String rightText;
    private String rightCn;
    private Integer sort;
    private Integer status;
}
