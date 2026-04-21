package cn.kugua.module.english.controller.admin.dictation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 听写单词创建/修改 Request VO")
@Data
public class DictationWordSaveReqVO {

    @Schema(description = "主键，修改时必填")
    private Long id;

    @Schema(description = "英文单词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "英文单词不能为空")
    private String en;

    @Schema(description = "中文释义")
    private String cn;

    @Schema(description = "词性")
    private String pos;

    @Schema(description = "词形变化展示行")
    private String forms;

    @Schema(description = "结构化词形 JSON")
    private String formsJson;

    @Schema(description = "例句")
    private String example;

    @Schema(description = "难度 1-5")
    private Integer difficulty;

    @Schema(description = "LLM状态 0=待处理 1=已完成 2=失败")
    private Integer llmStatus;

}
