package cn.kugua.module.english.service.personalpractice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.personalpractice.vo.PersonalPracticePageReqVO;
import cn.kugua.module.english.controller.admin.personalpractice.vo.PersonalPracticeSaveReqVO;
import cn.kugua.module.english.controller.app.personalpractice.vo.AppPersonalPracticeAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeAttemptDO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeDO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeProgressDO;
import cn.kugua.module.english.dal.mysql.personalpractice.PersonalPracticeAttemptMapper;
import cn.kugua.module.english.dal.mysql.personalpractice.PersonalPracticeMapper;
import cn.kugua.module.english.dal.mysql.personalpractice.PersonalPracticeProgressMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.PERSONAL_PRACTICE_NOT_EXISTS;
import static cn.kugua.module.english.enums.ErrorCodeConstants.PERSONAL_PRACTICE_CONFIG_INVALID;
import static cn.kugua.module.english.enums.ErrorCodeConstants.PERSONAL_PRACTICE_TYPE_INVALID;

@Service
public class PersonalPracticeServiceImpl implements PersonalPracticeService {

    @Resource
    private PersonalPracticeMapper practiceMapper;
    @Resource
    private PersonalPracticeProgressMapper progressMapper;
    @Resource
    private PersonalPracticeAttemptMapper attemptMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long create(PersonalPracticeSaveReqVO reqVO) {
        validate(reqVO);
        PersonalPracticeDO practice = BeanUtils.toBean(reqVO, PersonalPracticeDO.class);
        applyDefaults(practice);
        practiceMapper.insert(practice);
        return practice.getId();
    }

    @Override
    public void update(PersonalPracticeSaveReqVO reqVO) {
        validateExists(reqVO.getId());
        validate(reqVO);
        PersonalPracticeDO practice = BeanUtils.toBean(reqVO, PersonalPracticeDO.class);
        applyDefaults(practice);
        practiceMapper.updateById(practice);
    }

    @Override
    public void delete(Long id) {
        validateExists(id);
        practiceMapper.deleteById(id);
    }

    @Override
    public PersonalPracticeDO get(Long id) {
        return practiceMapper.selectById(id);
    }

    @Override
    public PageResult<PersonalPracticeDO> getPage(PersonalPracticePageReqVO reqVO) {
        return practiceMapper.selectPage(reqVO, reqVO.getPracticeType(), reqVO.getTitle(), reqVO.getStatus());
    }

    @Override
    public List<PersonalPracticeDO> getPublished(String practiceType) {
        validateType(practiceType);
        return practiceMapper.selectPublished(practiceType);
    }

    @Override
    public PersonalPracticeProgressDO getProgress(Long userId, Long practiceId) {
        return progressMapper.selectByUserAndPractice(userId, practiceId);
    }

    @Override
    @Transactional
    public PersonalPracticeAttemptDO submitAttempt(Long userId, Long practiceId, AppPersonalPracticeAttemptReqVO reqVO) {
        PersonalPracticeDO practice = validateExists(practiceId);
        if (!Integer.valueOf(1).equals(practice.getStatus())) {
            throw exception(PERSONAL_PRACTICE_NOT_EXISTS);
        }
        PersonalPracticeAttemptDO attempt = BeanUtils.toBean(reqVO, PersonalPracticeAttemptDO.class);
        int totalScore = reqVO.getGrammarScore() + reqVO.getContentScore();
        attempt.setUserId(userId);
        attempt.setPracticeId(practiceId);
        attempt.setTotalScore(totalScore);
        attemptMapper.insert(attempt);

        PersonalPracticeProgressDO progress = progressMapper.selectByUserAndPractice(userId, practiceId);
        boolean create = progress == null;
        if (create) {
            progress = new PersonalPracticeProgressDO();
            progress.setUserId(userId);
            progress.setPracticeId(practiceId);
            progress.setAttemptCount(0);
            progress.setBestScore(0);
        }
        progress.setAttemptCount(progress.getAttemptCount() + 1);
        progress.setLastScore(totalScore);
        progress.setBestScore(Math.max(progress.getBestScore(), totalScore));
        progress.setStatus(totalScore >= 80 ? 2 : 1);
        progress.setLastPracticeAt(LocalDateTime.now());
        if (create) progressMapper.insert(progress);
        else progressMapper.updateById(progress);
        return attempt;
    }

    private PersonalPracticeDO validateExists(Long id) {
        PersonalPracticeDO practice = id == null ? null : practiceMapper.selectById(id);
        if (practice == null) throw exception(PERSONAL_PRACTICE_NOT_EXISTS);
        return practice;
    }

    private void validate(PersonalPracticeSaveReqVO reqVO) {
        validateType(reqVO.getPracticeType());
        try {
            objectMapper.readTree(reqVO.getReferenceJson());
            if (reqVO.getContentPointsJson() != null && !reqVO.getContentPointsJson().isBlank()) {
                objectMapper.readTree(reqVO.getContentPointsJson());
            }
        } catch (Exception exception) {
            throw exception(PERSONAL_PRACTICE_CONFIG_INVALID);
        }
    }

    private void validateType(String type) {
        if (!"speaking".equals(type) && !"writing".equals(type)) {
            throw exception(PERSONAL_PRACTICE_TYPE_INVALID);
        }
    }

    private void applyDefaults(PersonalPracticeDO practice) {
        if (practice.getMinSentences() == null || practice.getMinSentences() < 1) practice.setMinSentences(1);
        if (practice.getMinWords() == null || practice.getMinWords() < 0) practice.setMinWords(0);
        if (practice.getSort() == null) practice.setSort(0);
        if (practice.getStatus() == null) practice.setStatus(1);
        if (practice.getContentPointsJson() == null || practice.getContentPointsJson().isBlank()) {
            practice.setContentPointsJson("[]");
        }
    }
}
