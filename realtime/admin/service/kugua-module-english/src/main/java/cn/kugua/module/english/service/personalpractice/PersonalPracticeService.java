package cn.kugua.module.english.service.personalpractice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.personalpractice.vo.PersonalPracticePageReqVO;
import cn.kugua.module.english.controller.admin.personalpractice.vo.PersonalPracticeSaveReqVO;
import cn.kugua.module.english.controller.app.personalpractice.vo.AppPersonalPracticeAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeAttemptDO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeDO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeProgressDO;

import java.util.List;

public interface PersonalPracticeService {
    Long create(PersonalPracticeSaveReqVO reqVO);
    void update(PersonalPracticeSaveReqVO reqVO);
    void delete(Long id);
    PersonalPracticeDO get(Long id);
    PageResult<PersonalPracticeDO> getPage(PersonalPracticePageReqVO reqVO);
    List<PersonalPracticeDO> getPublished(String practiceType);
    PersonalPracticeProgressDO getProgress(Long userId, Long practiceId);
    PersonalPracticeAttemptDO submitAttempt(Long userId, Long practiceId, AppPersonalPracticeAttemptReqVO reqVO);
}
