package cn.kugua.module.english.service.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListDO;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListItemDO;

import java.util.List;

public interface UserVocabListService {

    /** 默认生词本的 source 标识：每个用户懒创建一条，前端不暴露多列表 */
    String WORDBOOK_SOURCE = "wordbook";

    Long createList(Long userId, String name, String description);

    void updateList(Long userId, Long listId, String name, String description);

    void deleteList(Long userId, Long listId);

    UserVocabListDO getList(Long listId);

    /** 返回用户所有词库，带词数统计（顺带返回的 Pair） */
    List<UserVocabListWithCount> getMyLists(Long userId);

    int addItems(Long userId, Long listId, List<Long> vocabIds);

    void removeItem(Long userId, Long listId, Long vocabId);

    PageResult<UserVocabListItemDO> getItemPage(Long userId, Long listId, PageParam pageParam);

    List<Long> getAllVocabIds(Long userId, Long listId);

    /** 取（并在缺失时懒创建）用户的默认生词本 */
    UserVocabListDO getOrCreateDefaultWordbook(Long userId);

    /** 默认生词本里的 vocab_id，按加入时间升序（FIFO 入队用） */
    List<Long> getWordbookVocabIds(Long userId);

    /** 组合类型：词库 + 词数 */
    class UserVocabListWithCount {
        public UserVocabListDO list;
        public Long wordCount;
        public UserVocabListWithCount(UserVocabListDO list, Long wordCount) {
            this.list = list;
            this.wordCount = wordCount;
        }
    }

}
