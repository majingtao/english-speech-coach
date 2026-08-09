package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "H5 - 用户词库 Response VO")
@Data
public class AppUserVocabListRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "来源：manual / wrongs / teacher_push")
    private String source;

    @Schema(description = "词数")
    private Long wordCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
