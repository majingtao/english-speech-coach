package cn.kugua.module.english.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云邮件推送（DirectMail）发送实现。
 * <p>
 * 使用 SingleSendMail 接口：
 * <a href="https://help.aliyun.com/document_detail/29444.html">文档</a>
 * <p>
 * 需要配置 kugua.mail.aliyun.access-key-id / access-key-secret / account-name。
 */
@Slf4j
public class AliyunDirectMailSender implements MailSender {

    private static final String API_URL = "https://dm.aliyuncs.com/";

    private final String accessKeyId;
    private final String accessKeySecret;
    private final String accountName;
    private final RestTemplate restTemplate = new RestTemplate();

    public AliyunDirectMailSender(String accessKeyId, String accessKeySecret, String accountName) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.accountName = accountName;
    }

    @Override
    public void sendVerificationCode(String to, String code, String scene) {
        String subject = buildSubject(scene);
        String body = buildBody(code, scene);

        Map<String, String> params = new TreeMap<>();
        params.put("Action", "SingleSendMail");
        params.put("AccountName", accountName);
        params.put("ReplyToAddress", "false");
        params.put("AddressType", "1");
        params.put("ToAddress", to);
        params.put("Subject", subject);
        params.put("HtmlBody", body);
        params.put("Format", "JSON");
        params.put("Version", "2015-11-23");
        params.put("AccessKeyId", accessKeyId);
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        params.put("Timestamp", sdf.format(new Date()));

        String signature = sign(params);
        params.put("Signature", signature);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        params.forEach(form::add);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    API_URL, HttpMethod.POST, new HttpEntity<>(form, headers), String.class);
            log.info("[AliyunDM] 发送验证码邮件成功: to={}, scene={}, status={}", to, scene, resp.getStatusCode());
        }
        catch (Exception e) {
            log.error("[AliyunDM] 发送验证码邮件失败: to={}, scene={}", to, scene, e);
            throw e;
        }
    }

    private String sign(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> e : params.entrySet()) {
                if (!sb.isEmpty()) sb.append('&');
                sb.append(percentEncode(e.getKey())).append('=').append(percentEncode(e.getValue()));
            }
            String stringToSign = "POST&" + percentEncode("/") + "&" + percentEncode(sb.toString());
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec((accessKeySecret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] hash = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }
        catch (Exception e) {
            throw new RuntimeException("阿里云签名计算失败", e);
        }
    }

    private static String percentEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    private static String buildSubject(String scene) {
        return switch (scene) {
            case "register" -> "English Speech Coach - 注册验证码";
            case "login" -> "English Speech Coach - 登录验证码";
            case "reset" -> "English Speech Coach - 重置密码验证码";
            default -> "English Speech Coach - 验证码";
        };
    }

    private static String buildBody(String code, String scene) {
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
