package cn.kugua.module.english.dal.mysql.partFindDiffDifference;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.partFindDiffDifference.PartFindDiffDifferenceDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.partFindDiffDifference.vo.*;

/**
 * 找不同 - 差异点 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartFindDiffDifferenceMapper extends BaseMapperX<PartFindDiffDifferenceDO> {

    default PageResult<PartFindDiffDifferenceDO> selectPage(PartFindDiffDifferencePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartFindDiffDifferenceDO>()
                .eqIfPresent(PartFindDiffDifferenceDO::getPairId, reqVO.getPairId())
                .eqIfPresent(PartFindDiffDifferenceDO::getDiffNo, reqVO.getDiffNo())
                .likeIfPresent(PartFindDiffDifferenceDO::getDescription, reqVO.getDescription())
                .likeIfPresent(PartFindDiffDifferenceDO::getKeyword, reqVO.getKeyword())
                .betweenIfPresent(PartFindDiffDifferenceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartFindDiffDifferenceDO::getId));
    }

}
