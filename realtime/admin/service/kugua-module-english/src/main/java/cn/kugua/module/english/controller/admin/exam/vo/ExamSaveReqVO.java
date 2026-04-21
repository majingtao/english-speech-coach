package cn.kugua.module.english.controller.admin.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 试卷创建/修改 Request VO")
@Data
public class ExamSaveReqVO {

    @Schema(description = "主键，修改时必填")
    private Long id;

    @Schema(description = "试卷编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "gf_1")
    @NotEmpty(message = "试卷编码不能为空")
    private String examCode;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "版本号不能为空")
    private Integer version;

    @Schema(description = "是否当前生效版本：0=否 1=是", example = "1")
    private Integer isActive;

    @Schema(description = "级别编码：flyers / ket / pet", requiredMode = Schema.RequiredMode.REQUIRED, example = "flyers")
    @NotEmpty(message = "级别编码不能为空")
    private String levelCode;

    @Schema(description = "系列编码：go_flyers / flyers_1 / aep_1 ...", example = "go_flyers")
    private String seriesCode;

    @Schema(description = "试卷显示名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "试卷名称不能为空")
    private String label;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "出题年份")
    private String year;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态：0=草稿 1=发布 2=下架", example = "0")
    private Integer status;

    @Schema(description = "试卷完整 JSON")
    private String contentJson;

}
