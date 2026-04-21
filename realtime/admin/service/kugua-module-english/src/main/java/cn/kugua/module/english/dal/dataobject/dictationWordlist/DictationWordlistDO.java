package cn.kugua.module.english.dal.dataobject.dictationWordlist;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 听写-词书 DO（esc_dictation_wordlist）
 */
@TableName("esc_dictation_wordlist")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictationWordlistDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 显示名 */
    private String name;

    /** SCHOOL_GRADE 或 EXAM */
    private String categoryType;

    /** primary/middle/high */
    private String schoolLevel;

    /** 年级 1-12 */
    private Integer grade;

    /** 1=上学期 2=下学期 */
    private Integer semester;

    /** 教材版本：人教版/PEP版/外研版 */
    private String edition;

    /** 单元标签 */
    private String unitLabel;

    /** 引用 esc_exam_level.code */
    private String examLevelCode;

    /** 描述 */
    private String description;

    /** 缓存单词数 */
    private Integer wordCount;

    /** 排序 */
    private Integer sort;

    /** 0=草稿 1=发布 2=下架 */
    private Integer status;

}
