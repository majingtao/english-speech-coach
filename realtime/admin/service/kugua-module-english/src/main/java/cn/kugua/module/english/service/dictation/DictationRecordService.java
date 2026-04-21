package cn.kugua.module.english.service.dictation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationRecordPageReqVO;
import cn.kugua.module.english.dal.dataobject.dictationRecord.DictationRecordDO;

import java.util.List;

public interface DictationRecordService {

    Long createRecord(DictationRecordDO record);

    PageResult<DictationRecordDO> getRecordPage(DictationRecordPageReqVO reqVO);

    List<DictationRecordDO> getRecordsByStudentAndWordlist(Long studentId, Long wordlistId);

}
