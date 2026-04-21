package cn.kugua.module.english.dal.dataobject.examSeries;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 考试系列字典 DO（教材/真题集，跨租户共享）
 *
 * @author 苦瓜
 */
@TableName("esc_exam_series")
@KeySequence("esc_exam_series_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSeriesDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 系列编码：go_flyers / flyers_1 / aep_1 ...
     */
    private String code;
    /**
     * 所属级别，引用 esc_exam_level.code
     */
    private String levelCode;
    /**
     * 显示名：Go Flyers / Flyers 1 (2014) / AEP Vol.1
     */
    private String name;
    /**
     * 封面图 URL
     */
    private String coverUrl;
    /**
     * 出版方：Cambridge / Macmillan / ...
     */
    private String publisher;
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
