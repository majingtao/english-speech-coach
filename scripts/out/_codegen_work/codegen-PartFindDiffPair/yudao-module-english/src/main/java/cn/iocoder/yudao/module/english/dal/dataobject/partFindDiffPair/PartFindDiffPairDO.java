package cn.iocoder.yudao.module.english.dal.dataobject.partFindDiffPair;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 找不同 - 图对 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_find_diff_pair")
@KeySequence("esc_part_find_diff_pair_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartFindDiffPairDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * esc_exam_part.id
     */
    private Long partId;
    /**
     * 图对序号
     */
    private Integer pairNo;
    /**
     * 左图 URL
     */
    private String imageAUrl;
    /**
     * 右图 URL
     */
    private String imageBUrl;
    /**
     * 主题，如 Two pictures of a kitchen
     */
    private String topic;
    /**
     * 排序
     */
    private Integer sort;


}