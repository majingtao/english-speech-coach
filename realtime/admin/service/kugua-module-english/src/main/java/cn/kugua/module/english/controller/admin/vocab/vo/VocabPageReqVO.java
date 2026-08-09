package cn.kugua.module.english.controller.admin.vocab.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 词汇分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class VocabPageReqVO extends PageParam {

    @Schema(description = "单词模糊匹配")
    private String word;

    @Schema(description = "级别")
    private String levelCode;

    @Schema(description = "难度 1/2")
    private Integer difficulty;

    @Schema(description = "状态 0/1/2")
    private Integer status;

    @Schema(description = "主题ID（按主题过滤）")
    private Long themeId;

    @Schema(description = "能力要求 1=三会 / 2=四会")
    private Integer masteryLevel;

}
