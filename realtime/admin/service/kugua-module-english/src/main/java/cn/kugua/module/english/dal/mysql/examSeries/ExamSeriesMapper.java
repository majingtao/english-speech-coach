package cn.kugua.module.english.dal.mysql.examSeries;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.examSeries.ExamSeriesDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.examSeries.vo.*;

/**
 * 考试系列字典 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface ExamSeriesMapper extends BaseMapperX<ExamSeriesDO> {

    default PageResult<ExamSeriesDO> selectPage(ExamSeriesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ExamSeriesDO>()
                .eqIfPresent(ExamSeriesDO::getCode, reqVO.getCode())
                .eqIfPresent(ExamSeriesDO::getLevelCode, reqVO.getLevelCode())
                .likeIfPresent(ExamSeriesDO::getName, reqVO.getName())
                .likeIfPresent(ExamSeriesDO::getPublisher, reqVO.getPublisher())
                .eqIfPresent(ExamSeriesDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ExamSeriesDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(ExamSeriesDO::getLevelCode)
                .orderByAsc(ExamSeriesDO::getSort));
    }

}
