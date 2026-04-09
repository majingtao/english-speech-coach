package cn.kugua.module.english.dal.dataobject.schoolClass;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 班级 DO
 *
 * @author 苦瓜
 */
@TableName("esc_class")
@KeySequence("esc_class_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 班级名称
     */
    private String name;
    /**
     * 班级编码（可选）
     */
    private String code;
    /**
     * 描述
     */
    private String description;
    /**
     * 0=启用 1=停用
     */
    private Integer status;


}
