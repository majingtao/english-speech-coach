package cn.kugua.module.english.service.writing;

import cn.kugua.module.english.controller.app.writing.vo.AppKetWritingAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.writing.KetWritingAttemptDO;

public interface KetWritingAttemptService {

    KetWritingAttemptDO submitAttempt(Long userId, AppKetWritingAttemptReqVO reqVO);

    KetWritingAttemptDO getLatestAttempt(Long userId, String taskId);

    Integer countAttempts(Long userId, String taskId);
}
