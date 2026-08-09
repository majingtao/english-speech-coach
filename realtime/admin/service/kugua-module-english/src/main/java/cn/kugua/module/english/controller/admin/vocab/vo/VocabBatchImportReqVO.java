package cn.kugua.module.english.controller.admin.vocab.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 词汇批量导入 Request VO")
@Data
public class VocabBatchImportReqVO {

    @Schema(description = "批次默认级别（item 自带 level 时该项覆盖批次；若 item 和批次都缺失则跳过该词）")
    private String levelCode;

    @Schema(description = "词条列表（离线 LLM 生成后批量导入）")
    private List<Item> items;

    @Data
    public static class Item {

        @Schema(description = "单词，必填", requiredMode = Schema.RequiredMode.REQUIRED)
        private String word;

        @Schema(description = "级别（可选；不填则用批次 levelCode）。支持一次混级别导入，如某条 ket、某条 pet")
        private String level;

        @Schema(description = "词性，逗号分隔，如 noun,verb")
        private String pos;

        @Schema(description = "难度 1=A2 必备 / 2=B1 延展（默认 1）")
        private Integer difficulty;

        /** 主题 code 列表（按 V1_0_15 24 类 KET 官方主题，如 ["food-drink","personal-feelings"]） */
        @Schema(description = "主题 code 列表")
        private List<String> themes;

        @Schema(description = "状态 0=草稿 1=发布 2=归档（默认 1）")
        private Integer status;

        /**
         * LLM 生成的内容 JSON。
         * 结构同 /py/vocab/generate 输出：{definition_cn, definition_en, ipa, forms, examples}。
         * 后端会按原样写入 content_json，并把 forms 子节点同步到 forms_json 列。
         */
        @Schema(description = "LLM 生成的内容（含 definition_cn/definition_en/ipa/forms/examples）")
        private Map<String, Object> content;

        @Schema(description = "英式发音 URL（可选；离线 TTS 上传后贴 URL，省一次懒生成）")
        private String audioUkUrl;

        @Schema(description = "美式发音 URL（可选）")
        private String audioUsUrl;

        @Schema(description = "CEFR 主级别（A1/A2/B1）")
        private String cefr;

        @Schema(description = "CEFR 所有出现级别，导入端可传数组（如 [\"A1\",\"A2\"]），后端会逗号 join 存 cefr_list 列")
        @JsonAlias("cefr_list")
        private List<String> cefrList;

        @Schema(description = "能力要求 1=三会 / 2=四会；默认 1（不传按 1 入库）")
        @JsonAlias("mastery_level")
        private Integer masteryLevel;
    }

}
