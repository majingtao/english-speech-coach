package cn.kugua.module.english.dal.dataobject.partLongTurnPhoto;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 独立长答 - 图片描述 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_long_turn_photo")
@KeySequence("esc_part_long_turn_photo_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartLongTurnPhotoDO extends BaseDO {

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
     * 面向的考生 A / B
     */
    private String candidateRole;
    /**
     * 主题
     */
    private String topic;
    /**
     * 图片 URL
     */
    private String imageUrl;
    /**
     * 考官指令
     */
    private String instruction;
    /**
     * 排序
     */
    private Integer sort;


}
