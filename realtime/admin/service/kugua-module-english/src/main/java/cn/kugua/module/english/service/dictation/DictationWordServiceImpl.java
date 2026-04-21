package cn.kugua.module.english.service.dictation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordPageReqVO;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordSaveReqVO;
import cn.kugua.module.english.dal.dataobject.dictationWord.DictationWordDO;
import cn.kugua.module.english.dal.mysql.dictationWord.DictationWordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

@Service
@Validated
public class DictationWordServiceImpl implements DictationWordService {

    @Resource
    private DictationWordMapper wordMapper;

    @Override
    public Long createWord(DictationWordSaveReqVO reqVO) {
        validateWordUnique(null, reqVO.getEn());
        DictationWordDO word = BeanUtils.toBean(reqVO, DictationWordDO.class);
        wordMapper.insert(word);
        return word.getId();
    }

    @Override
    public void updateWord(DictationWordSaveReqVO reqVO) {
        validateWordExists(reqVO.getId());
        DictationWordDO updateObj = BeanUtils.toBean(reqVO, DictationWordDO.class);
        wordMapper.updateById(updateObj);
    }

    @Override
    public void deleteWord(Long id) {
        validateWordExists(id);
        wordMapper.deleteById(id);
    }

    @Override
    public DictationWordDO getWord(Long id) {
        return wordMapper.selectById(id);
    }

    @Override
    public PageResult<DictationWordDO> getWordPage(DictationWordPageReqVO reqVO) {
        return wordMapper.selectPage(reqVO);
    }

    @Override
    public int batchCreateWords(List<String> words) {
        int count = 0;
        for (String en : words) {
            String trimmed = en.trim();
            if (trimmed.isEmpty()) continue;
            DictationWordDO exist = wordMapper.selectByEn(trimmed);
            if (exist != null) continue;
            DictationWordDO word = new DictationWordDO();
            word.setEn(trimmed);
            word.setCn("");
            word.setPos("");
            word.setForms("");
            word.setDifficulty(1);
            word.setLlmStatus(0);
            wordMapper.insert(word);
            count++;
        }
        return count;
    }

    @Override
    public List<DictationWordDO> getWordsByIds(List<Long> ids) {
        return wordMapper.selectBatchIds(ids);
    }

    private void validateWordExists(Long id) {
        if (wordMapper.selectById(id) == null) {
            throw exception(DICTATION_WORD_NOT_EXISTS);
        }
    }

    private void validateWordUnique(Long id, String en) {
        DictationWordDO exist = wordMapper.selectByEn(en);
        if (exist == null) return;
        if (id == null || !exist.getId().equals(id)) {
            throw exception(DICTATION_WORD_DUPLICATE);
        }
    }

}
