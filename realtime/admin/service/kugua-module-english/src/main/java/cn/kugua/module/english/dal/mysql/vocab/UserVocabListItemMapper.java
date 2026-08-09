package cn.kugua.module.english.dal.mysql.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListItemDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserVocabListItemMapper extends BaseMapperX<UserVocabListItemDO> {

    /** 词条被物理删除时连带清掉所有用户词库里对该词的引用 */
    @Delete("DELETE FROM esc_user_vocab_list_item WHERE vocab_id = #{vocabId}")
    void physicalDeleteByVocabId(@Param("vocabId") Long vocabId);

    default List<UserVocabListItemDO> selectListByListId(Long listId) {
        return selectList(UserVocabListItemDO::getListId, listId);
    }

    /** 按加入时间升序（最早加入的先出），用于生词本 FIFO 入队 */
    default List<UserVocabListItemDO> selectListByListIdOrderByAddedAsc(Long listId) {
        return selectList(new LambdaQueryWrapperX<UserVocabListItemDO>()
                .eq(UserVocabListItemDO::getListId, listId)
                .orderByAsc(UserVocabListItemDO::getAddedAt)
                .orderByAsc(UserVocabListItemDO::getId));
    }

    default PageResult<UserVocabListItemDO> selectPageByListId(Long listId, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<UserVocabListItemDO>()
                .eq(UserVocabListItemDO::getListId, listId)
                .orderByDesc(UserVocabListItemDO::getAddedAt));
    }

    default UserVocabListItemDO selectByListAndVocab(Long listId, Long vocabId) {
        return selectOne(new LambdaQueryWrapperX<UserVocabListItemDO>()
                .eq(UserVocabListItemDO::getListId, listId)
                .eq(UserVocabListItemDO::getVocabId, vocabId));
    }

    default Long countByListId(Long listId) {
        return selectCount(UserVocabListItemDO::getListId, listId);
    }

    default void deleteByListAndVocab(Long listId, Long vocabId) {
        delete(new LambdaQueryWrapperX<UserVocabListItemDO>()
                .eq(UserVocabListItemDO::getListId, listId)
                .eq(UserVocabListItemDO::getVocabId, vocabId));
    }

}
