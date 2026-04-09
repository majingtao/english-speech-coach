package cn.kugua.module.english.dal.dataobject.examLevel;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 考试级别字典 DO
 *
 * @author 苦瓜
 */
@TableName("esc_exam_level")
@KeySequence("esc_exam_level_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamLevelDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 级别编码：flyers / ket / pet
     */
    private String code;
    /**
     * 显示名：Cambridge Flyers / KET / PET
     */
    private String name;
    /**
     * CEFR 等级：A2 / B1 ...
     */
    private String cefr;
    /**
     * 说明
     */
    private String description;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 0=启用 1=停用
     */
    private Integer status;


}
