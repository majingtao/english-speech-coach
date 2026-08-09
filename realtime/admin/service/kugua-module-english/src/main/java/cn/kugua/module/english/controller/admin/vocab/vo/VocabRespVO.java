package cn.kugua.module.english.controller.admin.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 词汇 Response VO")
@Data
public class VocabRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "单词")
    private String word;

    @Schema(description = "级别")
    private String levelCode;

    @Schema(description = "词性")
    private String pos;

    @Schema(description = "难度")
    private Integer difficulty;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "content_json")
    private String contentJson;

    @Schema(description = "词形变化 JSON")
    private String formsJson;

    @Schema(description = "英式发音 URL")
    private String audioUkUrl;

    @Schema(description = "美式发音 URL")
    private String audioUsUrl;

    @Schema(description = "主题ID数组")
    private List<Long> themeIds;

    @Schema(description = "CEFR 主级别")
    private String cefr;

    @Schema(description = "CEFR 所有出现级别，逗号分隔")
    private String cefrList;

    @Schema(description = "能力要求 1=三会 / 2=四会")
    private Integer masteryLevel;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
