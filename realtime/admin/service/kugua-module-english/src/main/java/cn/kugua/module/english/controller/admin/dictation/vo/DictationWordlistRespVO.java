package cn.kugua.module.english.controller.admin.dictation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 听写词书 Response VO")
@Data
public class DictationWordlistRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "显示名")
    private String name;

    @Schema(description = "分类类型")
    private String categoryType;

    @Schema(description = "学段")
    private String schoolLevel;

    @Schema(description = "年级")
    private Integer grade;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "教材版本")
    private String edition;

    @Schema(description = "单元标签")
    private String unitLabel;

    @Schema(description = "考试级别编码")
    private String examLevelCode;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "单词数")
    private Integer wordCount;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
