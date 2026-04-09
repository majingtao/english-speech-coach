package cn.kugua.module.english.dal.mysql.partGeneralConvoTopic;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.partGeneralConvoTopic.PartGeneralConvoTopicDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.partGeneralConvoTopic.vo.*;

/**
 * 一般对话 - 主题 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartGeneralConvoTopicMapper extends BaseMapperX<PartGeneralConvoTopicDO> {

    default PageResult<PartGeneralConvoTopicDO> selectPage(PartGeneralConvoTopicPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartGeneralConvoTopicDO>()
                .eqIfPresent(PartGeneralConvoTopicDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PartGeneralConvoTopicDO::getTopic, reqVO.getTopic())
                .eqIfPresent(PartGeneralConvoTopicDO::getIntro, reqVO.getIntro())
                .eqIfPresent(PartGeneralConvoTopicDO::getSort, reqVO.getSort())
                .betweenIfPresent(PartGeneralConvoTopicDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartGeneralConvoTopicDO::getId));
    }

}