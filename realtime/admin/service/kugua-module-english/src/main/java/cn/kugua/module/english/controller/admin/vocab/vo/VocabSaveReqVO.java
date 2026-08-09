package cn.kugua.module.english.controller.admin.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 词汇创建/修改 Request VO")
@Data
public class VocabSaveReqVO {

    @Schema(description = "主键，修改时必填")
    private Long id;

    @Schema(description = "单词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "单词不能为空")
    private String word;

    @Schema(description = "级别 flyers/ket/pet", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "级别不能为空")
    private String levelCode;

    @Schema(description = "词性，逗号分隔")
    private String pos;

    @Schema(description = "难度 1=A2 必备 2=B1 延展")
    private Integer difficulty;

    @Schema(description = "0=草稿 1=发布 2=归档")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "content_json（一般不手改，由 LLM 生成）")
    private String contentJson;

    @Schema(description = "词形变化 JSON（如 {\"past\":\"decided\",\"ing\":\"deciding\"}）")
    private String formsJson;

    @Schema(description = "英式发音 URL（一般留空由学员端首次播放时懒生成）")
    private String audioUkUrl;

    @Schema(description = "美式发音 URL（一般留空由学员端首次播放时懒生成）")
    private String audioUsUrl;

    @Schema(description = "主题ID数组")
    private List<Long> themeIds;

    @Schema(description = "CEFR 主级别（A1/A2/B1）")
    private String cefr;

    @Schema(description = "CEFR 所有出现级别，逗号分隔（如 \"A1,A2\"）")
    private String cefrList;

    @Schema(description = "能力要求：1=三会(receptive) / 2=四会(productive，必须会拼写)；默认 1")
    private Integer masteryLevel;

}
