package cn.kugua.module.english.controller.admin.partInfoExchangeQa.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 信息互换 - 问答条目分页 Request VO")
@Data
public class PartInfoExchangeQaPageReqVO extends PageParam {

    @Schema(description = "esc_part_info_exchange_card.id")
    private Long cardId;

    @Schema(description = "问答序号")
    private Integer qaNo;

    @Schema(description = "提示项")
    private String promptLabel;

    @Schema(description = "完整问句")
    private String questionText;

    @Schema(description = "参考答案")
    private String answerText;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
