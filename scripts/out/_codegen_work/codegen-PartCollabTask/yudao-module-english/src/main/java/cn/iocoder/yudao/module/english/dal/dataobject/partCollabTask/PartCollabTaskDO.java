package cn.iocoder.yudao.module.english.dal.dataobject.partCollabTask;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 协作任务 - 主体 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_collab_task")
@KeySequence("esc_part_collab_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartCollabTaskDO extends BaseDO {

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
     * 情境描述
     */
    private String scenario;
    /**
     * 辅助图片 URL
     */
    private String imageUrl;
    /**
     * 任务指令
     */
    private String instruction;


}