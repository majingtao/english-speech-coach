package cn.kugua.module.english.dal.mysql.practiceSession;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.practiceSession.PracticeSessionDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.practiceSession.vo.*;

/**
 * 练习会话 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PracticeSessionMapper extends BaseMapperX<PracticeSessionDO> {

    default PageResult<PracticeSessionDO> selectPage(PracticeSessionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PracticeSessionDO>()
                .eqIfPresent(PracticeSessionDO::getStudentId, reqVO.getStudentId())
                .eqIfPresent(PracticeSessionDO::getExamId, reqVO.getExamId())
                .eqIfPresent(PracticeSessionDO::getExamCode, reqVO.getExamCode())
                .eqIfPresent(PracticeSessionDO::getMode, reqVO.getMode())
                .betweenIfPresent(PracticeSessionDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(PracticeSessionDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(PracticeSessionDO::getDurationSec, reqVO.getDurationSec())
                .eqIfPresent(PracticeSessionDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PracticeSessionDO::getFinalOverallScore, reqVO.getFinalOverallScore())
                .eqIfPresent(PracticeSessionDO::getFinalComment, reqVO.getFinalComment())
                .betweenIfPresent(PracticeSessionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PracticeSessionDO::getId));
    }

}