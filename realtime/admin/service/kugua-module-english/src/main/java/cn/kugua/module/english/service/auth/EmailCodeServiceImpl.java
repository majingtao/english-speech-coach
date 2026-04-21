package cn.kugua.module.english.service.auth;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

@Slf4j
@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    /** 验证码 6 位数字，5 分钟有效 */
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    /** 同一邮箱发送最小间隔 60s */
    private static final Duration SEND_GAP = Duration.ofSeconds(60);

    @Resource
    private StringRedisTemplate redis;

    @Resource
    private MailSender mailSender;

    private static String codeKey(String email, String scene) {
        return "kugua:email:code:" + scene + ":" + email.toLowerCase();
    }

    private static String gapKey(String email, String scene) {
        return "kugua:email:gap:" + scene + ":" + email.toLowerCase();
    }

    @Override
    public void sendCode(String email, String scene) {
        String gap = gapKey(email, scene);
        Boolean ok = redis.opsForValue().setIfAbsent(gap, "1", SEND_GAP);
        if (Boolean.FALSE.equals(ok)) {
            throw exception(EMAIL_CODE_SEND_TOO_FAST);
        }
        String code = RandomUtil.randomNumbers(6);
        redis.opsForValue().set(codeKey(email, scene), code, CODE_TTL);
        mailSender.sendVerificationCode(email, code, scene);
    }

    @Override
    public void useCode(String email, String scene, String code) {
        String key = codeKey(email, scene);
        String stored = redis.opsForValue().get(key);
        if (stored == null) {
            throw exception(EMAIL_CODE_EXPIRED);
        }
        if (!stored.equals(code)) {
            throw exception(EMAIL_CODE_NOT_MATCH);
        }
        redis.delete(key);
    }

}
