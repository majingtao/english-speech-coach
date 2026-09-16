package cn.kugua.module.english.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Schema(description = "H5 - 邮箱验证码重置密码 Request VO")
@Data
public class AppEmailResetPasswordReqVO {

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "邮箱验证码（scene=reset）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "验证码不能为空")
    @Length(min = 4, max = 8, message = "验证码长度 4-8 位")
    @Pattern(regexp = "^[0-9]+$", message = "验证码必须都是数字")
    private String code;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "新密码不能为空")
    @Length(min = 6, max = 32, message = "密码长度 6-32 位")
    private String password;

}
