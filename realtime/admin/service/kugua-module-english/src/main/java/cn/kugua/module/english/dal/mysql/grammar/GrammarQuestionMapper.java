package cn.kugua.module.english.dal.mysql.grammar;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.grammar.GrammarQuestionDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GrammarQuestionMapper extends BaseMapperX<GrammarQuestionDO> {
    default PageResult<GrammarQuestionDO> selectPage(PageParam page, Long pointId, Integer difficulty, String type, Integer status) {
        return selectPage(page, new LambdaQueryWrapperX<GrammarQuestionDO>()
                .eqIfPresent(GrammarQuestionDO::getGrammarPointId, pointId)
                .eqIfPresent(GrammarQuestionDO::getDifficulty, difficulty)
                .eqIfPresent(GrammarQuestionDO::getQuestionType, type)
                .eqIfPresent(GrammarQuestionDO::getStatus, status)
                .orderByDesc(GrammarQuestionDO::getId));
    }
    default List<GrammarQuestionDO> selectPractice(Long pointId, Integer difficulty, Integer count) {
        return selectList(new LambdaQueryWrapperX<GrammarQuestionDO>()
                .eq(GrammarQuestionDO::getGrammarPointId, pointId)
                .leIfPresent(GrammarQuestionDO::getDifficulty, difficulty)
                .eq(GrammarQuestionDO::getStatus, 1)
                .last("ORDER BY RAND() LIMIT " + Math.max(1, Math.min(count, 30))));
    }
}
