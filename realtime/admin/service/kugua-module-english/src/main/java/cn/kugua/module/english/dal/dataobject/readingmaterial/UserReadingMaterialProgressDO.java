package cn.kugua.module.english.dal.dataobject.readingmaterial;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@TableName("esc_user_reading_material_progress")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserReadingMaterialProgressDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long materialId;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer readCorrectCount;
    private Integer readWrongCount;
    private Integer spellCorrectCount;
    private Integer spellWrongCount;
    private String lastResult;
    private String lastMode;
    private LocalDateTime lastPracticeAt;
}
