package cn.kugua.module.english.dal.dataobject.practiceSession;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 练习会话 DO
 *
 * @author 苦瓜
 */
@TableName("esc_practice_session")
@KeySequence("esc_practice_session_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticeSessionDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * esc_student.id
     */
    private Long studentId;
    /**
     * esc_exam.id（具体版本）
     */
    private Long examId;
    /**
     * 冗余 exam_code 便于跨版本统计
     */
    private String examCode;
    /**
     * 模式：exam / free_talk
     */
    private String mode;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 总时长（秒）
     */
    private Integer durationSec;
    /**
     * 0=进行中 1=已完成 2=已放弃
     */
    private Integer status;
    /**
     * 整卷综合分 0-100
     */
    private Integer finalOverallScore;
    /**
     * 整卷综合评语
     */
    private String finalComment;


}
