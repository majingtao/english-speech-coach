package cn.kugua.module.english.dal.mysql.partPersonalQuestion;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.partPersonalQuestion.PartPersonalQuestionDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.partPersonalQuestion.vo.*;

/**
 * 个人问答 - 问题 Mapper
 *
 * @author 苦瓜科技
 */
@Mapper
public interface PartPersonalQuestionMapper extends BaseMapperX<PartPersonalQuestionDO> {

    default PageResult<PartPersonalQuestionDO> selectPage(PartPersonalQuestionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartPersonalQuestionDO>()
                .eqIfPresent(PartPersonalQuestionDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PartPersonalQuestionDO::getQNo, reqVO.getQNo())
                .eqIfPresent(PartPersonalQuestionDO::getQuestionText, reqVO.getQuestionText())
                .eqIfPresent(PartPersonalQuestionDO::getTopic, reqVO.getTopic())
                .eqIfPresent(PartPersonalQuestionDO::getSort, reqVO.getSort())
                .betweenIfPresent(PartPersonalQuestionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartPersonalQuestionDO::getId));
    }

}