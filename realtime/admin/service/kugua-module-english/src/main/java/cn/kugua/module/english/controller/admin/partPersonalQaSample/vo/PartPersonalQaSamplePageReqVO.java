package cn.kugua.module.english.controller.admin.partPersonalQaSample.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 个人问答 - 示例答案分页 Request VO")
@Data
public class PartPersonalQaSamplePageReqVO extends PageParam {

    @Schema(description = "esc_part_personal_question.id")
    private Long questionId;

    @Schema(description = "示例序号")
    private Integer sampleNo;

    @Schema(description = "示例答案")
    private String sampleText;

    @Schema(description = "水平标签")
    private String levelTag;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
