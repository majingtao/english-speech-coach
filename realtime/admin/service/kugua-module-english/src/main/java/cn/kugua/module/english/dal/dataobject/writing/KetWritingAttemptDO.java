package cn.kugua.module.english.dal.dataobject.writing;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_ket_writing_attempt")
@Data
@EqualsAndHashCode(callSuper = true)
public class KetWritingAttemptDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String taskId;
    private String levelCode;
    private Integer part;
    private String practiceMode;
    private String promptSnapshotJson;
    private String learnerInfoJson;
    private String aiDraftText;
    private String responseText;
    private Integer wordCount;
    private String feedbackJson;
    private String scoreLabel;
}
