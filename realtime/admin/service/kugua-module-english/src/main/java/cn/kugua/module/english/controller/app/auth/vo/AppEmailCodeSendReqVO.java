package cn.kugua.module.english.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "H5 - 发送邮箱验证码 Request VO")
@Data
public class AppEmailCodeSendReqVO {

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "场景：register / login / reset", requiredMode = Schema.RequiredMode.REQUIRED, example = "register")
    @NotEmpty(message = "场景不能为空")
    private String scene;

}
