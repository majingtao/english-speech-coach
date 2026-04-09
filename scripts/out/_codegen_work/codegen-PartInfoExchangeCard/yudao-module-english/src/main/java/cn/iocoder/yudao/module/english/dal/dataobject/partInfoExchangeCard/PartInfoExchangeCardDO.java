package cn.iocoder.yudao.module.english.dal.dataobject.partInfoExchangeCard;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 信息互换 - 卡片 DO
 *
 * @author 苦瓜
 */
@TableName("esc_part_info_exchange_card")
@KeySequence("esc_part_info_exchange_card_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartInfoExchangeCardDO extends BaseDO {

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
     * 阶段：A=学生提问 B=学生回答
     */
    private String phase;
    /**
     * 卡片标题，如 Football Match
     */
    private String cardTitle;
    /**
     * 卡片图片 URL
     */
    private String cardImageUrl;
    /**
     * 卡片说明
     */
    private String intro;
    /**
     * 排序
     */
    private Integer sort;


}