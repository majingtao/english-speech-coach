package cn.kugua.module.english.service.auth;

import cn.kugua.module.english.controller.app.auth.vo.*;

public interface AppEmailAuthService {

    /** 检查邮箱是否已注册 */
    boolean isEmailRegistered(String email);

    /** 检查账号是否已注册 */
    boolean isUsernameRegistered(String username);

    /** 检查手机号是否已注册（member_user 表） */
    boolean isMobileRegistered(String mobile);

    AppAuthLoginRespVO emailRegister(AppEmailRegisterReqVO reqVO);

    AppAuthLoginRespVO usernameRegister(AppUsernameRegisterReqVO reqVO);

    AppAuthLoginRespVO emailLogin(AppEmailLoginReqVO reqVO);

    AppAuthLoginRespVO usernameLogin(AppUsernameLoginReqVO reqVO);

}
