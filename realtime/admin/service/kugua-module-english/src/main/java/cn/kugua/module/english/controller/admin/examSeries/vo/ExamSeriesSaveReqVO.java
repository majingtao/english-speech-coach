package cn.kugua.module.english.controller.admin.examSeries.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 考试系列字典新增/修改 Request VO")
@Data
public class ExamSeriesSaveReqVO {

    @Schema(description = "主键", example = "22681")
    private Long id;

    @Schema(description = "系列编码：go_flyers / flyers_1 / aep_1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "系列编码不能为空")
    private String code;

    @Schema(description = "所属级别编码：flyers / movers / starters / ket / pet / nce_1 ...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "所属级别编码不能为空")
    private String levelCode;

    @Schema(description = "显示名：Go Flyers / Flyers 1 (2014)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "显示名不能为空")
    private String name;

    @Schema(description = "封面图 URL")
    private String coverUrl;

    @Schema(description = "出版方：Cambridge / Macmillan", example = "Cambridge")
    private String publisher;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

}
