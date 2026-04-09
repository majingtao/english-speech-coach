package cn.iocoder.yudao.module.english.service.partPersonalQuestion;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.english.controller.admin.partPersonalQuestion.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partPersonalQuestion.PartPersonalQuestionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.english.dal.mysql.partPersonalQuestion.PartPersonalQuestionMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.english.enums.ErrorCodeConstants.*;

/**
 * 个人问答 - 问题 Service 实现类
 *
 * @author 苦瓜科技
 */
@Service
@Validated
public class PartPersonalQuestionServiceImpl implements PartPersonalQuestionService {

    @Resource
    private PartPersonalQuestionMapper partPersonalQuestionMapper;

    @Override
    public Long createPartPersonalQuestion(PartPersonalQuestionSaveReqVO createReqVO) {
        // 插入
        PartPersonalQuestionDO partPersonalQuestion = BeanUtils.toBean(createReqVO, PartPersonalQuestionDO.class);
        partPersonalQuestionMapper.insert(partPersonalQuestion);

        // 返回
        return partPersonalQuestion.getId();
    }

    @Override
    public void updatePartPersonalQuestion(PartPersonalQuestionSaveReqVO updateReqVO) {
        // 校验存在
        validatePartPersonalQuestionExists(updateReqVO.getId());
        // 更新
        PartPersonalQuestionDO updateObj = BeanUtils.toBean(updateReqVO, PartPersonalQuestionDO.class);
        partPersonalQuestionMapper.updateById(updateObj);
    }

    @Override
    public void deletePartPersonalQuestion(Long id) {
        // 校验存在
        validatePartPersonalQuestionExists(id);
        // 删除
        partPersonalQuestionMapper.deleteById(id);
    }

    @Override
        public void deletePartPersonalQuestionListByIds(List<Long> ids) {
        // 删除
        partPersonalQuestionMapper.deleteByIds(ids);
        }


    private void validatePartPersonalQuestionExists(Long id) {
        if (partPersonalQuestionMapper.selectById(id) == null) {
            throw exception(PART_PERSONAL_QUESTION_NOT_EXISTS);
        }
    }

    @Override
    public PartPersonalQuestionDO getPartPersonalQuestion(Long id) {
        return partPersonalQuestionMapper.selectById(id);
    }

    @Override
    public PageResult<PartPersonalQuestionDO> getPartPersonalQuestionPage(PartPersonalQuestionPageReqVO pageReqVO) {
        return partPersonalQuestionMapper.selectPage(pageReqVO);
    }

}