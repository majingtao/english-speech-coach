package cn.kugua.module.english.service.vocab;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用 Python 端（realtime/web/server.py）的词汇生成/IPA 接口。
 * <p>
 * Python 不可达时所有方法返回 null，让上层决定是否降级。
 */
@Component
@Slf4j
public class PyVocabClient {

    @Value("${english.py.base-url:https://127.0.0.1:8443}")
    private String baseUrl;

    private HttpClient http;

    @PostConstruct
    public void init() {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3));
        // Python 服务（realtime/web/server.py）只监听 HTTPS 且使用自签名证书，
        // JDK 默认信任库不认，调用会抛 PKIX / SSLHandshakeException，所有方法都返回 null。
        // 仅当 base-url 指向本机回环地址时放开证书校验（与 Next.js /py 代理 rejectUnauthorized=false 一致）。
        if (isLoopbackHttps(baseUrl)) {
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[]{new LoopbackTrustManager()}, new SecureRandom());
                builder.sslContext(ctx);
                log.info("[vocab] py client trusts self-signed cert for {}", baseUrl);
            } catch (Exception e) {
                log.warn("[vocab] init loopback ssl context failed: {}", e.getMessage());
            }
        }
        this.http = builder.build();
    }

    private static boolean isLoopbackHttps(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            return "https".equalsIgnoreCase(uri.getScheme()) && host != null
                    && (host.equals("127.0.0.1") || host.equalsIgnoreCase("localhost") || host.equals("::1") || host.equals("[::1]"));
        } catch (Exception e) {
            return false;
        }
    }

    /** 仅用于本机回环地址的 Python 服务：不校验证书链和主机名。 */
    private static final class LoopbackTrustManager extends X509ExtendedTrustManager {
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType) { }
        @Override public void checkServerTrusted(X509Certificate[] chain, String authType) { }
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) { }
        @Override public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) { }
        @Override public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) { }
        @Override public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) { }
        @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * POST /py/vocab/generate
     * @return JSON 字符串：{definition_cn, definition_en, ipa, examples, gen_at, model_used}；失败返回 null
     */
    public String generateContent(String word, String level, String posHint, List<String> themeHints) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("word", word);
            body.put("level", level);
            body.put("pos_hint", posHint);
            body.put("theme_hints", themeHints);
            String payload = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/py/vocab/generate"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return resp.body();
            }
            log.warn("[vocab] py generate returned {}: {}", resp.statusCode(), resp.body());
        } catch (Exception e) {
            log.warn("[vocab] py generate failed: {}", e.getMessage());
        }
        return null;
    }

    /**
     * POST /py/vocab/tts — 生成单词发音（MP3 字节）
     * @param accent "uk" / "us"
     * @return MP3 字节数组；失败返回 null
     */
    public byte[] generateAudio(String word, String accent) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("word", word);
            body.put("accent", accent);
            String payload = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/py/vocab/tts"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<byte[]> resp = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return resp.body();
            }
            log.warn("[vocab] py tts returned {} for word={}", resp.statusCode(), word);
        } catch (Exception e) {
            log.warn("[vocab] py tts failed for word={}: {}", word, e.getMessage());
        }
        return null;
    }

    /**
     * POST /py/grade_sentence
     * @return JSON 字符串 {ok, fb, cn, revised}；失败返回 null
     */
    public String gradeSentence(String word, String sentence, String level) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("word", word);
            body.put("sentence", sentence);
            body.put("level", level);
            String payload = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/py/grade_sentence"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return resp.body();
            }
            log.warn("[vocab] py grade_sentence returned {}", resp.statusCode());
        } catch (Exception e) {
            log.warn("[vocab] py grade_sentence failed: {}", e.getMessage());
        }
        return null;
    }

}
