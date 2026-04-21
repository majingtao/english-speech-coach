package cn.kugua.module.english.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Resend（https://resend.com）邮件发送实现。
 * <p>
 * 需要在 application.yaml 中配置 kugua.mail.resend.api-key 和 kugua.mail.from-email。
 */
@Slf4j
public class ResendMailSender implements MailSender {

    private static final String API_URL = "https://api.resend.com/emails";

    private final String apiKey;
    private final String fromEmail;
    private final RestTemplate restTemplate = new RestTemplate();

    public ResendMailSender(String apiKey, String fromEmail) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendVerificationCode(String to, String code, String scene) {
        String subject = buildSubject(scene);
        String html = buildHtml(code, scene);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", List.of(to),
                "subject", subject,
                "html", html
        );
        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    API_URL, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
            log.info("[Resend] 发送验证码邮件成功: to={}, scene={}, status={}", to, scene, resp.getStatusCode());
        }
        catch (Exception e) {
            log.error("[Resend] 发送验证码邮件失败: to={}, scene={}", to, scene, e);
            throw e;
        }
    }

    private static String buildSubject(String scene) {
        return switch (scene) {
            case "register" -> "English Speech Coach - 注册验证码";
            case "login" -> "English Speech Coach - 登录验证码";
            case "reset" -> "English Speech Coach - 重置密码验证码";
            default -> "English Speech Coach - 验证码";
        };
    }

    private static String buildHtml(String code, String scene) {
        String action = switch (scene) {
            case "register" -> "注册";
            case "login" -> "登录";
            case "reset" -> "重置密码";
            default -> "操作";
        };
        return "<div style='font-family:sans-serif;max-width:480px;margin:auto'>"
                + "<h2 style='color:#1a3d74'>English Speech Coach</h2>"
                + "<p>您正在进行<b>" + action + "</b>操作，验证码为：</p>"
                + "<p style='font-size:28px;letter-spacing:6px;font-weight:700;color:#1989fa'>" + code + "</p>"
                + "<p style='color:#999;font-size:12px'>验证码 5 分钟内有效，请勿泄露给他人。</p>"
                + "</div>";
    }

}
