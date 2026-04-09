package cn.kugua.module.english.service.exam;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.exam.vo.ExamPageReqVO;
import cn.kugua.module.english.controller.admin.exam.vo.ExamSaveReqVO;
import cn.kugua.module.english.dal.dataobject.exam.ExamDO;
import cn.kugua.module.english.dal.mysql.exam.ExamMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.EXAM_CODE_DUPLICATE;
import static cn.kugua.module.english.enums.ErrorCodeConstants.EXAM_NOT_EXISTS;

/**
 * 试卷 Service 实现
 */
@Service
@Validated
public class ExamServiceImpl implements ExamService {

    @Resource
    private ExamMapper examMapper;

    @Override
    public Long createExam(ExamSaveReqVO reqVO) {
        validateExamCodeUnique(null, reqVO.getExamCode(), reqVO.getVersion());
        ExamDO exam = BeanUtils.toBean(reqVO, ExamDO.class);
        examMapper.insert(exam);
        return exam.getId();
    }

    @Override
    public void updateExam(ExamSaveReqVO reqVO) {
        validateExamExists(reqVO.getId());
        validateExamCodeUnique(reqVO.getId(), reqVO.getExamCode(), reqVO.getVersion());
        ExamDO updateObj = BeanUtils.toBean(reqVO, ExamDO.class);
        examMapper.updateById(updateObj);
    }

    @Override
    public void deleteExam(Long id) {
        validateExamExists(id);
        examMapper.deleteById(id);
    }

    @Override
    public ExamDO getExam(Long id) {
        return examMapper.selectById(id);
    }

    @Override
    public PageResult<ExamDO> getExamPage(ExamPageReqVO reqVO) {
        return examMapper.selectPage(reqVO);
    }

    // ========== 校验 ==========

    private void validateExamExists(Long id) {
        if (examMapper.selectById(id) == null) {
            throw exception(EXAM_NOT_EXISTS);
        }
    }

    private void validateExamCodeUnique(Long id, String examCode, Integer version) {
        ExamDO exist = examMapper.selectByCodeAndVersion(examCode, version);
        if (exist == null) {
            return;
        }
        if (id == null || !exist.getId().equals(id)) {
            throw exception(EXAM_CODE_DUPLICATE);
        }
    }

}
