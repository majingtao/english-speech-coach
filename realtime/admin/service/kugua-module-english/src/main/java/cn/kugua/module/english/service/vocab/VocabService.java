package cn.kugua.module.english.service.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabBatchImportReqVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabPageReqVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabSaveReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import jakarta.validation.Valid;

import java.util.List;

public interface VocabService {

    Long createVocab(@Valid VocabSaveReqVO reqVO);

    void updateVocab(@Valid VocabSaveReqVO reqVO);

    void deleteVocab(Long id);

    VocabDO getVocab(Long id);

    /** 当 content_json 为空时调用 Python 生成，成功则写回。 */
    VocabDO getOrGenerateContent(Long id);

    /** 管理员手动重新生成 content_json（覆盖旧值） */
    void regenerateContent(Long id);

    /**
     * 懒获取词条发音 URL：已存返回；未生成则调 Python TTS + 上传 yudao 文件服务，回写后返回。
     * @param accent "uk" 或 "us"
     */
    String getOrGenerateAudio(Long id, String accent);

    PageResult<VocabDO> getVocabPage(VocabPageReqVO reqVO);

    List<VocabDO> getVocabListByIds(List<Long> ids);

    /**
     * 按单词前缀在已发布词库（status=1）中搜索，用于生词本联想补全 + 校验。
     * @param levelCode 可选：非空时限定级别
     * @param keyword   单词前缀（忽略大小写、首尾空白）
     * @param limit     返回上限
     */
    List<VocabDO> searchPublished(String levelCode, String keyword, int limit);

    /** 按级别 + 主题（可选）+ 难度（可选）分页浏览 */
    PageResult<VocabDO> getVocabBrowsePage(String levelCode, String themeCode, Integer difficulty, int pageNo, int pageSize);

    /** 返回词条关联的主题ID列表 */
    List<Long> getThemeIdsByVocabId(Long vocabId);

    /** 返回词条关联的主题 code 列表 */
    List<String> getThemeCodesByVocabId(Long vocabId);

    /** 批量导入（从 JSON 粘贴） */
    int batchImport(@Valid VocabBatchImportReqVO reqVO);

}
