package cn.kugua.module.english.service.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListDO;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListItemDO;
import cn.kugua.module.english.dal.mysql.vocab.UserVocabListItemMapper;
import cn.kugua.module.english.dal.mysql.vocab.UserVocabListMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

@Service
public class UserVocabListServiceImpl implements UserVocabListService {

    @Resource
    private UserVocabListMapper listMapper;

    @Resource
    private UserVocabListItemMapper itemMapper;

    @Override
    public Long createList(Long userId, String name, String description) {
        UserVocabListDO list = new UserVocabListDO();
        list.setUserId(userId);
        list.setName(name);
        list.setDescription(description == null ? "" : description);
        list.setSource("manual");
        list.setStatus(0);
        listMapper.insert(list);
        return list.getId();
    }

    @Override
    public void updateList(Long userId, Long listId, String name, String description) {
        UserVocabListDO list = requireOwned(userId, listId);
        if (name != null) list.setName(name);
        if (description != null) list.setDescription(description);
        listMapper.updateById(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteList(Long userId, Long listId) {
        requireOwned(userId, listId);
        listMapper.deleteById(listId);
        // items 软删除也随之生效（通过 list_id 外键查询会返回 deleted=0 的，但关联行未直接标删；
        // 简单方案：按 listId 批量 delete）
        for (UserVocabListItemDO it : itemMapper.selectListByListId(listId)) {
            itemMapper.deleteById(it.getId());
        }
    }

    @Override
    public UserVocabListDO getList(Long listId) {
        return listMapper.selectById(listId);
    }

    @Override
    public List<UserVocabListWithCount> getMyLists(Long userId) {
        List<UserVocabListDO> lists = listMapper.selectListByUser(userId);
        List<UserVocabListWithCount> result = new ArrayList<>(lists.size());
        for (UserVocabListDO l : lists) {
            result.add(new UserVocabListWithCount(l, itemMapper.countByListId(l.getId())));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addItems(Long userId, Long listId, List<Long> vocabIds) {
        requireOwned(userId, listId);
        if (vocabIds == null || vocabIds.isEmpty()) return 0;
        int count = 0;
        for (Long vid : vocabIds) {
            if (vid == null) continue;
            if (itemMapper.selectByListAndVocab(listId, vid) != null) continue;
            UserVocabListItemDO it = new UserVocabListItemDO();
            it.setListId(listId);
            it.setVocabId(vid);
            it.setAddedAt(LocalDateTime.now());
            itemMapper.insert(it);
            count++;
        }
        return count;
    }

    @Override
    public void removeItem(Long userId, Long listId, Long vocabId) {
        requireOwned(userId, listId);
        itemMapper.deleteByListAndVocab(listId, vocabId);
    }

    @Override
    public PageResult<UserVocabListItemDO> getItemPage(Long userId, Long listId, PageParam pageParam) {
        requireOwned(userId, listId);
        return itemMapper.selectPageByListId(listId, pageParam);
    }

    @Override
    public List<Long> getAllVocabIds(Long userId, Long listId) {
        requireOwned(userId, listId);
        return itemMapper.selectListByListId(listId).stream()
                .map(UserVocabListItemDO::getVocabId)
                .collect(Collectors.toList());
    }

    @Override
    public UserVocabListDO getOrCreateDefaultWordbook(Long userId) {
        UserVocabListDO existing = listMapper.selectByUserAndSource(userId, WORDBOOK_SOURCE);
        if (existing != null) return existing;
        UserVocabListDO list = new UserVocabListDO();
        list.setUserId(userId);
        list.setName("我的生词本");
        list.setDescription("");
        list.setSource(WORDBOOK_SOURCE);
        list.setStatus(0);
        listMapper.insert(list);
        return list;
    }

    @Override
    public List<Long> getWordbookVocabIds(Long userId) {
        UserVocabListDO wordbook = getOrCreateDefaultWordbook(userId);
        return itemMapper.selectListByListIdOrderByAddedAsc(wordbook.getId()).stream()
                .map(UserVocabListItemDO::getVocabId)
                .collect(Collectors.toList());
    }

    private UserVocabListDO requireOwned(Long userId, Long listId) {
        UserVocabListDO list = listMapper.selectById(listId);
        if (list == null) throw exception(USER_VOCAB_LIST_NOT_EXISTS);
        if (!list.getUserId().equals(userId)) throw exception(USER_VOCAB_LIST_NOT_OWNER);
        return list;
    }

}
