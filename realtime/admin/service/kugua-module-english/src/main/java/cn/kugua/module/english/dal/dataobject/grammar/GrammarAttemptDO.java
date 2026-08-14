package cn.kugua.module.english.dal.dataobject.grammar;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("esc_grammar_attempt")
@Data
@EqualsAndHashCode(callSuper = true)
public class GrammarAttemptDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long questionId;
    private Long grammarPointId;
    private Integer difficulty;
    private String answerText;
    private Boolean correct;
    private Integer durationSeconds;
}
