package cn.kugua.module.english.dal.dataobject.partInfoExchangeQa;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 信息互换 - 问答条目 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_info_exchange_qa")
@KeySequence("esc_part_info_exchange_qa_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartInfoExchangeQaDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * esc_part_info_exchange_card.id
     */
    private Long cardId;
    /**
     * 问答序号
     */
    private Integer qaNo;
    /**
     * 提示项（如 Place / Date / Cost）
     */
    private String promptLabel;
    /**
     * 完整问句
     */
    private String questionText;
    /**
     * 参考答案
     */
    private String answerText;
    /**
     * 排序
     */
    private Integer sort;

}
