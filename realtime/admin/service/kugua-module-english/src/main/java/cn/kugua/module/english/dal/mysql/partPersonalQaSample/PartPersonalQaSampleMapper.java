package cn.kugua.module.english.dal.mysql.partPersonalQaSample;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.partPersonalQaSample.PartPersonalQaSampleDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.partPersonalQaSample.vo.*;

/**
 * 个人问答 - 示例答案 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartPersonalQaSampleMapper extends BaseMapperX<PartPersonalQaSampleDO> {

    default PageResult<PartPersonalQaSampleDO> selectPage(PartPersonalQaSamplePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartPersonalQaSampleDO>()
                .eqIfPresent(PartPersonalQaSampleDO::getQuestionId, reqVO.getQuestionId())
                .eqIfPresent(PartPersonalQaSampleDO::getSampleNo, reqVO.getSampleNo())
                .likeIfPresent(PartPersonalQaSampleDO::getSampleText, reqVO.getSampleText())
                .eqIfPresent(PartPersonalQaSampleDO::getLevelTag, reqVO.getLevelTag())
                .betweenIfPresent(PartPersonalQaSampleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartPersonalQaSampleDO::getId));
    }

}
