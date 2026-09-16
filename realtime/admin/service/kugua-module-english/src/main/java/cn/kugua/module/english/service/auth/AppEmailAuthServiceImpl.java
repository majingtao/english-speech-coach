package cn.kugua.module.english.service.auth;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.member.controller.app.user.vo.AppMemberUserResetPasswordReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import cn.kugua.module.english.controller.app.auth.vo.*;
import cn.kugua.module.english.dal.dataobject.memberAuth.MemberEmailAccountDO;
import cn.kugua.module.english.dal.mysql.memberAuth.MemberEmailAccountMapper;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getTerminal;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;
import static cn.kugua.module.english.service.auth.EmailCodeService.SCENE_LOGIN;
import static cn.kugua.module.english.service.auth.EmailCodeService.SCENE_REGISTER;
import static cn.kugua.module.english.service.auth.EmailCodeService.SCENE_RESET;

@Slf4j
@Service
public class AppEmailAuthServiceImpl implements AppEmailAuthService {

    private static final BCryptPasswordEncoder PWD = new BCryptPasswordEncoder();

    @Resource
    private MemberEmailAccountMapper accountMapper;
    @Resource
    private MemberUserService memberUserService;
    @Resource
    private EmailCodeService emailCodeService;
    @Resource
    private OAuth2TokenCommonApi oauth2TokenApi;
    @Resource
    private MemberUserMapper memberUserMapper;
    /** yudao member_user.password 使用的加密器（与 /member/auth/login 校验一致） */
    @Resource
    private PasswordEncoder passwordEncoder;

    // ========== 唯一性检查 ==========

    @Override
    public boolean isEmailRegistered(String email) {
        return accountMapper.selectByEmail(email.toLowerCase()) != null;
    }

    @Override
    public boolean isUsernameRegistered(String username) {
        return accountMapper.selectByUsername(username) != null;
    }

    @Override
    public boolean isMobileRegistered(String mobile) {
        return memberUserService.getUserByMobile(mobile) != null;
    }

    // ========== 注册 ==========

    @Override
    @Transactional
    public AppAuthLoginRespVO emailRegister(AppEmailRegisterReqVO reqVO) {
        String email = reqVO.getEmail().toLowerCase();
        if (accountMapper.selectByEmail(email) != null) {
            throw exception(EMAIL_ALREADY_REGISTERED);
        }
        emailCodeService.useCode(email, SCENE_REGISTER, reqVO.getCode());

        String nickname = email.substring(0, email.indexOf('@'));
        String username = "u_" + IdUtil.fastSimpleUUID().substring(0, 10);
        MemberUserDO user = memberUserService.createUser(nickname, null, getClientIP(), getTerminal());

        MemberEmailAccountDO account = MemberEmailAccountDO.builder()
                .userId(user.getId())
                .email(email)
                .username(username)
                .password(PWD.encode(reqVO.getPassword()))
                .emailVerified(true)
                .status(CommonStatusEnum.ENABLE.getStatus())
                .build();
        accountMapper.insert(account);
        return buildToken(user.getId());
    }

    @Override
    @Transactional
    public AppAuthLoginRespVO usernameRegister(AppUsernameRegisterReqVO reqVO) {
        if (accountMapper.selectByUsername(reqVO.getUsername()) != null) {
            throw exception(USERNAME_ALREADY_REGISTERED);
        }
        MemberUserDO user = memberUserService.createUser(reqVO.getUsername(), null, getClientIP(), getTerminal());

        MemberEmailAccountDO account = MemberEmailAccountDO.builder()
                .userId(user.getId())
                .username(reqVO.getUsername())
                .password(PWD.encode(reqVO.getPassword()))
                .emailVerified(false)
                .status(CommonStatusEnum.ENABLE.getStatus())
                .build();
        accountMapper.insert(account);
        return buildToken(user.getId());
    }

    // ========== 登录 ==========

    @Override
    public AppAuthLoginRespVO emailLogin(AppEmailLoginReqVO reqVO) {
        String email = reqVO.getEmail().toLowerCase();
        MemberEmailAccountDO account = accountMapper.selectByEmail(email);
        if (account == null) {
            throw exception(AUTH_EMAIL_NOT_REGISTERED);
        }
        if (CommonStatusEnum.isDisable(account.getStatus())) {
            throw exception(AUTH_ACCOUNT_DISABLED);
        }
        if (StrUtil.isBlank(account.getPassword())
                || !PWD.matches(reqVO.getPassword(), account.getPassword())) {
            throw exception(AUTH_BAD_CREDENTIALS);
        }
        return buildToken(account.getUserId());
    }

