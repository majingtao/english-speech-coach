package cn.kugua.module.english.controller.admin.dictation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 听写词书创建/修改 Request VO")
@Data
public class DictationWordlistSaveReqVO {

    @Schema(description = "主键，修改时必填")
    private Long id;

    @Schema(description = "显示名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "词书名称不能为空")
    private String name;

    @Schema(description = "分类类型 SCHOOL_GRADE/EXAM", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分类类型不能为空")
    private String categoryType;

    @Schema(description = "学段 primary/middle/high")
    private String schoolLevel;

    @Schema(description = "年级 1-12")
    private Integer grade;

    @Schema(description = "学期 1=上 2=下")
    private Integer semester;

    @Schema(description = "教材版本")
    private String edition;

    @Schema(description = "单元标签")
    private String unitLabel;

    @Schema(description = "考试级别编码")
    private String examLevelCode;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0=草稿 1=发布 2=下架")
    private Integer status;

}
