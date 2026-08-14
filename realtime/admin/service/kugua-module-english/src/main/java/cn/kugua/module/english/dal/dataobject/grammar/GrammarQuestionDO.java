package cn.kugua.module.english.dal.dataobject.grammar;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("esc_grammar_question")
@Data
@EqualsAndHashCode(callSuper = true)
public class GrammarQuestionDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long grammarPointId;
    private Long generationJobId;
    private Long sourceQuestionId;
    private String code;
    private String questionType;
    private Integer difficulty;
    private String instruction;
    private String stem;
    private String optionsJson;
    private String answerJson;
    private String explanationZh;
    private String ruleText;
    private String errorTagsJson;
    private String mediaJson;
    private String source;
    private Integer validationStatus;
    private Integer sort;
    private Integer status;
}
