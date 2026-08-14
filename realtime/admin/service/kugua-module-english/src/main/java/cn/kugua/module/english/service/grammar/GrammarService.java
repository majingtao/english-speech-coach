package cn.kugua.module.english.service.grammar;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.grammar.vo.*;
import cn.kugua.module.english.controller.app.grammar.vo.*;
import cn.kugua.module.english.dal.dataobject.grammar.*;
import java.util.List;
public interface GrammarService {
    List<GrammarPointDO> getPoints(String level, boolean onlyEnabled);
    PageResult<GrammarQuestionDO> getQuestionPage(GrammarQuestionPageReqVO req);
    GrammarQuestionDO getQuestion(Long id);
    Long createQuestion(GrammarQuestionSaveReqVO req);
    void updateQuestion(GrammarQuestionSaveReqVO req);
    void deleteQuestion(Long id);
    GrammarGenerationJobDO importGenerated(GrammarBatchImportReqVO req);
    List<AppGrammarQuestionRespVO> getPractice(Long pointId, Integer difficulty, Integer count);
    AppGrammarAnswerRespVO answer(Long userId, Long questionId, AppGrammarAnswerReqVO req);
}
