package cn.kugua.module.english.dal.dataobject.dictationRecord;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 听写-学生练习记录 DO（esc_dictation_record）
 */
@TableName("esc_dictation_record")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictationRecordDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 单词ID */
    private Long wordId;

    /** 词书ID */
    private Long wordlistId;

    /** 是否认识 */
    private Boolean knowMeaning;

    /** 是否会拼 */
    private Boolean canSpell;

}
