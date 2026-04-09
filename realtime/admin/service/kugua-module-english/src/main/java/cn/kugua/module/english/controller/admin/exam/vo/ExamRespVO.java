package cn.kugua.module.english.controller.admin.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 试卷 Response VO")
@Data
public class ExamRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "试卷编码", example = "gf_1")
    private String examCode;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "是否当前生效版本")
    private Integer isActive;

    @Schema(description = "级别编码")
    private String levelCode;

    @Schema(description = "试卷显示名")
    private String label;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "出题年份")
    private String year;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态：0=草稿 1=发布 2=下架")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
