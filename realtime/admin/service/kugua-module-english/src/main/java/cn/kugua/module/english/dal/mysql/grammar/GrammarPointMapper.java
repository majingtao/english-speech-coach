package cn.kugua.module.english.dal.mysql.grammar;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.grammar.GrammarPointDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GrammarPointMapper extends BaseMapperX<GrammarPointDO> {
    default List<GrammarPointDO> selectEnabled(String level) {
        return selectList(new LambdaQueryWrapperX<GrammarPointDO>()
                .eq(GrammarPointDO::getLevelCode, level).eq(GrammarPointDO::getStatus, 1)
                .orderByAsc(GrammarPointDO::getSort).orderByAsc(GrammarPointDO::getId));
    }
    default List<GrammarPointDO> selectByLevel(String level) {
        return selectList(new LambdaQueryWrapperX<GrammarPointDO>()
                .eqIfPresent(GrammarPointDO::getLevelCode, level)
                .orderByAsc(GrammarPointDO::getSort).orderByAsc(GrammarPointDO::getId));
    }
}
