package cn.kugua.module.english.dal.dataobject.grammar;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("esc_grammar_generation_job")
@Data
@EqualsAndHashCode(callSuper = true)
public class GrammarGenerationJobDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long grammarPointId;
    private Integer requestedCount;
    private Integer acceptedCount;
    private Integer difficulty;
    private String questionTypes;
    private Boolean autoPublish;
    private String model;
    private String promptVersion;
    private String settingsJson;
    private Integer status;
    private String errorMessage;
}
