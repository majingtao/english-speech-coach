package cn.kugua.module.english.dal.mysql.aiCallLog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.aiCallLog.AiCallLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.aiCallLog.vo.*;

/**
 * AI 调用日志 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface AiCallLogMapper extends BaseMapperX<AiCallLogDO> {

    default PageResult<AiCallLogDO> selectPage(AiCallLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiCallLogDO>()
                .eqIfPresent(AiCallLogDO::getStudentId, reqVO.getStudentId())
                .eqIfPresent(AiCallLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiCallLogDO::getServiceType, reqVO.getServiceType())
                .eqIfPresent(AiCallLogDO::getEngine, reqVO.getEngine())
                .eqIfPresent(AiCallLogDO::getEndpoint, reqVO.getEndpoint())
                .eqIfPresent(AiCallLogDO::getRequestSize, reqVO.getRequestSize())
                .eqIfPresent(AiCallLogDO::getResponseSize, reqVO.getResponseSize())
                .eqIfPresent(AiCallLogDO::getDurationMs, reqVO.getDurationMs())
                .eqIfPresent(AiCallLogDO::getStatusCode, reqVO.getStatusCode())
                .eqIfPresent(AiCallLogDO::getSuccess, reqVO.getSuccess())
                .eqIfPresent(AiCallLogDO::getErrorMessage, reqVO.getErrorMessage())
                .eqIfPresent(AiCallLogDO::getClientIp, reqVO.getClientIp())
                .betweenIfPresent(AiCallLogDO::getCallDate, reqVO.getCallDate())
                .betweenIfPresent(AiCallLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AiCallLogDO::getId));
    }

}