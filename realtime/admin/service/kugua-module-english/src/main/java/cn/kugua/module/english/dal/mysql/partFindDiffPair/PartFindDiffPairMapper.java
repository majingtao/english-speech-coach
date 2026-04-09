package cn.kugua.module.english.dal.mysql.partFindDiffPair;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.partFindDiffPair.PartFindDiffPairDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.partFindDiffPair.vo.*;

/**
 * 找不同 - 图对 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartFindDiffPairMapper extends BaseMapperX<PartFindDiffPairDO> {

    default PageResult<PartFindDiffPairDO> selectPage(PartFindDiffPairPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartFindDiffPairDO>()
                .eqIfPresent(PartFindDiffPairDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PartFindDiffPairDO::getPairNo, reqVO.getPairNo())
                .eqIfPresent(PartFindDiffPairDO::getImageAUrl, reqVO.getImageAUrl())
                .eqIfPresent(PartFindDiffPairDO::getImageBUrl, reqVO.getImageBUrl())
                .eqIfPresent(PartFindDiffPairDO::getTopic, reqVO.getTopic())
                .eqIfPresent(PartFindDiffPairDO::getSort, reqVO.getSort())
                .betweenIfPresent(PartFindDiffPairDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartFindDiffPairDO::getId));
    }

}