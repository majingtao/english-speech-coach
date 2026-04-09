package cn.iocoder.yudao.module.english.dal.mysql.partCollabTask;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.english.dal.dataobject.partCollabTask.PartCollabTaskDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.english.controller.admin.partCollabTask.vo.*;

/**
 * 协作任务 - 主体 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface PartCollabTaskMapper extends BaseMapperX<PartCollabTaskDO> {

    default PageResult<PartCollabTaskDO> selectPage(PartCollabTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartCollabTaskDO>()
                .eqIfPresent(PartCollabTaskDO::getPartId, reqVO.getPartId())
                .eqIfPresent(PartCollabTaskDO::getScenario, reqVO.getScenario())
                .eqIfPresent(PartCollabTaskDO::getImageUrl, reqVO.getImageUrl())
                .eqIfPresent(PartCollabTaskDO::getInstruction, reqVO.getInstruction())
                .betweenIfPresent(PartCollabTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartCollabTaskDO::getId));
    }

}