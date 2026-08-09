package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "H5 - 主题项")
@Data
public class AppVocabThemeRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "中文名")
    private String nameCn;

    @Schema(description = "英文名")
    private String nameEn;

    @Schema(description = "级别")
    private String levelCode;

    @Schema(description = "排序")
    private Integer sort;

}
