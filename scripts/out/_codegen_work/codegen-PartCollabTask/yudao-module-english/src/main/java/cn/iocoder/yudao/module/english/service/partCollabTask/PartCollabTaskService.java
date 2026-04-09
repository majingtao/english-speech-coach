package cn.iocoder.yudao.module.english.service.partCollabTask;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.english.controller.admin.partCollabTask.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partCollabTask.PartCollabTaskDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 协作任务 - 主体 Service 接口
 *
 * @author 苦瓜
 */
public interface PartCollabTaskService {

    /**
     * 创建协作任务 - 主体
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPartCollabTask(@Valid PartCollabTaskSaveReqVO createReqVO);

    /**
     * 更新协作任务 - 主体
     *
     * @param updateReqVO 更新信息
     */
    void updatePartCollabTask(@Valid PartCollabTaskSaveReqVO updateReqVO);

    /**
     * 删除协作任务 - 主体
     *
     * @param id 编号
     */
    void deletePartCollabTask(Long id);

    /**
    * 批量删除协作任务 - 主体
    *
    * @param ids 编号
    */
    void deletePartCollabTaskListByIds(List<Long> ids);

    /**
     * 获得协作任务 - 主体
     *
     * @param id 编号
     * @return 协作任务 - 主体
     */
    PartCollabTaskDO getPartCollabTask(Long id);

    /**
     * 获得协作任务 - 主体分页
     *
     * @param pageReqVO 分页查询
     * @return 协作任务 - 主体分页
     */
    PageResult<PartCollabTaskDO> getPartCollabTaskPage(PartCollabTaskPageReqVO pageReqVO);

}