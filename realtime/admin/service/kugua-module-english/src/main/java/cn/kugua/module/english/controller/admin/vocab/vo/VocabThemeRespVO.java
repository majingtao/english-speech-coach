package cn.kugua.module.english.controller.admin.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 词汇主题 Response VO")
@Data
public class VocabThemeRespVO {

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

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