    @Override
    public AppAuthLoginRespVO usernameLogin(AppUsernameLoginReqVO reqVO) {
        MemberEmailAccountDO account = accountMapper.selectByUsername(reqVO.getUsername());
        if (account == null || StrUtil.isBlank(account.getPassword())
                || !PWD.matches(reqVO.getPassword(), account.getPassword())) {
            throw exception(AUTH_BAD_CREDENTIALS);
        }
        if (CommonStatusEnum.isDisable(account.getStatus())) {
            throw exception(AUTH_ACCOUNT_DISABLED);
        }
        return buildToken(account.getUserId());
    }

    // ========== 验证码 ==========

    @Override
    public void sendEmailCode(AppEmailCodeSendReqVO reqVO) {
        String email = reqVO.getEmail().toLowerCase();
        String scene = reqVO.getScene();
        if (!SCENE_REGISTER.equals(scene) && !SCENE_LOGIN.equals(scene) && !SCENE_RESET.equals(scene)) {
            throw exception(EMAIL_CODE_SCENE_INVALID);
        }
        if (SCENE_RESET.equals(scene)) {
            // 忘记密码：只给已注册且未停用的邮箱发码，避免被用来给任意邮箱发信
            MemberEmailAccountDO account = accountMapper.selectByEmail(email);
            if (account == null) {
                throw exception(AUTH_EMAIL_NOT_REGISTERED);
            }
            if (CommonStatusEnum.isDisable(account.getStatus())) {
                throw exception(AUTH_ACCOUNT_DISABLED);
            }
        }
        emailCodeService.sendCode(email, scene);
    }

    // ========== 忘记密码 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordByEmail(AppEmailResetPasswordReqVO reqVO) {
        String email = reqVO.getEmail().toLowerCase();
        MemberEmailAccountDO account = accountMapper.selectByEmail(email);
        if (account == null) {
            throw exception(AUTH_EMAIL_NOT_REGISTERED);
        }
        if (CommonStatusEnum.isDisable(account.getStatus())) {
            throw exception(AUTH_ACCOUNT_DISABLED);
        }
        // 校验并消费验证码（失败会抛出 EMAIL_CODE_EXPIRED / EMAIL_CODE_NOT_MATCH）
        emailCodeService.useCode(email, SCENE_RESET, reqVO.getCode());

        accountMapper.updateById(MemberEmailAccountDO.builder()
                .id(account.getId())
                .password(PWD.encode(reqVO.getPassword()))
                .emailVerified(true)
                .build());
        // 同一个 member_user 的手机密码保持一致
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(account.getUserId())
                .password(passwordEncoder.encode(reqVO.getPassword()))
                .build());
        log.info("[resetPasswordByEmail] userId={} 通过邮箱重置密码", account.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordByMobile(AppMobileResetPasswordReqVO reqVO) {
        MemberUserDO user = memberUserService.getUserByMobile(reqVO.getMobile());
        if (user == null) {
            throw exception(AUTH_MOBILE_NOT_REGISTERED);
        }
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            throw exception(AUTH_ACCOUNT_DISABLED);
        }
        // yudao 内部会校验并消费短信验证码（scene = MEMBER_RESET_PASSWORD），然后更新 member_user.password
        memberUserService.resetUserPassword(AppMemberUserResetPasswordReqVO.builder()
                .mobile(reqVO.getMobile())
                .code(reqVO.getCode())
                .password(reqVO.getPassword())
                .build());

        // 如果该用户也绑定了邮箱/账号，同步更新，保证任一方式都能用新密码登录
        MemberEmailAccountDO account = accountMapper.selectByUserId(user.getId());
        if (account != null) {
            accountMapper.updateById(MemberEmailAccountDO.builder()
                    .id(account.getId())
                    .password(PWD.encode(reqVO.getPassword()))
                    .build());
        }
        log.info("[resetPasswordByMobile] userId={} 通过手机重置密码", user.getId());
    }

    // ========== 内部 ==========

    private AppAuthLoginRespVO buildToken(Long userId) {
        OAuth2AccessTokenRespDTO token = oauth2TokenApi.createAccessToken(new OAuth2AccessTokenCreateReqDTO()
                .setUserId(userId)
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT));
        return AppAuthLoginRespVO.builder()
                .userId(userId)
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresTime(token.getExpiresTime())
                .build();
    }

}
