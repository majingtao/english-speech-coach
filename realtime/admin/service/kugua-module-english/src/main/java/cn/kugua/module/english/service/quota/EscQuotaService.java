package cn.kugua.module.english.service.quota;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.quota.vo.EscQuotaDefaultUpdateReqVO;
import cn.kugua.module.english.controller.admin.quota.vo.EscUserQuotaPageReqVO;
import cn.kugua.module.english.controller.admin.quota.vo.EscUserQuotaSaveReqVO;
import cn.kugua.module.english.controller.admin.quota.vo.EscQuotaUsagePageReqVO;
import cn.kugua.module.english.controller.app.quota.vo.EscQuotaConsumeRespVO;
import cn.kugua.module.english.controller.app.quota.vo.EscQuotaMeRespVO;
import cn.kugua.module.english.dal.dataobject.quota.EscQuotaDefaultDO;
import cn.kugua.module.english.dal.dataobject.quota.EscQuotaUsageDailyDO;
import cn.kugua.module.english.dal.dataobject.quota.EscUserQuotaDO;

import java.time.LocalDate;

public interface EscQuotaService {

    // ---- 全局默认 ----
    EscQuotaDefaultDO getDefault();

    void updateDefault(EscQuotaDefaultUpdateReqVO reqVO);

    // ---- 单用户覆盖 ----
    PageResult<EscUserQuotaDO> getUserQuotaPage(EscUserQuotaPageReqVO reqVO);

    EscUserQuotaDO getUserQuota(Long userId);

    /** 以 userId 为 UK，存在则更新，不存在则插入 */
    void saveUserQuota(EscUserQuotaSaveReqVO reqVO);

    void deleteUserQuota(Long userId);

    // ---- 运行时：查 + 扣 ----

    /** 生效配额（null 字段回落默认）+ 今日已用 */
    EscQuotaMeRespVO getMyQuota(Long userId);

    /**
     * 原子扣减并校验。
     * - 账号冻结 / 资源类型非法 直接抛业务异常；
     * - 超限抛对应 ESC_QUOTA_{LLM|ASR|TTS}_EXCEEDED；
     * - 正常返回扣减后剩余。
     */
    EscQuotaConsumeRespVO consume(Long userId, String resource, int amount);

    /**
     * 清零指定用户今日的用量计数（LLM/ASR/TTS 三把 Redis key 全删）。
     * 不影响 esc_user_quota 的每日上限覆盖行，只把"已用"归零。
     */
    void resetTodayUsage(Long userId);

    // ---- Phase 3 归档/报表 ----

    /**
     * 扫描 Redis 昨日 esc:used:* keys，按 userId 汇总写入 esc_quota_usage_daily。
     * 幂等（UPSERT）。返回处理的用户数量，用于定时任务日志。
     */
    int archiveYesterdayUsage();

    /** 指定某一天强制归档（用于补跑 / 测试） */
    int archiveUsageForDate(LocalDate date);

    /** 分页查询归档数据 */
    PageResult<EscQuotaUsageDailyDO> getUsagePage(EscQuotaUsagePageReqVO reqVO);

}
