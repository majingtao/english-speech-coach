package cn.kugua.module.english.controller.app.auth;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.kugua.module.english.controller.app.auth.vo.*;
import cn.kugua.module.english.service.auth.AppEmailAuthService;
import cn.kugua.module.english.service.auth.EmailCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "H5 - 邮箱/账号认证")
@RestController
@RequestMapping("/member/email-auth")
@Validated
public class AppEmailAuthController {

    @Resource
    private AppEmailAuthService authService;

    @Resource
    private EmailCodeService emailCodeService;

    // ========== 唯一性检查 ==========

    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱是否已注册")
    @Parameter(name = "email", description = "邮箱", required = true)
    @PermitAll
    public CommonResult<Boolean> checkEmail(@RequestParam("email") String email) {
        return success(authService.isEmailRegistered(email));
    }

    @GetMapping("/check-username")
    @Operation(summary = "检查账号是否已注册")
    @Parameter(name = "username", description = "账号", required = true)
    @PermitAll
    public CommonResult<Boolean> checkUsername(@RequestParam("username") String username) {
        return success(authService.isUsernameRegistered(username));
    }

    @GetMapping("/check-mobile")
    @Operation(summary = "检查手机号是否已注册")
    @Parameter(name = "mobile", description = "手机号", required = true)
    @PermitAll
    public CommonResult<Boolean> checkMobile(@RequestParam("mobile") String mobile) {
        return success(authService.isMobileRegistered(mobile));
    }

    // ========== 验证码 ==========

    @PostMapping("/send-email-code")
    @Operation(summary = "发送邮箱验证码")
    @PermitAll
    public CommonResult<Boolean> sendEmailCode(@RequestBody @Valid AppEmailCodeSendReqVO reqVO) {
        emailCodeService.sendCode(reqVO.getEmail(), reqVO.getScene());
        return success(true);
    }

    // ========== 注册 ==========

    @PostMapping("/email-register")
    @Operation(summary = "邮箱注册")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> emailRegister(@RequestBody @Valid AppEmailRegisterReqVO reqVO) {
        return success(authService.emailRegister(reqVO));
    }

    @PostMapping("/username-register")
    @Operation(summary = "账号密码注册")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> usernameRegister(@RequestBody @Valid AppUsernameRegisterReqVO reqVO) {
        return success(authService.usernameRegister(reqVO));
    }

    // ========== 登录 ==========

    @PostMapping("/email-login")
    @Operation(summary = "邮箱 + 密码登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> emailLogin(@RequestBody @Valid AppEmailLoginReqVO reqVO) {
        return success(authService.emailLogin(reqVO));
    }

    @PostMapping("/username-login")
    @Operation(summary = "账号密码登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> usernameLogin(@RequestBody @Valid AppUsernameLoginReqVO reqVO) {
        return success(authService.usernameLogin(reqVO));
    }

}
