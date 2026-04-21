package cn.kugua.module.english.service.dictation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationRecordPageReqVO;
import cn.kugua.module.english.dal.dataobject.dictationRecord.DictationRecordDO;
import cn.kugua.module.english.dal.mysql.dictationRecord.DictationRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service("dictationRecordService")
@Validated
public class DictationRecordServiceImpl implements DictationRecordService {

    @Resource
    private DictationRecordMapper recordMapper;

    @Override
    public Long createRecord(DictationRecordDO record) {
        recordMapper.insert(record);
        return record.getId();
    }

    @Override
    public PageResult<DictationRecordDO> getRecordPage(DictationRecordPageReqVO reqVO) {
        return recordMapper.selectPage(reqVO);
    }

    @Override
    public List<DictationRecordDO> getRecordsByStudentAndWordlist(Long studentId, Long wordlistId) {
        return recordMapper.selectByStudentAndWordlist(studentId, wordlistId);
    }

}
