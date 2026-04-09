package cn.kugua.module.english.dal.dataobject.exam;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷主表 DO（esc_exam）
 * <p>
 * V2 版本化：exam_code 跨版本稳定，version 递增，is_active 标识当前生效版本。
 * 多租户：tenant_id = 0 为公共题库，>0 为租户私有。
 */
@TableName("esc_exam")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 试卷稳定编码（跨版本不变），如 gf_1 / aep1_2 */
    private String examCode;

    /** 版本号，递增 */
    private Integer version;

    /** 是否当前生效版本：0=否 1=是 */
    private Integer isActive;

    /** 级别编码，引用 esc_exam_level.code：flyers / ket / pet */
    private String levelCode;

    /** 试卷显示名 */
    private String label;

    /** 来源（Go Flyers / Authentic Exam Papers ...） */
    private String source;

    /** 出题年份 */
    private String year;

    /** 描述 */
    private String description;

    /** 0=草稿 1=发布 2=下架 */
    private Integer status;

}
