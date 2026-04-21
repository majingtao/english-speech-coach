package cn.kugua.module.english.service.quota;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.quota.vo.EscQuotaDefaultUpdateReqVO;
import cn.kugua.module.english.controller.admin.quota.vo.EscQuotaUsagePageReqVO;
import cn.kugua.module.english.controller.admin.quota.vo.EscUserQuotaPageReqVO;
import cn.kugua.module.english.controller.admin.quota.vo.EscUserQuotaSaveReqVO;
import cn.kugua.module.english.controller.app.quota.vo.EscQuotaConsumeRespVO;
import cn.kugua.module.english.controller.app.quota.vo.EscQuotaMeRespVO;
import cn.kugua.module.english.dal.dataobject.quota.EscQuotaDefaultDO;
import cn.kugua.module.english.dal.dataobject.quota.EscQuotaUsageDailyDO;
import cn.kugua.module.english.dal.dataobject.quota.EscUserQuotaDO;
import cn.kugua.module.english.dal.mysql.quota.EscQuotaDefaultMapper;
import cn.kugua.module.english.dal.mysql.quota.EscQuotaUsageDailyMapper;
import cn.kugua.module.english.dal.mysql.quota.EscUserQuotaMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

@Slf4j
@Service
public class EscQuotaServiceImpl implements EscQuotaService {

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    /** Redis 当日 key 保留 48h，避免跨日边界丢失 */
    private static final Duration KEY_TTL = Duration.ofHours(48);

    @Resource
    private EscQuotaDefaultMapper defaultMapper;
    @Resource
    private EscUserQuotaMapper userQuotaMapper;
    @Resource
    private EscQuotaUsageDailyMapper usageMapper;
    @Resource
    private StringRedisTemplate redis;

    // ========== 全局默认 ==========

    @Override
    public EscQuotaDefaultDO getDefault() {
        EscQuotaDefaultDO d = defaultMapper.selectById(EscQuotaDefaultDO.SINGLETON_ID);
        if (d == null) {
            throw exception(ESC_QUOTA_DEFAULT_NOT_EXISTS);
        }
        return d;
    }

    @Override
    public void updateDefault(EscQuotaDefaultUpdateReqVO reqVO) {
        EscQuotaDefaultDO d = defaultMapper.selectById(EscQuotaDefaultDO.SINGLETON_ID);
        if (d == null) {
            d = EscQuotaDefaultDO.builder()
                    .id(EscQuotaDefaultDO.SINGLETON_ID)
                    .llmDaily(reqVO.getLlmDaily())
                    .asrDailySec(reqVO.getAsrDailySec())
                    .ttsDailyChars(reqVO.getTtsDailyChars())
                    .build();
            defaultMapper.insert(d);
            return;
        }
        d.setLlmDaily(reqVO.getLlmDaily());
        d.setAsrDailySec(reqVO.getAsrDailySec());
        d.setTtsDailyChars(reqVO.getTtsDailyChars());
        defaultMapper.updateById(d);
    }

    // ========== 单用户覆盖 ==========

    @Override
    public PageResult<EscUserQuotaDO> getUserQuotaPage(EscUserQuotaPageReqVO reqVO) {
        // nickname 暂由 controller 层做用户名 → userId 解析
        return userQuotaMapper.selectPage(reqVO);
    }

    @Override
    public EscUserQuotaDO getUserQuota(Long userId) {
        return userQuotaMapper.selectByUserId(userId);
    }

