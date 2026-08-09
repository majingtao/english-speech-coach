package cn.kugua.module.english.dal.dataobject.vocab;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 词汇-全量词汇主表 DO（esc_vocab）
 */
@TableName("esc_vocab")
@Data
@EqualsAndHashCode(callSuper = true)
public class VocabDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 单词 */
    private String word;

    /** 级别：flyers / ket / pet */
    private String levelCode;

    /** 词性，逗号分隔（noun,verb） */
    private String pos;

    /** 1=A2 必备 2=B1 延展 */
    private Integer difficulty;

    /** 0=草稿 1=发布 2=归档 */
    private Integer status;

    /** 排序 */
    private Integer sort;

    /** LLM + IPA 缓存：{definition_cn, definition_en, ipa, examples, gen_at, model_used} */
    private String contentJson;

    /** 词形变化 JSON（动词 past/ing/third_person、名词 plural、形容词 comparative…） */
    private String formsJson;

    /** 英式发音音频 URL（懒生成，存 yudao infra 文件服务） */
    private String audioUkUrl;

    /** 美式发音音频 URL（懒生成） */
    private String audioUsUrl;

    /** CEFR 主级别（A1/A2/B1） */
    private String cefr;

    /** CEFR 所有出现级别，逗号分隔（如 "A1,A2"） */
    private String cefrList;

    /** 能力要求：1=三会(receptive)，2=四会(productive，必须会拼写) */
    private Integer masteryLevel;

}
