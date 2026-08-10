package cn.kugua.module.english.dal.mysql.writing;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.writing.KetWritingAttemptDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KetWritingAttemptMapper extends BaseMapperX<KetWritingAttemptDO> {

    default KetWritingAttemptDO selectLatest(Long userId, String taskId) {
        return selectOne(new LambdaQueryWrapperX<KetWritingAttemptDO>()
                .eq(KetWritingAttemptDO::getUserId, userId)
                .eq(KetWritingAttemptDO::getTaskId, taskId)
                .orderByDesc(KetWritingAttemptDO::getId)
                .last("LIMIT 1"));
    }

    default Long selectCountByUserAndTask(Long userId, String taskId) {
        return selectCount(new LambdaQueryWrapperX<KetWritingAttemptDO>()
                .eq(KetWritingAttemptDO::getUserId, userId)
                .eq(KetWritingAttemptDO::getTaskId, taskId));
    }
}
