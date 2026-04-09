package cn.kugua.module.english.controller.admin.aiCallLog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - AI 调用日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AiCallLogRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10864")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_student.id（学生发起时）", example = "28904")
    @ExcelProperty("esc_student.id（学生发起时）")
    private Long studentId;

    @Schema(description = "system_users.id（管理员/老师发起时）", example = "22880")
    @ExcelProperty("system_users.id（管理员/老师发起时）")
    private Long userId;

    @Schema(description = "服务类型 llm / asr / tts", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("服务类型 llm / asr / tts")
    private String serviceType;

    @Schema(description = "具体引擎名")
    @ExcelProperty("具体引擎名")
    private String engine;

    @Schema(description = "调用端点")
    @ExcelProperty("调用端点")
    private String endpoint;

    @Schema(description = "请求字节数")
    @ExcelProperty("请求字节数")
    private Integer requestSize;

    @Schema(description = "响应字节数")
    @ExcelProperty("响应字节数")
    private Integer responseSize;

    @Schema(description = "耗时（毫秒）")
    @ExcelProperty("耗时（毫秒）")
    private Integer durationMs;

    @Schema(description = "响应码")
    @ExcelProperty("响应码")
    private Integer statusCode;

    @Schema(description = "是否成功 1=成功 0=失败", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否成功 1=成功 0=失败")
    private Integer success;

    @Schema(description = "错误信息")
    @ExcelProperty("错误信息")
    private String errorMessage;

    @Schema(description = "客户端 IP")
    @ExcelProperty("客户端 IP")
    private String clientIp;

    @Schema(description = "调用日期（用于按天配额）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("调用日期（用于按天配额）")
    private LocalDate callDate;

    @Schema(description = "添加时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("添加时间")
    private LocalDateTime createTime;

}
