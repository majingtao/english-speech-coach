package cn.kugua.module.english.dal.dataobject.practiceAnswer;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 练习单题作答 DO
 *
 * @author 苦瓜
 */
@TableName("esc_practice_answer")
@KeySequence("esc_practice_answer_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticeAnswerDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * esc_practice_session.id
     */
    private Long sessionId;
    /**
     * esc_exam_part.id
     */
    private Long partId;
    /**
     * 引用题目所在子表名
     */
    private String itemRefTable;
    /**
     * 引用题目主键
     */
    private Long itemRefId;
    /**
     * 该 part 内的顺序
     */
    private Integer seq;
    /**
     * 题目快照（防止题库后续变动）
     */
    private String questionSnapshot;
    /**
     * 学生录音 URL
     */
    private String audioUrl;
    /**
     * ASR 转写文本
     */
    private String asrText;
    /**
     * ASR 引擎
     */
    private String asrEngine;
    /**
     * ASR 处理耗时（毫秒）
     */
    private Integer asrDurationMs;
    /**
     * 语法词汇 0-100
     */
    private Integer scoreGrammarVocab;
    /**
     * 发音 0-100
     */
    private Integer scorePronunciation;
    /**
     * 互动 0-100
     */
    private Integer scoreInteraction;
    /**
     * 篇章组织 0-100（PET 及以上）
     */
    private Integer scoreDiscourse;
    /**
     * 综合分 0-100
     */
    private Integer scoreOverall;
    /**
     * LLM 中文反馈
     */
    private String feedbackText;
    /**
     * LLM 修正版本
     */
    private String correctedText;
    /**
     * LLM 引擎 / 模型名
     */
    private String llmEngine;
    /**
     * 原始 LLM 响应（调试 / 审计）
     */
    private String llmRawResponse;


}
