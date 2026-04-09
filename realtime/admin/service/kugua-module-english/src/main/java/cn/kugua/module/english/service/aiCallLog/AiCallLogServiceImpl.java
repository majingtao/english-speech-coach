package cn.kugua.module.english.service.aiCallLog;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.aiCallLog.vo.*;
import cn.kugua.module.english.dal.dataobject.aiCallLog.AiCallLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.aiCallLog.AiCallLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * AI 调用日志 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class AiCallLogServiceImpl implements AiCallLogService {

    @Resource
    private AiCallLogMapper aiCallLogMapper;

    @Override
    public Long createAiCallLog(AiCallLogSaveReqVO createReqVO) {
        // 插入
        AiCallLogDO aiCallLog = BeanUtils.toBean(createReqVO, AiCallLogDO.class);
        aiCallLogMapper.insert(aiCallLog);

        // 返回
        return aiCallLog.getId();
    }

    @Override
    public void updateAiCallLog(AiCallLogSaveReqVO updateReqVO) {
        // 校验存在
        validateAiCallLogExists(updateReqVO.getId());
        // 更新
        AiCallLogDO updateObj = BeanUtils.toBean(updateReqVO, AiCallLogDO.class);
        aiCallLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteAiCallLog(Long id) {
        // 校验存在
        validateAiCallLogExists(id);
        // 删除
        aiCallLogMapper.deleteById(id);
    }

    @Override
        public void deleteAiCallLogListByIds(List<Long> ids) {
        // 删除
        aiCallLogMapper.deleteByIds(ids);
        }


    private void validateAiCallLogExists(Long id) {
        if (aiCallLogMapper.selectById(id) == null) {
            throw exception(AI_CALL_LOG_NOT_EXISTS);
        }
    }

    @Override
    public AiCallLogDO getAiCallLog(Long id) {
        return aiCallLogMapper.selectById(id);
    }

    @Override
    public PageResult<AiCallLogDO> getAiCallLogPage(AiCallLogPageReqVO pageReqVO) {
        return aiCallLogMapper.selectPage(pageReqVO);
    }

}