package cn.kugua.module.english.dal.dataobject.vocab;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 词汇-主题字典 DO（esc_vocab_theme）
 */
@TableName("esc_vocab_theme")
@Data
@EqualsAndHashCode(callSuper = true)
public class VocabThemeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 主题编码 sports/food/... */
    private String code;

    private String nameCn;

    private String nameEn;

    /** 所属级别（可空 = 通用） */
    private String levelCode;

    private Integer sort;

    /** 0=禁用 1=启用 */
    private Integer status;

}
