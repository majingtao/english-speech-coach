package cn.kugua.module.english.service.exam;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.exam.vo.ExamPageReqVO;
import cn.kugua.module.english.controller.admin.exam.vo.ExamSaveReqVO;
import cn.kugua.module.english.dal.dataobject.exam.ExamDO;
import jakarta.validation.Valid;

/**
 * 试卷 Service 接口
 */
public interface ExamService {

    Long createExam(@Valid ExamSaveReqVO reqVO);

    void updateExam(@Valid ExamSaveReqVO reqVO);

    void deleteExam(Long id);

    ExamDO getExam(Long id);

    PageResult<ExamDO> getExamPage(ExamPageReqVO reqVO);

    void updateContentJson(Long id, String contentJson);

}
