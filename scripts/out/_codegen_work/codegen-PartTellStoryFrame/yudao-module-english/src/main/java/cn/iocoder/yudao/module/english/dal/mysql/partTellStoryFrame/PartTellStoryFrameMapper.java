package cn.iocoder.yudao.module.english.dal.mysql.partTellStoryFrame;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.english.dal.dataobject.partTellStoryFrame.PartTellStoryFrameDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.english.controller.admin.partTellStoryFrame.vo.*;

/**
 * 讲故事 - 单帧 Mapper
 *
 * @author 苦瓜科技
 */
@Mapper
public interface PartTellStoryFrameMapper extends BaseMapperX<PartTellStoryFrameDO> {

    default PageResult<PartTellStoryFrameDO> selectPage(PartTellStoryFramePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartTellStoryFrameDO>()
                .eqIfPresent(PartTellStoryFrameDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PartTellStoryFrameDO::getFrameNo, reqVO.getFrameNo())
                .eqIfPresent(PartTellStoryFrameDO::getImageUrl, reqVO.getImageUrl())
                .eqIfPresent(PartTellStoryFrameDO::getSentenceText, reqVO.getSentenceText())
                .eqIfPresent(PartTellStoryFrameDO::getHint, reqVO.getHint())
                .eqIfPresent(PartTellStoryFrameDO::getSort, reqVO.getSort())
                .betweenIfPresent(PartTellStoryFrameDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartTellStoryFrameDO::getId));
    }

}