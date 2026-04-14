package cn.kugua.module.english.dal.dataobject.partFindDiffDifference;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 找不同 - 差异点 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_find_diff_difference")
@KeySequence("esc_part_find_diff_difference_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartFindDiffDifferenceDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * esc_part_find_diff_pair.id
     */
    private Long pairId;
    /**
     * 差异点序号
     */
    private Integer diffNo;
    /**
     * 差异描述（英文）
     */
    private String description;
    /**
     * 关键词，便于评分匹配
     */
    private String keyword;
    /**
     * 排序
     */
    private Integer sort;

}
