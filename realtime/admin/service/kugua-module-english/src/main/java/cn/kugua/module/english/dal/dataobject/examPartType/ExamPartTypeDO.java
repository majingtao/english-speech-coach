package cn.kugua.module.english.dal.dataobject.examPartType;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 题型字典 DO
 *
 * @author 苦瓜
 */
@TableName("esc_exam_part_type")
@KeySequence("esc_exam_part_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamPartTypeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 题型编码
     */
    private String code;
    /**
     * 显示名
     */
    private String name;
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
