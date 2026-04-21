package cn.kugua.module.english.controller.admin.dictation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 听写单词 Response VO")
@Data
public class DictationWordRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "英文单词")
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

    @Schema(description = "LLM状态")
    private Integer llmStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
