package cn.kugua.module.english.service.examPart;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.examPart.vo.*;
import cn.kugua.module.english.dal.dataobject.examPart.ExamPartDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.examPart.ExamPartMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 试卷 Part 多态头 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class ExamPartServiceImpl implements ExamPartService {

    @Resource
    private ExamPartMapper examPartMapper;

    @Override
    public Long createExamPart(ExamPartSaveReqVO createReqVO) {
        // 插入
        ExamPartDO examPart = BeanUtils.toBean(createReqVO, ExamPartDO.class);
        examPartMapper.insert(examPart);

        // 返回
        return examPart.getId();
    }

    @Override
    public void updateExamPart(ExamPartSaveReqVO updateReqVO) {
        // 校验存在
        validateExamPartExists(updateReqVO.getId());
        // 更新
        ExamPartDO updateObj = BeanUtils.toBean(updateReqVO, ExamPartDO.class);
        examPartMapper.updateById(updateObj);
    }

    @Override
    public void deleteExamPart(Long id) {
        // 校验存在
        validateExamPartExists(id);
        // 删除
        examPartMapper.deleteById(id);
    }

    @Override
        public void deleteExamPartListByIds(List<Long> ids) {
        // 删除
        examPartMapper.deleteByIds(ids);
        }


    private void validateExamPartExists(Long id) {
        if (examPartMapper.selectById(id) == null) {
            throw exception(EXAM_PART_NOT_EXISTS);
        }
    }

    @Override
    public ExamPartDO getExamPart(Long id) {
        return examPartMapper.selectById(id);
    }

    @Override
    public PageResult<ExamPartDO> getExamPartPage(ExamPartPageReqVO pageReqVO) {
        return examPartMapper.selectPage(pageReqVO);
    }

}