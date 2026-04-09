package cn.kugua.module.english.dal.mysql.examPartType;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.examPartType.ExamPartTypeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.examPartType.vo.*;

/**
 * 题型字典 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface ExamPartTypeMapper extends BaseMapperX<ExamPartTypeDO> {

    default PageResult<ExamPartTypeDO> selectPage(ExamPartTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ExamPartTypeDO>()
                .eqIfPresent(ExamPartTypeDO::getCode, reqVO.getCode())
                .likeIfPresent(ExamPartTypeDO::getName, reqVO.getName())
                .eqIfPresent(ExamPartTypeDO::getDescription, reqVO.getDescription())
                .eqIfPresent(ExamPartTypeDO::getSort, reqVO.getSort())
                .eqIfPresent(ExamPartTypeDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ExamPartTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ExamPartTypeDO::getId));
    }

}