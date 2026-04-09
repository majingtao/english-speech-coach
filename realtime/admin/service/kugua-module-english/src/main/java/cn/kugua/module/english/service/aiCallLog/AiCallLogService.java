package cn.kugua.module.english.service.aiCallLog;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.aiCallLog.vo.*;
import cn.kugua.module.english.dal.dataobject.aiCallLog.AiCallLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * AI 调用日志 Service 接口
 *
 * @author 苦瓜
 */
public interface AiCallLogService {

    /**
     * 创建AI 调用日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAiCallLog(@Valid AiCallLogSaveReqVO createReqVO);

    /**
     * 更新AI 调用日志
     *
     * @param updateReqVO 更新信息
     */
    void updateAiCallLog(@Valid AiCallLogSaveReqVO updateReqVO);

    /**
     * 删除AI 调用日志
     *
     * @param id 编号
     */
    void deleteAiCallLog(Long id);

    /**
    * 批量删除AI 调用日志
    *
    * @param ids 编号
    */
    void deleteAiCallLogListByIds(List<Long> ids);

    /**
     * 获得AI 调用日志
     *
     * @param id 编号
     * @return AI 调用日志
     */
    AiCallLogDO getAiCallLog(Long id);

    /**
     * 获得AI 调用日志分页
     *
     * @param pageReqVO 分页查询
     * @return AI 调用日志分页
     */
    PageResult<AiCallLogDO> getAiCallLogPage(AiCallLogPageReqVO pageReqVO);

}