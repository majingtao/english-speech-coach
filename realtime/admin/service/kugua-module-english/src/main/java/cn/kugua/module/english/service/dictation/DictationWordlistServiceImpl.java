package cn.kugua.module.english.service.dictation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordlistPageReqVO;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordlistSaveReqVO;
import cn.kugua.module.english.dal.dataobject.dictationWordlist.DictationWordlistDO;
import cn.kugua.module.english.dal.dataobject.dictationWordlistWord.DictationWordlistWordDO;
import cn.kugua.module.english.dal.mysql.dictationWordlist.DictationWordlistMapper;
import cn.kugua.module.english.dal.mysql.dictationWordlistWord.DictationWordlistWordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.DICTATION_WORDLIST_NOT_EXISTS;

@Service
@Validated
public class DictationWordlistServiceImpl implements DictationWordlistService {

    @Resource
    private DictationWordlistMapper wordlistMapper;

    @Resource
    private DictationWordlistWordMapper wordlistWordMapper;

    @Override
    public Long createWordlist(DictationWordlistSaveReqVO reqVO) {
        DictationWordlistDO wordlist = BeanUtils.toBean(reqVO, DictationWordlistDO.class);
        wordlist.setWordCount(0);
        wordlistMapper.insert(wordlist);
        return wordlist.getId();
    }

    @Override
    public void updateWordlist(DictationWordlistSaveReqVO reqVO) {
        validateWordlistExists(reqVO.getId());
        DictationWordlistDO updateObj = BeanUtils.toBean(reqVO, DictationWordlistDO.class);
        wordlistMapper.updateById(updateObj);
    }

    @Override
    public void deleteWordlist(Long id) {
        validateWordlistExists(id);
        wordlistMapper.deleteById(id);
    }

    @Override
    public DictationWordlistDO getWordlist(Long id) {
        return wordlistMapper.selectById(id);
    }

    @Override
    public PageResult<DictationWordlistDO> getWordlistPage(DictationWordlistPageReqVO reqVO) {
        return wordlistMapper.selectPage(reqVO);
    }

    @Override
    public void addWordsToWordlist(Long wordlistId, List<Long> wordIds) {
        validateWordlistExists(wordlistId);
        // 获取当前最大 seq
        List<DictationWordlistWordDO> existing = wordlistWordMapper.selectByWordlistId(wordlistId);
        int maxSeq = existing.stream().mapToInt(DictationWordlistWordDO::getSeq).max().orElse(0);
        int added = 0;
        for (Long wordId : wordIds) {
            if (wordlistWordMapper.selectByWordlistIdAndWordId(wordlistId, wordId) != null) {
                continue;
            }
            DictationWordlistWordDO link = new DictationWordlistWordDO();
            link.setWordlistId(wordlistId);
            link.setWordId(wordId);
            link.setSeq(++maxSeq);
            wordlistWordMapper.insert(link);
            added++;
        }
        // 更新缓存数
        if (added > 0) {
            DictationWordlistDO update = new DictationWordlistDO();
            update.setId(wordlistId);
            update.setWordCount(existing.size() + added);
            wordlistMapper.updateById(update);
        }
    }

    @Override
    public void removeWordFromWordlist(Long wordlistId, Long wordId) {
        wordlistWordMapper.deleteByWordlistIdAndWordId(wordlistId, wordId);
        // 更新缓存数
        List<DictationWordlistWordDO> remaining = wordlistWordMapper.selectByWordlistId(wordlistId);
        DictationWordlistDO update = new DictationWordlistDO();
        update.setId(wordlistId);
        update.setWordCount(remaining.size());
        wordlistMapper.updateById(update);
    }

    @Override
    public List<DictationWordlistWordDO> getWordlistWords(Long wordlistId) {
        return wordlistWordMapper.selectByWordlistId(wordlistId);
    }

    @Override
    public List<DictationWordlistDO> listPublished() {
        return wordlistMapper.selectList(new LambdaQueryWrapper<DictationWordlistDO>()
                .eq(DictationWordlistDO::getStatus, 1)
                .orderByAsc(DictationWordlistDO::getSort));
    }

    private void validateWordlistExists(Long id) {
        if (wordlistMapper.selectById(id) == null) {
            throw exception(DICTATION_WORDLIST_NOT_EXISTS);
        }
    }

}
