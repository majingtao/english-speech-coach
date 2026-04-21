package cn.kugua.module.english.controller.admin.dictation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 听写单词分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictationWordPageReqVO extends PageParam {

    @Schema(description = "英文，模糊匹配")
    private String en;

    @Schema(description = "中文，模糊匹配")
    private String cn;

    @Schema(description = "词性")
    private String pos;

    @Schema(description = "LLM状态 0=待处理 1=已完成 2=失败")
    private Integer llmStatus;

    @Schema(description = "难度 1-5")
    private Integer difficulty;

}
