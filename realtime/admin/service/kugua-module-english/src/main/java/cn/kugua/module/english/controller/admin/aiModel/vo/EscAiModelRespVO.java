package cn.kugua.module.english.controller.admin.aiModel.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI模型配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EscAiModelRespVO {

    @Schema(description = "主键")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "类型：llm / asr / tts")
    @ExcelProperty("类型")
    private String type;

    @Schema(description = "提供商")
    @ExcelProperty("提供商")
    private String provider;

    @Schema(description = "模型标识")
    @ExcelProperty("模型标识")
    private String modelKey;

    @Schema(description = "显示名")
    @ExcelProperty("显示名")
    private String label;

    @Schema(description = "扩展配置JSON")
    private String configJson;

    @Schema(description = "是否默认选中")
    @ExcelProperty("默认")
    private Integer isDefault;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "0=启用 1=停用")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
