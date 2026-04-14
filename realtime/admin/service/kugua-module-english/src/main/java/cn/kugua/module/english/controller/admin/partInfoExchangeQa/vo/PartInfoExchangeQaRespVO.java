package cn.kugua.module.english.controller.admin.partInfoExchangeQa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 信息互换 - 问答条目 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PartInfoExchangeQaRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "esc_part_info_exchange_card.id", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("卡片ID")
    private Long cardId;

    @Schema(description = "问答序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("问答序号")
    private Integer qaNo;

    @Schema(description = "提示项")
    @ExcelProperty("提示项")
    private String promptLabel;

    @Schema(description = "完整问句")
    @ExcelProperty("完整问句")
    private String questionText;

    @Schema(description = "参考答案")
    @ExcelProperty("参考答案")
    private String answerText;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
