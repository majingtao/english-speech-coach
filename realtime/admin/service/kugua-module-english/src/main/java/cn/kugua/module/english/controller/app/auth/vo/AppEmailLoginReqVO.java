package cn.kugua.module.english.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "H5 - 邮箱 + 密码登录 Request VO")
@Data
public class AppEmailLoginReqVO {

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    private String password;

}
