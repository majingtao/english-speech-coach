package cn.kugua.module.english.service.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 邮件发送配置。
 * <p>
 * 当 kugua.mail.resend.api-key 存在时自动启用 Resend 主通道。
 * 同时有阿里云配置时，启用 Failover 主备切换；仅有 Resend 时单通道。
 * 如果均未配置，回退到 {@link StubMailSender}（仅日志）。
 * <p>
 * application.yaml 示例：
 * <pre>
 * kugua:
 *   mail:
 *     from-email: noreply@yourdomain.com
 *     resend:
 *       api-key: re_xxxxxxxxx
 *     aliyun:
 *       access-key-id: LTAI...
 *       access-key-secret: xxxxx
 *       account-name: noreply@yourdomain.com
 * </pre>
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kugua.mail")
@Data
public class MailSenderConfiguration {

    private String fromEmail;
    private ResendConfig resend;
    private AliyunConfig aliyun;

    @Data
    public static class ResendConfig {
        private String apiKey;
    }

    @Data
    public static class AliyunConfig {
        private String accessKeyId;
        private String accessKeySecret;
        private String accountName;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "kugua.mail.resend", name = "api-key")
    public MailSender mailSender() {
        ResendMailSender resendSender = new ResendMailSender(resend.getApiKey(), fromEmail);

        if (aliyun != null && aliyun.getAccessKeyId() != null) {
            AliyunDirectMailSender aliyunSender = new AliyunDirectMailSender(
                    aliyun.getAccessKeyId(), aliyun.getAccessKeySecret(), aliyun.getAccountName());
            log.info("[MailSender] 已启用 Resend(主) + 阿里云 DirectMail(备) 双通道");
            return new FailoverMailSender(resendSender, aliyunSender);
        }

        log.info("[MailSender] 已启用 Resend 单通道");
        return resendSender;
    }

}
