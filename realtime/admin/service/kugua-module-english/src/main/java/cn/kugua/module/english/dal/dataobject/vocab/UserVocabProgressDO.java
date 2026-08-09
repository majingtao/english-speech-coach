package cn.kugua.module.english.dal.dataobject.vocab;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 词汇-学员 SRS 进度 DO（esc_user_vocab_progress）
 */
@TableName("esc_user_vocab_progress")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserVocabProgressDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long vocabId;

    /** 0=新词 1=学习中 2=已掌握 */
    private Integer status;

    private Integer repetitions;

    private Integer intervalDays;

    /** 难易系数×100（SM-2 备用） */
    private Integer ease;

    private LocalDateTime lastReviewAt;

    private LocalDateTime nextReviewAt;

    private Integer correctCount;

    private Integer wrongCount;

}
