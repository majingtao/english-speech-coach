package cn.kugua.module.english.dal.dataobject.vocab;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 词汇-词与主题关联 DO（esc_vocab_theme_rel）
 */
@TableName("esc_vocab_theme_rel")
@Data
@EqualsAndHashCode(callSuper = true)
public class VocabThemeRelDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long vocabId;

    private Long themeId;

}