    @Override
    public void saveUserQuota(EscUserQuotaSaveReqVO reqVO) {
        EscUserQuotaDO exists = userQuotaMapper.selectByUserId(reqVO.getUserId());
        if (exists == null) {
            EscUserQuotaDO row = EscUserQuotaDO.builder()
                    .userId(reqVO.getUserId())
                    .llmDaily(reqVO.getLlmDaily())
                    .asrDailySec(reqVO.getAsrDailySec())
                    .ttsDailyChars(reqVO.getTtsDailyChars())
                    .enabled(reqVO.getEnabled())
                    .remark(reqVO.getRemark())
                    .build();
            userQuotaMapper.insert(row);
            return;
        }
        exists.setLlmDaily(reqVO.getLlmDaily());
        exists.setAsrDailySec(reqVO.getAsrDailySec());
        exists.setTtsDailyChars(reqVO.getTtsDailyChars());
        exists.setEnabled(reqVO.getEnabled());
        exists.setRemark(reqVO.getRemark());
        userQuotaMapper.updateById(exists);
    }

    @Override
    public void deleteUserQuota(Long userId) {
        EscUserQuotaDO exists = userQuotaMapper.selectByUserId(userId);
        if (exists == null) {
            throw exception(ESC_USER_QUOTA_NOT_EXISTS);
        }
        userQuotaMapper.deleteById(exists.getId());
    }

    // ========== 运行时 ==========

    @Override
    public EscQuotaMeRespVO getMyQuota(Long userId) {
        EffectiveQuota eq = resolveQuota(userId);
        String day = LocalDate.now().format(DAY_FMT);
        int llmUsed = readUsed("llm", userId, day);
        int asrUsed = readUsed("asr", userId, day);
        int ttsUsed = readUsed("tts", userId, day);
        return EscQuotaMeRespVO.builder()
                .enabled(eq.enabled)
                .llmDaily(eq.llmDaily)
                .asrDailySec(eq.asrDailySec)
                .ttsDailyChars(eq.ttsDailyChars)
                .llmUsed(llmUsed)
                .asrUsedSec(asrUsed)
                .ttsUsedChars(ttsUsed)
                .llmRemaining(Math.max(eq.llmDaily - llmUsed, 0))
                .asrRemainingSec(Math.max(eq.asrDailySec - asrUsed, 0))
                .ttsRemainingChars(Math.max(eq.ttsDailyChars - ttsUsed, 0))
                .build();
    }

    @Override
    public EscQuotaConsumeRespVO consume(Long userId, String resource, int amount) {
        if (!isValidResource(resource)) {
            throw exception(ESC_QUOTA_RESOURCE_INVALID);
        }
        EffectiveQuota eq = resolveQuota(userId);
        if (Boolean.FALSE.equals(eq.enabled)) {
            throw exception(ESC_QUOTA_USER_DISABLED);
        }

        int limit = switch (resource) {
            case "llm" -> eq.llmDaily;
            case "asr" -> eq.asrDailySec;
            case "tts" -> eq.ttsDailyChars;
            default -> 0;
        };

        String day = LocalDate.now().format(DAY_FMT);
        String key = usedKey(resource, userId, day);
        Long newUsed = redis.opsForValue().increment(key, amount);
        if (newUsed != null && newUsed == amount) {
            // 首次设置，打上 TTL
            redis.expire(key, KEY_TTL);
        }
        long used = newUsed == null ? amount : newUsed;

        if (used > limit) {
            // 回滚本次扣减，避免"卡额度"
            redis.opsForValue().decrement(key, amount);
            throw exception(switch (resource) {
                case "llm" -> ESC_QUOTA_LLM_EXCEEDED;
                case "asr" -> ESC_QUOTA_ASR_EXCEEDED;
                default    -> ESC_QUOTA_TTS_EXCEEDED;
            });
        }

        return EscQuotaConsumeRespVO.builder()
                .allowed(true)
                .remaining((int) Math.max(limit - used, 0))
                .dailyLimit(limit)
                .build();
    }

    // ========== Phase 3：归档 / 报表 ==========

    @Override
    public int archiveYesterdayUsage() {
        return archiveUsageForDate(LocalDate.now().minusDays(1));
    }

