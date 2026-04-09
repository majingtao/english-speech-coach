package cn.kugua.module.english.dal.dataobject.partPersonalQuestion;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 个人问答 - 问题 DO
 *
 * @author 苦瓜科技
 */
@TableName("esc_part_personal_question")
@KeySequence("esc_part_personal_question_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartPersonalQuestionDO extends BaseDO {

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
     * 题号
     */
    private Integer qNo;
    /**
     * 问题
     */
    private String questionText;
    /**
     * 主题分组
     */
    private String topic;
    /**
     * 排序
     */
    private Integer sort;


}
