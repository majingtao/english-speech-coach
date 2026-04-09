package cn.kugua.module.english.service.examPartType;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.examPartType.vo.*;
import cn.kugua.module.english.dal.dataobject.examPartType.ExamPartTypeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.examPartType.ExamPartTypeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 题型字典 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class ExamPartTypeServiceImpl implements ExamPartTypeService {

    @Resource
    private ExamPartTypeMapper examPartTypeMapper;

    @Override
    public Long createExamPartType(ExamPartTypeSaveReqVO createReqVO) {
        // 插入
        ExamPartTypeDO examPartType = BeanUtils.toBean(createReqVO, ExamPartTypeDO.class);
        examPartTypeMapper.insert(examPartType);

        // 返回
        return examPartType.getId();
    }

    @Override
    public void updateExamPartType(ExamPartTypeSaveReqVO updateReqVO) {
        // 校验存在
        validateExamPartTypeExists(updateReqVO.getId());
        // 更新
        ExamPartTypeDO updateObj = BeanUtils.toBean(updateReqVO, ExamPartTypeDO.class);
        examPartTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteExamPartType(Long id) {
        // 校验存在
        validateExamPartTypeExists(id);
        // 删除
        examPartTypeMapper.deleteById(id);
    }

    @Override
        public void deleteExamPartTypeListByIds(List<Long> ids) {
        // 删除
        examPartTypeMapper.deleteByIds(ids);
        }


    private void validateExamPartTypeExists(Long id) {
        if (examPartTypeMapper.selectById(id) == null) {
            throw exception(EXAM_PART_TYPE_NOT_EXISTS);
        }
    }

    @Override
    public ExamPartTypeDO getExamPartType(Long id) {
        return examPartTypeMapper.selectById(id);
    }

    @Override
    public PageResult<ExamPartTypeDO> getExamPartTypePage(ExamPartTypePageReqVO pageReqVO) {
        return examPartTypeMapper.selectPage(pageReqVO);
    }

}