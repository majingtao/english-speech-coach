package cn.kugua.module.english.service.partTellStoryFrame;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.partTellStoryFrame.vo.*;
import cn.kugua.module.english.dal.dataobject.partTellStoryFrame.PartTellStoryFrameDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.partTellStoryFrame.PartTellStoryFrameMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 讲故事 - 单帧 Service 实现类
 *
 * @author 苦瓜科技
 */
@Service
@Validated
public class PartTellStoryFrameServiceImpl implements PartTellStoryFrameService {

    @Resource
    private PartTellStoryFrameMapper partTellStoryFrameMapper;

    @Override
    public Long createPartTellStoryFrame(PartTellStoryFrameSaveReqVO createReqVO) {
        // 插入
        PartTellStoryFrameDO partTellStoryFrame = BeanUtils.toBean(createReqVO, PartTellStoryFrameDO.class);
        partTellStoryFrameMapper.insert(partTellStoryFrame);

        // 返回
        return partTellStoryFrame.getId();
    }

    @Override
    public void updatePartTellStoryFrame(PartTellStoryFrameSaveReqVO updateReqVO) {
        // 校验存在
        validatePartTellStoryFrameExists(updateReqVO.getId());
        // 更新
        PartTellStoryFrameDO updateObj = BeanUtils.toBean(updateReqVO, PartTellStoryFrameDO.class);
        partTellStoryFrameMapper.updateById(updateObj);
    }

    @Override
    public void deletePartTellStoryFrame(Long id) {
        // 校验存在
        validatePartTellStoryFrameExists(id);
        // 删除
        partTellStoryFrameMapper.deleteById(id);
    }

    @Override
        public void deletePartTellStoryFrameListByIds(List<Long> ids) {
        // 删除
        partTellStoryFrameMapper.deleteByIds(ids);
        }


    private void validatePartTellStoryFrameExists(Long id) {
        if (partTellStoryFrameMapper.selectById(id) == null) {
            throw exception(PART_TELL_STORY_FRAME_NOT_EXISTS);
        }
    }

    @Override
    public PartTellStoryFrameDO getPartTellStoryFrame(Long id) {
        return partTellStoryFrameMapper.selectById(id);
    }

    @Override
    public PageResult<PartTellStoryFrameDO> getPartTellStoryFramePage(PartTellStoryFramePageReqVO pageReqVO) {
        return partTellStoryFrameMapper.selectPage(pageReqVO);
    }

}