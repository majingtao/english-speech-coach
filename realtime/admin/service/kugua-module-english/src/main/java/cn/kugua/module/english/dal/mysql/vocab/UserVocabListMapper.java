package cn.kugua.module.english.dal.mysql.vocab;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserVocabListMapper extends BaseMapperX<UserVocabListDO> {

    default List<UserVocabListDO> selectListByUser(Long userId) {
        return selectList(new LambdaQueryWrapperX<UserVocabListDO>()
                .eq(UserVocabListDO::getUserId, userId)
                .orderByDesc(UserVocabListDO::getId));
    }

    /** 取用户指定来源的词库（生词本用 source='wordbook'，每人一条），取最早创建的一条兜底去重 */
    default UserVocabListDO selectByUserAndSource(Long userId, String source) {
        return selectOne(new LambdaQueryWrapperX<UserVocabListDO>()
                .eq(UserVocabListDO::getUserId, userId)
                .eq(UserVocabListDO::getSource, source)
                .orderByAsc(UserVocabListDO::getId)
                .last("LIMIT 1"));
    }

}
