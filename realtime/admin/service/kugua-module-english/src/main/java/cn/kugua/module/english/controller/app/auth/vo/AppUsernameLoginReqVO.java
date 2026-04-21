package cn.kugua.module.english.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Schema(description = "H5 - 账号密码登录 Request VO")
@Data
public class AppUsernameLoginReqVO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 32, message = "密码长度 6-32 位")
    private String password;

}
