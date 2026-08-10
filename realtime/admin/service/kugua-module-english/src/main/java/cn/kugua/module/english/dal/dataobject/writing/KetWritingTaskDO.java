package cn.kugua.module.english.dal.dataobject.writing;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_ket_writing_task")
@Data
@EqualsAndHashCode(callSuper = true)
public class KetWritingTaskDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;
    private String levelCode;
    private Integer part;
    private String type;
    private String title;
    private String sourceBook;
    private Integer sourceUnit;
    private Integer sourcePage;
    private String status;
    private String contentJson;
    private Integer sort;
    private Integer enabled;
}
