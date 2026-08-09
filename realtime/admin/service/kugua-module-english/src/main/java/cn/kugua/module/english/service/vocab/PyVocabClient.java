package cn.kugua.module.english.service.vocab;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
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
