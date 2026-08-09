package cn.kugua.module.english.dal.mysql.expression;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.expression.UserExpressionProgressDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserExpressionProgressMapper extends BaseMapperX<UserExpressionProgressDO> {

    default UserExpressionProgressDO selectByUserAndItem(Long userId, Long itemId) {
        return selectOne(new LambdaQueryWrapperX<UserExpressionProgressDO>()
                .eq(UserExpressionProgressDO::getUserId, userId)
                .eq(UserExpressionProgressDO::getExpressionItemId, itemId));
    }
}
