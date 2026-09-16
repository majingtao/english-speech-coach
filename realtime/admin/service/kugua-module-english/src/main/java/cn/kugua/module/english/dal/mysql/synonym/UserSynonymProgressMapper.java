package cn.kugua.module.english.dal.mysql.synonym;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.synonym.UserSynonymProgressDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserSynonymProgressMapper extends BaseMapperX<UserSynonymProgressDO> {
    default UserSynonymProgressDO selectByUserAndPoint(Long userId, Long pointId) {
        return selectOne(new LambdaQueryWrapperX<UserSynonymProgressDO>()
                .eq(UserSynonymProgressDO::getUserId, userId)
                .eq(UserSynonymProgressDO::getPointId, pointId));
    }

    default List<UserSynonymProgressDO> selectListByUser(Long userId) {
        return selectList(UserSynonymProgressDO::getUserId, userId);
    }
}
