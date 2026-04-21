package cn.kugua.module.english.controller.admin.quota.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "管理后台 - 每日用量归档分页 Request VO")
@Data
public class EscQuotaUsagePageReqVO extends PageParam {

    @Schema(description = "会员 ID")
    private Long userId;

    @Schema(description = "昵称（模糊搜索）")
    private String nickname;

    @Schema(description = "起始日（含）", example = "2026-04-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @Schema(description = "截止日（含）", example = "2026-04-30")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

}
