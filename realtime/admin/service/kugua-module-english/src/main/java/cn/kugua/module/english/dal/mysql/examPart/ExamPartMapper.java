package cn.kugua.module.english.dal.mysql.examPart;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.examPart.ExamPartDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.examPart.vo.*;

/**
 * 试卷 Part 多态头 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface ExamPartMapper extends BaseMapperX<ExamPartDO> {

    default PageResult<ExamPartDO> selectPage(ExamPartPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ExamPartDO>()
                .eqIfPresent(ExamPartDO::getExamId, reqVO.getExamId())
                .eqIfPresent(ExamPartDO::getPartNo, reqVO.getPartNo())
                .eqIfPresent(ExamPartDO::getPartType, reqVO.getPartType())
                .eqIfPresent(ExamPartDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ExamPartDO::getIntro, reqVO.getIntro())
                .eqIfPresent(ExamPartDO::getTimeLimitSec, reqVO.getTimeLimitSec())
                .eqIfPresent(ExamPartDO::getSort, reqVO.getSort())
                .betweenIfPresent(ExamPartDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ExamPartDO::getId));
    }

}