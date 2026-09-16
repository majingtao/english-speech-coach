package cn.kugua.module.english.dal.mysql.expression;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionDialogueTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExpressionDialogueTaskMapper extends BaseMapperX<ExpressionDialogueTaskDO> {
    default List<ExpressionDialogueTaskDO> selectPublishedByLevel(String levelCode) {
        return selectList(new LambdaQueryWrapperX<ExpressionDialogueTaskDO>()
                .eq(ExpressionDialogueTaskDO::getLevelCode, levelCode)
                .eq(ExpressionDialogueTaskDO::getStatus, 1)
                .orderByAsc(ExpressionDialogueTaskDO::getSort)
                .orderByAsc(ExpressionDialogueTaskDO::getId));
    }
}
