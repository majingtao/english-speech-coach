package cn.kugua.module.english.dal.dataobject.dictationWord;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 听写-单词主表 DO（esc_dictation_word）
 */
@TableName("esc_dictation_word")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictationWordDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 英文单词/短语 */
    private String en;

    /** 中文释义 */
    private String cn;

    /** 词性 n./v./adj./adv./prep. */
    private String pos;

    /** 词形变化展示行 */
    private String forms;

    /** 结构化词形 JSON [{word,en,zh}] */
    private String formsJson;

    /** 例句（en\nzh 成对） */
    private String example;

    /** 难度 1-5 */
    private Integer difficulty;

    /** 0=待处理 1=已完成 2=失败 */
    private Integer llmStatus;

}
