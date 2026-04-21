package cn.kugua.module.english.dal.mysql.exam;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.exam.vo.ExamPageReqVO;
import cn.kugua.module.english.dal.dataobject.exam.ExamDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamMapper extends BaseMapperX<ExamDO> {

    default PageResult<ExamDO> selectPage(ExamPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ExamDO>()
                .likeIfPresent(ExamDO::getExamCode, reqVO.getExamCode())
                .likeIfPresent(ExamDO::getLabel, reqVO.getLabel())
                .eqIfPresent(ExamDO::getLevelCode, reqVO.getLevelCode())
                .eqIfPresent(ExamDO::getSeriesCode, reqVO.getSeriesCode())
                .eqIfPresent(ExamDO::getIsActive, reqVO.getIsActive())
                .eqIfPresent(ExamDO::getStatus, reqVO.getStatus())
                .orderByDesc(ExamDO::getId));
    }

    default ExamDO selectByCodeAndVersion(String examCode, Integer version) {
        return selectOne(ExamDO::getExamCode, examCode, ExamDO::getVersion, version);
    }

}
