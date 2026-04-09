package cn.iocoder.yudao.module.english.service.partGeneralConvoTopic;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.english.controller.admin.partGeneralConvoTopic.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partGeneralConvoTopic.PartGeneralConvoTopicDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.english.dal.mysql.partGeneralConvoTopic.PartGeneralConvoTopicMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.english.enums.ErrorCodeConstants.*;

/**
 * 一般对话 - 主题 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartGeneralConvoTopicServiceImpl implements PartGeneralConvoTopicService {

    @Resource
    private PartGeneralConvoTopicMapper partGeneralConvoTopicMapper;

    @Override
    public Long createPartGeneralConvoTopic(PartGeneralConvoTopicSaveReqVO createReqVO) {
        // 插入
        PartGeneralConvoTopicDO partGeneralConvoTopic = BeanUtils.toBean(createReqVO, PartGeneralConvoTopicDO.class);
        partGeneralConvoTopicMapper.insert(partGeneralConvoTopic);

        // 返回
        return partGeneralConvoTopic.getId();
    }

    @Override
    public void updatePartGeneralConvoTopic(PartGeneralConvoTopicSaveReqVO updateReqVO) {
        // 校验存在
        validatePartGeneralConvoTopicExists(updateReqVO.getId());
        // 更新
        PartGeneralConvoTopicDO updateObj = BeanUtils.toBean(updateReqVO, PartGeneralConvoTopicDO.class);
        partGeneralConvoTopicMapper.updateById(updateObj);
    }

    @Override
    public void deletePartGeneralConvoTopic(Long id) {
        // 校验存在
        validatePartGeneralConvoTopicExists(id);
        // 删除
        partGeneralConvoTopicMapper.deleteById(id);
    }

    @Override
        public void deletePartGeneralConvoTopicListByIds(List<Long> ids) {
        // 删除
        partGeneralConvoTopicMapper.deleteByIds(ids);
        }


    private void validatePartGeneralConvoTopicExists(Long id) {
        if (partGeneralConvoTopicMapper.selectById(id) == null) {
            throw exception(PART_GENERAL_CONVO_TOPIC_NOT_EXISTS);
        }
    }

    @Override
    public PartGeneralConvoTopicDO getPartGeneralConvoTopic(Long id) {
        return partGeneralConvoTopicMapper.selectById(id);
    }

    @Override
    public PageResult<PartGeneralConvoTopicDO> getPartGeneralConvoTopicPage(PartGeneralConvoTopicPageReqVO pageReqVO) {
        return partGeneralConvoTopicMapper.selectPage(pageReqVO);
    }

}