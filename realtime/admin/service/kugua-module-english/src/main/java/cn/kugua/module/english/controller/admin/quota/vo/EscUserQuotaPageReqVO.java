package cn.kugua.module.english.controller.admin.quota.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 单用户配额分页 Request VO")
@Data
public class EscUserQuotaPageReqVO extends PageParam {

    @Schema(description = "会员 ID")
    private Long userId;

    @Schema(description = "昵称（模糊搜索）")
    private String nickname;

    @Schema(description = "是否启用")
    private Boolean enabled;

}
