package cn.kugua.module.english.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Schema(description = "H5 - 账号密码注册 Request VO")
@Data
public class AppUsernameRegisterReqVO {

    @Schema(description = "账号（4-20位，字母/数字/下划线）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "账号不能为空")
    @Length(min = 4, max = 20, message = "账号长度 4-20 位")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "账号只能包含字母、数字、下划线")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 32, message = "密码长度 6-32 位")
    private String password;

}
