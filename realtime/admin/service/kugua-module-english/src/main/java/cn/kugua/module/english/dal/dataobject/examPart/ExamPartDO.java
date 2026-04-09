package cn.kugua.module.english.dal.dataobject.examPart;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 试卷 Part 多态头 DO
 *
 * @author 苦瓜
 */
@TableName("esc_exam_part")
@KeySequence("esc_exam_part_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamPartDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 所属试卷 esc_exam.id
     */
    private Long examId;
    /**
     * 题段序号 1/2/3/4
     */
    private Integer partNo;
    /**
     * 题型编码，引用 esc_exam_part_type.code
     */
    private String partType;
    /**
     * Part 显示标题
     */
    private String title;
    /**
     * Part 引导语 / 考官口述
     */
    private String intro;
    /**
     * 时间限制（秒），可为空
     */
    private Integer timeLimitSec;
    /**
     * 排序
     */
    private Integer sort;


}
