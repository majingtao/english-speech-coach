package cn.kugua.module.english.dal.dataobject.partPersonalQaSample;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 个人问答 - 示例答案 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_personal_qa_sample")
@KeySequence("esc_part_personal_qa_sample_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartPersonalQaSampleDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * esc_part_personal_question.id
     */
    private Long questionId;
    /**
     * 示例序号
     */
    private Integer sampleNo;
    /**
     * 示例答案
     */
    private String sampleText;
    /**
     * 水平标签，如 basic / good
     */
    private String levelTag;
    /**
     * 排序
     */
    private Integer sort;

}
