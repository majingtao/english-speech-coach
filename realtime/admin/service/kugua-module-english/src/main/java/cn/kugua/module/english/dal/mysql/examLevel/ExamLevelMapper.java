package cn.kugua.module.english.dal.mysql.examLevel;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.examLevel.ExamLevelDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.examLevel.vo.*;

/**
 * 考试级别字典 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface ExamLevelMapper extends BaseMapperX<ExamLevelDO> {

    default PageResult<ExamLevelDO> selectPage(ExamLevelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ExamLevelDO>()
                .eqIfPresent(ExamLevelDO::getCode, reqVO.getCode())
                .likeIfPresent(ExamLevelDO::getName, reqVO.getName())
                .eqIfPresent(ExamLevelDO::getCefr, reqVO.getCefr())
                .eqIfPresent(ExamLevelDO::getDescription, reqVO.getDescription())
                .eqIfPresent(ExamLevelDO::getSort, reqVO.getSort())
                .eqIfPresent(ExamLevelDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ExamLevelDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ExamLevelDO::getId));
    }

}