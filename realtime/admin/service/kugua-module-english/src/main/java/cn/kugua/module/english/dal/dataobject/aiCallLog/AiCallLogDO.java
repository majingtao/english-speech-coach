package cn.kugua.module.english.dal.dataobject.aiCallLog;

import lombok.*;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * AI 调用日志 DO
 *
 * @author 苦瓜
 */
@TableName("esc_ai_call_log")
@KeySequence("esc_ai_call_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCallLogDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * esc_student.id（学生发起时）
     */
    private Long studentId;
    /**
     * system_users.id（管理员/老师发起时）
     */
    private Long userId;
    /**
     * 服务类型 llm / asr / tts
     */
    private String serviceType;
    /**
     * 具体引擎名
     */
    private String engine;
    /**
     * 调用端点
     */
    private String endpoint;
    /**
     * 请求字节数
     */
    private Integer requestSize;
    /**
     * 响应字节数
     */
    private Integer responseSize;
    /**
     * 耗时（毫秒）
     */
    private Integer durationMs;
    /**
     * 响应码
     */
    private Integer statusCode;
    /**
     * 是否成功 1=成功 0=失败
     */
    private Integer success;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 客户端 IP
     */
    private String clientIp;
    /**
     * 调用日期（用于按天配额）
     */
    private LocalDate callDate;


}
