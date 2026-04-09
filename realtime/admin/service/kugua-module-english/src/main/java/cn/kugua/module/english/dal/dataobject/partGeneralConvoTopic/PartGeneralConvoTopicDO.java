package cn.kugua.module.english.dal.dataobject.partGeneralConvoTopic;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 一般对话 - 主题 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_general_convo_topic")
@KeySequence("esc_part_general_convo_topic_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartGeneralConvoTopicDO extends BaseDO {

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
     * 对话主题
     */
    private String topic;
    /**
     * 主题引导
     */
    private String intro;
    /**
     * 排序
     */
    private Integer sort;


}
