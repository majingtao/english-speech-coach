package cn.kugua.module.english.controller.admin.examLevel.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 考试级别字典分页 Request VO")
@Data
public class ExamLevelPageReqVO extends PageParam {

    @Schema(description = "级别编码：flyers / ket / pet")
    private String code;

    @Schema(description = "显示名：Cambridge Flyers / KET / PET", example = "张三")
    private String name;

    @Schema(description = "CEFR 等级：A2 / B1 ...")
    private String cefr;

    @Schema(description = "说明", example = "随便")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "0=启用 1=停用", example = "2")
    private Integer status;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}