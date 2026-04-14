package cn.kugua.module.english.dal.mysql.partInfoExchangeQa;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeQa.PartInfoExchangeQaDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.partInfoExchangeQa.vo.*;

/**
 * 信息互换 - 问答条目 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartInfoExchangeQaMapper extends BaseMapperX<PartInfoExchangeQaDO> {

    default PageResult<PartInfoExchangeQaDO> selectPage(PartInfoExchangeQaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartInfoExchangeQaDO>()
                .eqIfPresent(PartInfoExchangeQaDO::getCardId, reqVO.getCardId())
                .eqIfPresent(PartInfoExchangeQaDO::getQaNo, reqVO.getQaNo())
                .likeIfPresent(PartInfoExchangeQaDO::getPromptLabel, reqVO.getPromptLabel())
                .likeIfPresent(PartInfoExchangeQaDO::getQuestionText, reqVO.getQuestionText())
                .likeIfPresent(PartInfoExchangeQaDO::getAnswerText, reqVO.getAnswerText())
                .betweenIfPresent(PartInfoExchangeQaDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartInfoExchangeQaDO::getId));
    }

}
