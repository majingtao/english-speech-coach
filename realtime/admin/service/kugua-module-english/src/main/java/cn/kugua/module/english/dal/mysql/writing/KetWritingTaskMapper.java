package cn.kugua.module.english.dal.mysql.writing;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.writing.KetWritingTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KetWritingTaskMapper extends BaseMapperX<KetWritingTaskDO> {

    default List<KetWritingTaskDO> selectPublished(String levelCode) {
        return selectList(new LambdaQueryWrapperX<KetWritingTaskDO>()
                .eqIfPresent(KetWritingTaskDO::getLevelCode, levelCode)
                .eq(KetWritingTaskDO::getEnabled, 1)
                .orderByAsc(KetWritingTaskDO::getSort)
                .orderByAsc(KetWritingTaskDO::getId));
    }
}
