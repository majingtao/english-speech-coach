package cn.kugua.module.english.service.writing;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.app.writing.vo.AppKetWritingAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.writing.KetWritingAttemptDO;
import cn.kugua.module.english.dal.mysql.writing.KetWritingAttemptMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class KetWritingAttemptServiceImpl implements KetWritingAttemptService {

    @Resource
    private KetWritingAttemptMapper attemptMapper;

    @Override
    public KetWritingAttemptDO submitAttempt(Long userId, AppKetWritingAttemptReqVO reqVO) {
        KetWritingAttemptDO attempt = BeanUtils.toBean(reqVO, KetWritingAttemptDO.class);
        attempt.setUserId(userId);
        attempt.setLevelCode(blankToDefault(reqVO.getLevelCode(), "ket"));
        attempt.setPracticeMode(normalizeMode(reqVO.getPracticeMode()));
        attempt.setWordCount(reqVO.getWordCount() == null ? 0 : reqVO.getWordCount());
        attemptMapper.insert(attempt);
        return attempt;
    }

    @Override
    public KetWritingAttemptDO getLatestAttempt(Long userId, String taskId) {
        return attemptMapper.selectLatest(userId, taskId);
    }

    @Override
    public Integer countAttempts(Long userId, String taskId) {
        Long count = attemptMapper.selectCountByUserAndTask(userId, taskId);
        return count == null ? 0 : count.intValue();
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim().toLowerCase();
    }

    private String normalizeMode(String mode) {
        String value = mode == null ? "" : mode.trim().toLowerCase();
        if ("guided".equals(value) || "imitate".equals(value) || "free".equals(value)) {
            return value;
        }
        return "guided";
    }
}
