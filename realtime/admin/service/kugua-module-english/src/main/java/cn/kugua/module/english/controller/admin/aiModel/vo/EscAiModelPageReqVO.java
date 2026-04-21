package cn.kugua.module.english.controller.admin.aiModel.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - AI模型配置分页 Request VO")
@Data
public class EscAiModelPageReqVO extends PageParam {

    @Schema(description = "类型：llm / asr / tts")
    private String type;

    @Schema(description = "提供商")
    private String provider;

    @Schema(description = "显示名")
    private String label;

    @Schema(description = "0=启用 1=停用")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
