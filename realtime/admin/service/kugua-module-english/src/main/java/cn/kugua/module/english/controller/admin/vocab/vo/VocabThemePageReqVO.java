package cn.kugua.module.english.controller.admin.vocab.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 词汇主题分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class VocabThemePageReqVO extends PageParam {

    @Schema(description = "编码")
    private String code;

    @Schema(description = "中文名")
    private String nameCn;

    @Schema(description = "级别")
    private String levelCode;

    @Schema(description = "状态")
    private Integer status;

}
