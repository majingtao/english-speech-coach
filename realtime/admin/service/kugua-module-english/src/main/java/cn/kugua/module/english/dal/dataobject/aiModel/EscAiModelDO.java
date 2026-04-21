package cn.kugua.module.english.dal.dataobject.aiModel;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI 模型配置 DO（esc_ai_model）
 *
 * type 区分用途：llm / asr / tts
 */
@TableName("esc_ai_model")
@KeySequence("esc_ai_model_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscAiModelDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型：llm / asr / tts */
    private String type;

    /** 提供商：openai / claude / moonshot / openrouter / ollama（LLM 用） */
    private String provider;

    /** 模型标识：gpt-4o-mini / sensevoice-small / edge 等 */
    private String modelKey;

    /** 前端显示名 */
    private String label;

    /** 扩展配置 JSON，如 {"use_proxy":true} */
    private String configJson;

    /** 是否默认选中：0=否 1=是（每种 type 最多一个默认） */
    private Integer isDefault;

    /** 排序 */
    private Integer sort;

    /** 0=启用 1=停用 */
    private Integer status;
}
