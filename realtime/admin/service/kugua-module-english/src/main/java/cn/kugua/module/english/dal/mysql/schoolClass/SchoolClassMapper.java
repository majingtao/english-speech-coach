package cn.kugua.module.english.dal.mysql.schoolClass;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.schoolClass.SchoolClassDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.schoolClass.vo.*;

/**
 * 班级 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface SchoolClassMapper extends BaseMapperX<SchoolClassDO> {

    default PageResult<SchoolClassDO> selectPage(SchoolClassPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SchoolClassDO>()
                .likeIfPresent(SchoolClassDO::getName, reqVO.getName())
                .eqIfPresent(SchoolClassDO::getCode, reqVO.getCode())
                .eqIfPresent(SchoolClassDO::getDescription, reqVO.getDescription())
                .eqIfPresent(SchoolClassDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SchoolClassDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SchoolClassDO::getId));
    }

}