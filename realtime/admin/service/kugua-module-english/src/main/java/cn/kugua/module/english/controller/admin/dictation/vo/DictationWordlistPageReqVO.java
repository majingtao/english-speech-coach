package cn.kugua.module.english.controller.admin.dictation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 听写词书分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictationWordlistPageReqVO extends PageParam {

    @Schema(description = "词书名称，模糊匹配")
    private String name;

    @Schema(description = "分类类型 SCHOOL_GRADE/EXAM")
    private String categoryType;

    @Schema(description = "学段 primary/middle/high")
    private String schoolLevel;

    @Schema(description = "年级 1-12")
    private Integer grade;

    @Schema(description = "考试级别编码")
    private String examLevelCode;

    @Schema(description = "状态 0=草稿 1=发布 2=下架")
    private Integer status;

}
