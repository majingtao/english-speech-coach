package cn.kugua.module.english.dal.mysql.aiModel;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelPageReqVO;
import cn.kugua.module.english.dal.dataobject.aiModel.EscAiModelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EscAiModelMapper extends BaseMapperX<EscAiModelDO> {

    default PageResult<EscAiModelDO> selectPage(EscAiModelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EscAiModelDO>()
                .eqIfPresent(EscAiModelDO::getType, reqVO.getType())
                .eqIfPresent(EscAiModelDO::getProvider, reqVO.getProvider())
                .likeIfPresent(EscAiModelDO::getLabel, reqVO.getLabel())
                .eqIfPresent(EscAiModelDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(EscAiModelDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(EscAiModelDO::getType)
                .orderByAsc(EscAiModelDO::getSort));
    }

    default List<EscAiModelDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<EscAiModelDO>()
                .eq(EscAiModelDO::getStatus, 0)
                .orderByAsc(EscAiModelDO::getType)
                .orderByAsc(EscAiModelDO::getSort));
    }
}
