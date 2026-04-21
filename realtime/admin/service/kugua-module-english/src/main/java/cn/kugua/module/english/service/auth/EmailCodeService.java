package cn.kugua.module.english.service.auth;

/**
 * 邮箱验证码服务
 */
public interface EmailCodeService {

    /** 注册场景 */
    String SCENE_REGISTER = "register";
    /** 登录场景 */
    String SCENE_LOGIN = "login";
    /** 重置密码场景 */
    String SCENE_RESET = "reset";

    /**
     * 发送验证码到指定邮箱（含发送频控 + Redis 写入）。
     */
    void sendCode(String email, String scene);

    /**
     * 校验并消费验证码。校验成功后立即删除，防止重放。
     *
     * @throws cn.iocoder.yudao.framework.common.exception.ServiceException 校验失败
     */
    void useCode(String email, String scene, String code);

}
