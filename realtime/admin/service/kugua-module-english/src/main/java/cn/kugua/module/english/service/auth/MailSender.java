package cn.kugua.module.english.service.auth;

/**
 * 邮件发送抽象。
 * <p>
 * 当前提供 {@link StubMailSender} 占位实现：仅打印日志、不真正发件，便于本地联调。
 * task c) 中将引入 Resend 主通道 + 阿里云 DirectMail 备通道，并在主通道异常时熔断降级。
 */
public interface MailSender {

    /**
     * 发送验证码邮件
     *
     * @param to      收件邮箱
     * @param code    验证码（明文）
     * @param scene   场景（register / login / reset），用于挑选模板
     */
    void sendVerificationCode(String to, String code, String scene);

}
