package cn.kugua.module.english.dal.dataobject.vocab;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 词汇-用户自建词库 DO（esc_user_vocab_list）
 */
@TableName("esc_user_vocab_list")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserVocabListDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String description;

    /** manual / wrongs / teacher_push */
    private String source;

    /** 0=私有 1=共享（预留） */
    private Integer status;

}
