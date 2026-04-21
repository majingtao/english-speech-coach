package cn.kugua.module.english.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

/**
 * 占位实现：仅打日志，不真正发送。
 * 当 task c) 接入 Resend / 阿里云后，会通过 @Primary 覆盖此 Bean。
 */
@Slf4j
@Configuration
public class StubMailSender {

    @Bean
    @ConditionalOnMissingBean(MailSender.class)
    public MailSender stubMailSender() {
        return (to, code, scene) -> log.warn(
                "[StubMailSender] 未配置真实邮件通道，请在 task c) 接入 Resend/阿里云。to={}, scene={}, code={}",
                to, scene, code);
    }

}
