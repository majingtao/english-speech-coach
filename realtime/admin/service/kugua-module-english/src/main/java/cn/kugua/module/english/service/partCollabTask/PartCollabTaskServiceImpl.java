package cn.kugua.module.english.service.partCollabTask;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.partCollabTask.vo.*;
import cn.kugua.module.english.dal.dataobject.partCollabTask.PartCollabTaskDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.partCollabTask.PartCollabTaskMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 协作任务 - 主体 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartCollabTaskServiceImpl implements PartCollabTaskService {

    @Resource
    private PartCollabTaskMapper partCollabTaskMapper;

    @Override
    public Long createPartCollabTask(PartCollabTaskSaveReqVO createReqVO) {
        // 插入
        PartCollabTaskDO partCollabTask = BeanUtils.toBean(createReqVO, PartCollabTaskDO.class);
        partCollabTaskMapper.insert(partCollabTask);

        // 返回
        return partCollabTask.getId();
    }

    @Override
    public void updatePartCollabTask(PartCollabTaskSaveReqVO updateReqVO) {
        // 校验存在
        validatePartCollabTaskExists(updateReqVO.getId());
        // 更新
        PartCollabTaskDO updateObj = BeanUtils.toBean(updateReqVO, PartCollabTaskDO.class);
        partCollabTaskMapper.updateById(updateObj);
    }

    @Override
    public void deletePartCollabTask(Long id) {
        // 校验存在
        validatePartCollabTaskExists(id);
        // 删除
        partCollabTaskMapper.deleteById(id);
    }

    @Override
        public void deletePartCollabTaskListByIds(List<Long> ids) {
        // 删除
        partCollabTaskMapper.deleteByIds(ids);
        }


    private void validatePartCollabTaskExists(Long id) {
        if (partCollabTaskMapper.selectById(id) == null) {
            throw exception(PART_COLLAB_TASK_NOT_EXISTS);
        }
    }

    @Override
    public PartCollabTaskDO getPartCollabTask(Long id) {
        return partCollabTaskMapper.selectById(id);
    }

    @Override
    public PageResult<PartCollabTaskDO> getPartCollabTaskPage(PartCollabTaskPageReqVO pageReqVO) {
        return partCollabTaskMapper.selectPage(pageReqVO);
    }

}