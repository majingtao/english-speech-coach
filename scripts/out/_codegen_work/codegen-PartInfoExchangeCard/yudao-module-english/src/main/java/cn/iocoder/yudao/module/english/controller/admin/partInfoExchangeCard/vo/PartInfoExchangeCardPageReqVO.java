package cn.iocoder.yudao.module.english.controller.admin.partInfoExchangeCard.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 信息互换 - 卡片分页 Request VO")
@Data
public class PartInfoExchangeCardPageReqVO extends PageParam {

    @Schema(description = "esc_exam_part.id", example = "21763")
    private Long partId;

    @Schema(description = "阶段：A=学生提问 B=学生回答")
    private String phase;

    @Schema(description = "卡片标题，如 Football Match")
    private String cardTitle;

    @Schema(description = "卡片图片 URL", example = "https://www.iocoder.cn")
    private String cardImageUrl;

    @Schema(description = "卡片说明")
    private String intro;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}