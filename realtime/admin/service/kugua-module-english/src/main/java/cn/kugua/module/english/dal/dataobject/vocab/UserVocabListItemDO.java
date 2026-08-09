package cn.kugua.module.english.dal.dataobject.vocab;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 词汇-用户词库词条 DO（esc_user_vocab_list_item）
 */
@TableName("esc_user_vocab_list_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserVocabListItemDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long listId;

    private Long vocabId;

    private LocalDateTime addedAt;

}
