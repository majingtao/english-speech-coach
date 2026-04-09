package cn.kugua.module.english.dal.mysql.practiceAnswer;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.practiceAnswer.PracticeAnswerDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.practiceAnswer.vo.*;

/**
 * 练习单题作答 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PracticeAnswerMapper extends BaseMapperX<PracticeAnswerDO> {

    default PageResult<PracticeAnswerDO> selectPage(PracticeAnswerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PracticeAnswerDO>()
                .eqIfPresent(PracticeAnswerDO::getSessionId, reqVO.getSessionId())
                .eqIfPresent(PracticeAnswerDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PracticeAnswerDO::getItemRefTable, reqVO.getItemRefTable())
                .eqIfPresent(PracticeAnswerDO::getItemRefId, reqVO.getItemRefId())
                .eqIfPresent(PracticeAnswerDO::getSeq, reqVO.getSeq())
                .eqIfPresent(PracticeAnswerDO::getQuestionSnapshot, reqVO.getQuestionSnapshot())
                .eqIfPresent(PracticeAnswerDO::getAudioUrl, reqVO.getAudioUrl())
                .eqIfPresent(PracticeAnswerDO::getAsrText, reqVO.getAsrText())
                .eqIfPresent(PracticeAnswerDO::getAsrEngine, reqVO.getAsrEngine())
                .eqIfPresent(PracticeAnswerDO::getAsrDurationMs, reqVO.getAsrDurationMs())
                .eqIfPresent(PracticeAnswerDO::getScoreGrammarVocab, reqVO.getScoreGrammarVocab())
                .eqIfPresent(PracticeAnswerDO::getScorePronunciation, reqVO.getScorePronunciation())
                .eqIfPresent(PracticeAnswerDO::getScoreInteraction, reqVO.getScoreInteraction())
                .eqIfPresent(PracticeAnswerDO::getScoreDiscourse, reqVO.getScoreDiscourse())
                .eqIfPresent(PracticeAnswerDO::getScoreOverall, reqVO.getScoreOverall())
                .eqIfPresent(PracticeAnswerDO::getFeedbackText, reqVO.getFeedbackText())
                .eqIfPresent(PracticeAnswerDO::getCorrectedText, reqVO.getCorrectedText())
                .eqIfPresent(PracticeAnswerDO::getLlmEngine, reqVO.getLlmEngine())
                .eqIfPresent(PracticeAnswerDO::getLlmRawResponse, reqVO.getLlmRawResponse())
                .betweenIfPresent(PracticeAnswerDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PracticeAnswerDO::getId));
    }

}