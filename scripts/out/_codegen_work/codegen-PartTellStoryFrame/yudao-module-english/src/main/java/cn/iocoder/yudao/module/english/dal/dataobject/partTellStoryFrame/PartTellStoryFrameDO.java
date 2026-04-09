package cn.iocoder.yudao.module.english.dal.dataobject.partTellStoryFrame;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 讲故事 - 单帧 DO
 *
 * @author 苦瓜科技
 */
@TableName("esc_part_tell_story_frame")
@KeySequence("esc_part_tell_story_frame_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartTellStoryFrameDO extends BaseDO {

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
     * 帧序号 1~5
     */
    private Integer frameNo;
    /**
     * 该帧图片 URL
     */
    private String imageUrl;
    /**
     * 该帧参考句子
     */
    private String sentenceText;
    /**
     * 提示词 / 关键词
     */
    private String hint;
    /**
     * 排序
     */
    private Integer sort;


}