    @Override
    public int archiveUsageForDate(LocalDate date) {
        String day = date.format(DAY_FMT);
        // 扫描三类资源 key，聚合到 userId -> (llm, asr, tts)
        java.util.Map<Long, int[]> agg = new java.util.HashMap<>();
        int totalKeys = 0;
        for (String res : new String[]{"llm", "asr", "tts"}) {
            String pattern = "esc:used:" + res + ":*:" + day;
            java.util.Set<String> keys = redis.keys(pattern);
            int matched = keys == null ? 0 : keys.size();
            totalKeys += matched;
            log.info("[quota-archive] scan pattern={} matched={}", pattern, matched);
            if (keys == null || keys.isEmpty()) continue;
            java.util.List<String> keyList = new java.util.ArrayList<>(keys);
            java.util.List<String> values = redis.opsForValue().multiGet(keyList);
            if (values == null) continue;
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                String val = values.get(i);
                if (val == null) continue;
                // key: esc:used:{res}:{userId}:{day}
                String[] parts = key.split(":");
                if (parts.length < 5) continue;
                long userId;
                int used;
                try {
                    userId = Long.parseLong(parts[3]);
                    used = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    log.warn("[quota-archive] skip malformed key={} val={}", key, val);
                    continue;
                }
                int[] row = agg.computeIfAbsent(userId, k -> new int[3]);
                switch (res) {
                    case "llm" -> row[0] = used;
                    case "asr" -> row[1] = used;
                    case "tts" -> row[2] = used;
                }
            }
        }
        for (java.util.Map.Entry<Long, int[]> e : agg.entrySet()) {
            int[] v = e.getValue();
            usageMapper.upsertUsage(e.getKey(), date, v[0], v[1], v[2]);
        }
        log.info("[quota-archive] date={} totalKeys={} users={} done", date, totalKeys, agg.size());
        return agg.size();
    }

    @Override
    public PageResult<EscQuotaUsageDailyDO> getUsagePage(EscQuotaUsagePageReqVO reqVO) {
        return usageMapper.selectPage(reqVO);
    }

    @Override
    public void resetTodayUsage(Long userId) {
        String day = LocalDate.now().format(DAY_FMT);
        redis.delete(java.util.List.of(
                usedKey("llm", userId, day),
                usedKey("asr", userId, day),
                usedKey("tts", userId, day)
        ));
    }

    // ========== 内部 ==========

    private static boolean isValidResource(String r) {
        return "llm".equals(r) || "asr".equals(r) || "tts".equals(r);
    }

    private static String usedKey(String resource, Long userId, String day) {
        return "esc:used:" + resource + ":" + userId + ":" + day;
    }

    private int readUsed(String resource, Long userId, String day) {
        String v = redis.opsForValue().get(usedKey(resource, userId, day));
        if (v == null) return 0;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** 合并：用户覆盖 > 默认。覆盖行不存在则视为 enabled=true，三项全走默认。 */
    private EffectiveQuota resolveQuota(Long userId) {
        EscQuotaDefaultDO d = getDefault();
        EscUserQuotaDO u = userQuotaMapper.selectByUserId(userId);
        EffectiveQuota eq = new EffectiveQuota();
        if (u == null) {
            eq.enabled = true;
            eq.llmDaily = d.getLlmDaily();
            eq.asrDailySec = d.getAsrDailySec();
            eq.ttsDailyChars = d.getTtsDailyChars();
            return eq;
        }
        eq.enabled = u.getEnabled() == null ? true : u.getEnabled();
        eq.llmDaily = u.getLlmDaily() != null ? u.getLlmDaily() : d.getLlmDaily();
        eq.asrDailySec = u.getAsrDailySec() != null ? u.getAsrDailySec() : d.getAsrDailySec();
        eq.ttsDailyChars = u.getTtsDailyChars() != null ? u.getTtsDailyChars() : d.getTtsDailyChars();
        return eq;
    }

    private static class EffectiveQuota {
        Boolean enabled;
        Integer llmDaily;
        Integer asrDailySec;
        Integer ttsDailyChars;
    }

}
