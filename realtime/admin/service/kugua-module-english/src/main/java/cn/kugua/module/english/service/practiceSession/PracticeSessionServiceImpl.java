package cn.kugua.module.english.service.practiceSession;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.practiceSession.vo.*;
import cn.kugua.module.english.dal.dataobject.practiceSession.PracticeSessionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.practiceSession.PracticeSessionMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 练习会话 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PracticeSessionServiceImpl implements PracticeSessionService {

    @Resource
    private PracticeSessionMapper practiceSessionMapper;

    @Override
    public Long createPracticeSession(PracticeSessionSaveReqVO createReqVO) {
        // 插入
        PracticeSessionDO practiceSession = BeanUtils.toBean(createReqVO, PracticeSessionDO.class);
        practiceSessionMapper.insert(practiceSession);

        // 返回
        return practiceSession.getId();
    }

    @Override
    public void updatePracticeSession(PracticeSessionSaveReqVO updateReqVO) {
        // 校验存在
        validatePracticeSessionExists(updateReqVO.getId());
        // 更新
        PracticeSessionDO updateObj = BeanUtils.toBean(updateReqVO, PracticeSessionDO.class);
        practiceSessionMapper.updateById(updateObj);
    }

    @Override
    public void deletePracticeSession(Long id) {
        // 校验存在
        validatePracticeSessionExists(id);
        // 删除
        practiceSessionMapper.deleteById(id);
    }

    @Override
        public void deletePracticeSessionListByIds(List<Long> ids) {
        // 删除
        practiceSessionMapper.deleteByIds(ids);
        }


    private void validatePracticeSessionExists(Long id) {
        if (practiceSessionMapper.selectById(id) == null) {
            throw exception(PRACTICE_SESSION_NOT_EXISTS);
        }
    }

    @Override
    public PracticeSessionDO getPracticeSession(Long id) {
        return practiceSessionMapper.selectById(id);
    }

    @Override
    public PageResult<PracticeSessionDO> getPracticeSessionPage(PracticeSessionPageReqVO pageReqVO) {
        return practiceSessionMapper.selectPage(pageReqVO);
    }

}