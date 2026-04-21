package cn.kugua.module.english.dal.mysql.dictationWordlist;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordlistPageReqVO;
import cn.kugua.module.english.dal.dataobject.dictationWordlist.DictationWordlistDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictationWordlistMapper extends BaseMapperX<DictationWordlistDO> {

    default PageResult<DictationWordlistDO> selectPage(DictationWordlistPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DictationWordlistDO>()
                .likeIfPresent(DictationWordlistDO::getName, reqVO.getName())
                .eqIfPresent(DictationWordlistDO::getCategoryType, reqVO.getCategoryType())
                .eqIfPresent(DictationWordlistDO::getSchoolLevel, reqVO.getSchoolLevel())
                .eqIfPresent(DictationWordlistDO::getGrade, reqVO.getGrade())
                .eqIfPresent(DictationWordlistDO::getExamLevelCode, reqVO.getExamLevelCode())
                .eqIfPresent(DictationWordlistDO::getStatus, reqVO.getStatus())
                .orderByAsc(DictationWordlistDO::getSort));
    }

}
