package cn.iocoder.yudao.module.english.controller.admin.partTellStoryFrame.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 讲故事 - 单帧分页 Request VO")
@Data
public class PartTellStoryFramePageReqVO extends PageParam {

    @Schema(description = "esc_exam_part.id", example = "13064")
    private Long partId;

    @Schema(description = "帧序号 1~5")
    private Integer frameNo;

    @Schema(description = "该帧图片 URL", example = "https://www.iocoder.cn")
    private String imageUrl;

    @Schema(description = "该帧参考句子")
    private String sentenceText;

    @Schema(description = "提示词 / 关键词")
    private String hint;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}