package cn.kugua.module.english.dal.mysql.expression;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionThemeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExpressionThemeMapper extends BaseMapperX<ExpressionThemeDO> {

    default ExpressionThemeDO selectByCode(String levelCode, String code) {
        return selectOne(new LambdaQueryWrapperX<ExpressionThemeDO>()
                .eq(ExpressionThemeDO::getLevelCode, levelCode)
                .eq(ExpressionThemeDO::getCode, code));
    }

    default List<ExpressionThemeDO> selectEnabledByLevel(String levelCode) {
        return selectList(new LambdaQueryWrapperX<ExpressionThemeDO>()
                .eqIfPresent(ExpressionThemeDO::getLevelCode, levelCode)
                .eq(ExpressionThemeDO::getStatus, 1)
                .orderByAsc(ExpressionThemeDO::getSort)
                .orderByAsc(ExpressionThemeDO::getId));
    }
}
