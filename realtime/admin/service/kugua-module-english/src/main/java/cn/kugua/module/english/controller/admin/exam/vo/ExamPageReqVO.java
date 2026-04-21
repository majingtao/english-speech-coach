package cn.kugua.module.english.controller.admin.exam.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 试卷分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExamPageReqVO extends PageParam {

    @Schema(description = "试卷编码，模糊匹配")
    private String examCode;

    @Schema(description = "试卷名称，模糊匹配")
    private String label;

    @Schema(description = "级别编码：flyers / ket / pet")
    private String levelCode;

    @Schema(description = "系列编码：go_flyers / flyers_1 / aep_1")
    private String seriesCode;

    @Schema(description = "是否当前生效版本：0=否 1=是")
    private Integer isActive;

    @Schema(description = "状态：0=草稿 1=发布 2=下架")
    private Integer status;

}
