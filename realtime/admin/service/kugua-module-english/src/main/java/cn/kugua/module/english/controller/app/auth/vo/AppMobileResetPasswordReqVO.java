package cn.kugua.module.english.controller.app.auth.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 手机验证码重置密码。
 * <p>
 * 验证码通过 /member/auth/send-sms-code 发送，scene = 4（SmsSceneEnum.MEMBER_RESET_PASSWORD）。
 * 密码上限 16 位，与 yudao 手机+密码登录（/member/auth/login）的校验保持一致。
 */
@Schema(description = "H5 - 手机验证码重置密码 Request VO")
@Data
public class AppMobileResetPasswordReqVO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotBlank(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "手机验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "验证码不能为空")
    @Length(min = 4, max = 6, message = "验证码长度 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "验证码必须都是数字")
    private String code;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "新密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度 6-16 位")
    private String password;

}
