package cn.iocoder.yudao.module.english.dal.mysql.partInfoExchangeCard;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.english.dal.dataobject.partInfoExchangeCard.PartInfoExchangeCardDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.english.controller.admin.partInfoExchangeCard.vo.*;

/**
 * 信息互换 - 卡片 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartInfoExchangeCardMapper extends BaseMapperX<PartInfoExchangeCardDO> {

    default PageResult<PartInfoExchangeCardDO> selectPage(PartInfoExchangeCardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartInfoExchangeCardDO>()
                .eqIfPresent(PartInfoExchangeCardDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PartInfoExchangeCardDO::getPhase, reqVO.getPhase())
                .eqIfPresent(PartInfoExchangeCardDO::getCardTitle, reqVO.getCardTitle())
                .eqIfPresent(PartInfoExchangeCardDO::getCardImageUrl, reqVO.getCardImageUrl())
                .eqIfPresent(PartInfoExchangeCardDO::getIntro, reqVO.getIntro())
                .eqIfPresent(PartInfoExchangeCardDO::getSort, reqVO.getSort())
                .betweenIfPresent(PartInfoExchangeCardDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartInfoExchangeCardDO::getId));
    }

}