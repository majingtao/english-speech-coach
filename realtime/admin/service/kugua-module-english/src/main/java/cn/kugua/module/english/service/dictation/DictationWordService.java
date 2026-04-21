package cn.kugua.module.english.service.dictation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordPageReqVO;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordSaveReqVO;
import cn.kugua.module.english.dal.dataobject.dictationWord.DictationWordDO;
import jakarta.validation.Valid;

import java.util.List;

public interface DictationWordService {

    Long createWord(@Valid DictationWordSaveReqVO reqVO);

    void updateWord(@Valid DictationWordSaveReqVO reqVO);

    void deleteWord(Long id);

    DictationWordDO getWord(Long id);

    PageResult<DictationWordDO> getWordPage(DictationWordPageReqVO reqVO);

    /** 批量创建单词（一行一词），返回成功创建数 */
    int batchCreateWords(List<String> words);

    /** 获取指定 ID 列表的单词 */
    List<DictationWordDO> getWordsByIds(List<Long> ids);

}
