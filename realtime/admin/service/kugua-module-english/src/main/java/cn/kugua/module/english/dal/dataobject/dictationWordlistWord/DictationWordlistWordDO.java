package cn.kugua.module.english.dal.dataobject.dictationWordlistWord;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 听写-词书单词关联 DO（esc_dictation_wordlist_word）
 */
@TableName("esc_dictation_wordlist_word")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictationWordlistWordDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 词书ID */
    private Long wordlistId;

    /** 单词ID */
    private Long wordId;

    /** 排序 */
    private Integer seq;

}
