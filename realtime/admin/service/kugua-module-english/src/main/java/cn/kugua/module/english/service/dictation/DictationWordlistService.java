package cn.kugua.module.english.service.dictation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordlistPageReqVO;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordlistSaveReqVO;
import cn.kugua.module.english.dal.dataobject.dictationWordlist.DictationWordlistDO;
import cn.kugua.module.english.dal.dataobject.dictationWordlistWord.DictationWordlistWordDO;
import jakarta.validation.Valid;

import java.util.List;

public interface DictationWordlistService {

    Long createWordlist(@Valid DictationWordlistSaveReqVO reqVO);

    void updateWordlist(@Valid DictationWordlistSaveReqVO reqVO);

    void deleteWordlist(Long id);

    DictationWordlistDO getWordlist(Long id);

    PageResult<DictationWordlistDO> getWordlistPage(DictationWordlistPageReqVO reqVO);

    /** 给词书添加单词 */
    void addWordsToWordlist(Long wordlistId, List<Long> wordIds);

    /** 从词书移除单词 */
    void removeWordFromWordlist(Long wordlistId, Long wordId);

    /** 获取词书的所有关联记录 */
    List<DictationWordlistWordDO> getWordlistWords(Long wordlistId);

    /** 查询所有已发布词书 */
    List<DictationWordlistDO> listPublished();

}
