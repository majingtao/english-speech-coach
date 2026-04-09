package cn.kugua.module.english.service.partFindDiffPair;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.partFindDiffPair.vo.*;
import cn.kugua.module.english.dal.dataobject.partFindDiffPair.PartFindDiffPairDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.partFindDiffPair.PartFindDiffPairMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 找不同 - 图对 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartFindDiffPairServiceImpl implements PartFindDiffPairService {

    @Resource
    private PartFindDiffPairMapper partFindDiffPairMapper;

    @Override
    public Long createPartFindDiffPair(PartFindDiffPairSaveReqVO createReqVO) {
        // 插入
        PartFindDiffPairDO partFindDiffPair = BeanUtils.toBean(createReqVO, PartFindDiffPairDO.class);
        partFindDiffPairMapper.insert(partFindDiffPair);

        // 返回
        return partFindDiffPair.getId();
    }

    @Override
    public void updatePartFindDiffPair(PartFindDiffPairSaveReqVO updateReqVO) {
        // 校验存在
        validatePartFindDiffPairExists(updateReqVO.getId());
        // 更新
        PartFindDiffPairDO updateObj = BeanUtils.toBean(updateReqVO, PartFindDiffPairDO.class);
        partFindDiffPairMapper.updateById(updateObj);
    }

    @Override
    public void deletePartFindDiffPair(Long id) {
        // 校验存在
        validatePartFindDiffPairExists(id);
        // 删除
        partFindDiffPairMapper.deleteById(id);
    }

    @Override
        public void deletePartFindDiffPairListByIds(List<Long> ids) {
        // 删除
        partFindDiffPairMapper.deleteByIds(ids);
        }


    private void validatePartFindDiffPairExists(Long id) {
        if (partFindDiffPairMapper.selectById(id) == null) {
            throw exception(PART_FIND_DIFF_PAIR_NOT_EXISTS);
        }
    }

    @Override
    public PartFindDiffPairDO getPartFindDiffPair(Long id) {
        return partFindDiffPairMapper.selectById(id);
    }

    @Override
    public PageResult<PartFindDiffPairDO> getPartFindDiffPairPage(PartFindDiffPairPageReqVO pageReqVO) {
        return partFindDiffPairMapper.selectPage(pageReqVO);
    }

}