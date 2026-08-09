package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "H5 - 词汇列表项（浏览/SRS 队列共用）")
@Data
public class AppVocabListItemVO {

    @Schema(description = "词条ID")
    private Long id;

    @Schema(description = "单词")
    private String word;

    @Schema(description = "级别")
    private String levelCode;

    @Schema(description = "词性")
    private String pos;

    @Schema(description = "难度")
    private Integer difficulty;

    @Schema(description = "进度状态")
    private Integer progressStatus;

    @Schema(description = "连续答对")
    private Integer repetitions;

}
