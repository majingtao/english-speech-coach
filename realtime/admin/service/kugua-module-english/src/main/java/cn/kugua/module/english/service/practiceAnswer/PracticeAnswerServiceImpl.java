package cn.kugua.module.english.service.practiceAnswer;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.practiceAnswer.vo.*;
import cn.kugua.module.english.dal.dataobject.practiceAnswer.PracticeAnswerDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.practiceAnswer.PracticeAnswerMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 练习单题作答 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PracticeAnswerServiceImpl implements PracticeAnswerService {

    @Resource
    private PracticeAnswerMapper practiceAnswerMapper;

    @Override
    public Long createPracticeAnswer(PracticeAnswerSaveReqVO createReqVO) {
        // 插入
        PracticeAnswerDO practiceAnswer = BeanUtils.toBean(createReqVO, PracticeAnswerDO.class);
        practiceAnswerMapper.insert(practiceAnswer);

        // 返回
        return practiceAnswer.getId();
    }

    @Override
    public void updatePracticeAnswer(PracticeAnswerSaveReqVO updateReqVO) {
        // 校验存在
        validatePracticeAnswerExists(updateReqVO.getId());
        // 更新
        PracticeAnswerDO updateObj = BeanUtils.toBean(updateReqVO, PracticeAnswerDO.class);
        practiceAnswerMapper.updateById(updateObj);
    }

    @Override
    public void deletePracticeAnswer(Long id) {
        // 校验存在
        validatePracticeAnswerExists(id);
        // 删除
        practiceAnswerMapper.deleteById(id);
    }

    @Override
        public void deletePracticeAnswerListByIds(List<Long> ids) {
        // 删除
        practiceAnswerMapper.deleteByIds(ids);
        }


    private void validatePracticeAnswerExists(Long id) {
        if (practiceAnswerMapper.selectById(id) == null) {
            throw exception(PRACTICE_ANSWER_NOT_EXISTS);
        }
    }

    @Override
    public PracticeAnswerDO getPracticeAnswer(Long id) {
        return practiceAnswerMapper.selectById(id);
    }

    @Override
    public PageResult<PracticeAnswerDO> getPracticeAnswerPage(PracticeAnswerPageReqVO pageReqVO) {
        return practiceAnswerMapper.selectPage(pageReqVO);
    }

}