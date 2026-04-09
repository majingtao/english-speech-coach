package cn.kugua.module.english.service.examLevel;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.examLevel.vo.*;
import cn.kugua.module.english.dal.dataobject.examLevel.ExamLevelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.examLevel.ExamLevelMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 考试级别字典 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class ExamLevelServiceImpl implements ExamLevelService {

    @Resource
    private ExamLevelMapper examLevelMapper;

    @Override
    public Long createExamLevel(ExamLevelSaveReqVO createReqVO) {
        // 插入
        ExamLevelDO examLevel = BeanUtils.toBean(createReqVO, ExamLevelDO.class);
        examLevelMapper.insert(examLevel);

        // 返回
        return examLevel.getId();
    }

    @Override
    public void updateExamLevel(ExamLevelSaveReqVO updateReqVO) {
        // 校验存在
        validateExamLevelExists(updateReqVO.getId());
        // 更新
        ExamLevelDO updateObj = BeanUtils.toBean(updateReqVO, ExamLevelDO.class);
        examLevelMapper.updateById(updateObj);
    }

    @Override
    public void deleteExamLevel(Long id) {
        // 校验存在
        validateExamLevelExists(id);
        // 删除
        examLevelMapper.deleteById(id);
    }

    @Override
        public void deleteExamLevelListByIds(List<Long> ids) {
        // 删除
        examLevelMapper.deleteByIds(ids);
        }


    private void validateExamLevelExists(Long id) {
        if (examLevelMapper.selectById(id) == null) {
            throw exception(EXAM_LEVEL_NOT_EXISTS);
        }
    }

    @Override
    public ExamLevelDO getExamLevel(Long id) {
        return examLevelMapper.selectById(id);
    }

    @Override
    public PageResult<ExamLevelDO> getExamLevelPage(ExamLevelPageReqVO pageReqVO) {
        return examLevelMapper.selectPage(pageReqVO);
    }

}