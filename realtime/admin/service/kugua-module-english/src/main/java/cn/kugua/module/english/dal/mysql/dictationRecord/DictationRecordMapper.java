package cn.kugua.module.english.dal.mysql.dictationRecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationRecordPageReqVO;
import cn.kugua.module.english.dal.dataobject.dictationRecord.DictationRecordDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictationRecordMapper extends BaseMapperX<DictationRecordDO> {

    default PageResult<DictationRecordDO> selectPage(DictationRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DictationRecordDO>()
                .eqIfPresent(DictationRecordDO::getStudentId, reqVO.getStudentId())
                .eqIfPresent(DictationRecordDO::getWordId, reqVO.getWordId())
                .eqIfPresent(DictationRecordDO::getWordlistId, reqVO.getWordlistId())
                .orderByDesc(DictationRecordDO::getId));
    }

    default List<DictationRecordDO> selectByStudentAndWordlist(Long studentId, Long wordlistId) {
        return selectList(new LambdaQueryWrapper<DictationRecordDO>()
                .eq(DictationRecordDO::getStudentId, studentId)
                .eq(DictationRecordDO::getWordlistId, wordlistId));
    }

}
