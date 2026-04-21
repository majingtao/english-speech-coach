package cn.kugua.module.english.dal.mysql.quota;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.quota.vo.EscQuotaUsagePageReqVO;
import cn.kugua.module.english.dal.dataobject.quota.EscQuotaUsageDailyDO;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EscQuotaUsageDailyMapper extends BaseMapperX<EscQuotaUsageDailyDO> {

    default PageResult<EscQuotaUsageDailyDO> selectPage(EscQuotaUsagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EscQuotaUsageDailyDO>()
                .eqIfPresent(EscQuotaUsageDailyDO::getUserId, reqVO.getUserId())
                .geIfPresent(EscQuotaUsageDailyDO::getStatDate, reqVO.getDateFrom())
                .leIfPresent(EscQuotaUsageDailyDO::getStatDate, reqVO.getDateTo())
                .orderByDesc(EscQuotaUsageDailyDO::getStatDate)
                .orderByDesc(EscQuotaUsageDailyDO::getId));
    }

    /**
     * 单条 UPSERT，幂等归档。
     * <p>
     * 加 {@link InterceptorIgnore}(tenantLine="true") 是因为 MyBatis-Plus 的
     * {@code TenantLineInnerInterceptor} 会用 JSqlParser 解析 SQL 尝试注入 tenant_id，
     * 对 INSERT ... ON DUPLICATE KEY UPDATE 语法抛 UnsupportedOperationException。
     * 本表已在 application.yaml ignore-tables 中，跳过解析即可。
     */
    @InterceptorIgnore(tenantLine = "true")
    @Update("INSERT INTO esc_quota_usage_daily (user_id, stat_date, llm_used, asr_used_sec, tts_used_chars) "
            + "VALUES (#{userId}, #{statDate}, #{llm}, #{asr}, #{tts}) "
            + "ON DUPLICATE KEY UPDATE llm_used = VALUES(llm_used), asr_used_sec = VALUES(asr_used_sec), "
            + "tts_used_chars = VALUES(tts_used_chars)")
    int upsertUsage(@Param("userId") Long userId,
                    @Param("statDate") java.time.LocalDate statDate,
                    @Param("llm") int llm,
                    @Param("asr") int asr,
                    @Param("tts") int tts);

}
