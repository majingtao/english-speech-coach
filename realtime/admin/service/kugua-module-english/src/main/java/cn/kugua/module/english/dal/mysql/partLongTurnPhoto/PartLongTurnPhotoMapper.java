package cn.kugua.module.english.dal.mysql.partLongTurnPhoto;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.partLongTurnPhoto.PartLongTurnPhotoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.partLongTurnPhoto.vo.*;

/**
 * 独立长答 - 图片描述 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartLongTurnPhotoMapper extends BaseMapperX<PartLongTurnPhotoDO> {

    default PageResult<PartLongTurnPhotoDO> selectPage(PartLongTurnPhotoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartLongTurnPhotoDO>()
                .eqIfPresent(PartLongTurnPhotoDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PartLongTurnPhotoDO::getCandidateRole, reqVO.getCandidateRole())
                .eqIfPresent(PartLongTurnPhotoDO::getTopic, reqVO.getTopic())
                .eqIfPresent(PartLongTurnPhotoDO::getImageUrl, reqVO.getImageUrl())
                .eqIfPresent(PartLongTurnPhotoDO::getInstruction, reqVO.getInstruction())
                .eqIfPresent(PartLongTurnPhotoDO::getSort, reqVO.getSort())
                .betweenIfPresent(PartLongTurnPhotoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartLongTurnPhotoDO::getId));
    }

}