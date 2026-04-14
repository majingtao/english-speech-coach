package cn.kugua.module.english.service.partFindDiffDifference;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.kugua.module.english.controller.admin.partFindDiffDifference.vo.*;
import cn.kugua.module.english.dal.dataobject.partFindDiffDifference.PartFindDiffDifferenceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.partFindDiffDifference.PartFindDiffDifferenceMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 找不同 - 差异点 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartFindDiffDifferenceServiceImpl implements PartFindDiffDifferenceService {

    @Resource
    private PartFindDiffDifferenceMapper partFindDiffDifferenceMapper;

    @Override
    public Long createPartFindDiffDifference(PartFindDiffDifferenceSaveReqVO createReqVO) {
        PartFindDiffDifferenceDO obj = BeanUtils.toBean(createReqVO, PartFindDiffDifferenceDO.class);
        partFindDiffDifferenceMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updatePartFindDiffDifference(PartFindDiffDifferenceSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        PartFindDiffDifferenceDO updateObj = BeanUtils.toBean(updateReqVO, PartFindDiffDifferenceDO.class);
        partFindDiffDifferenceMapper.updateById(updateObj);
    }

    @Override
    public void deletePartFindDiffDifference(Long id) {
        validateExists(id);
        partFindDiffDifferenceMapper.deleteById(id);
    }

    @Override
    public void deletePartFindDiffDifferenceListByIds(List<Long> ids) {
        partFindDiffDifferenceMapper.deleteByIds(ids);
    }

    private void validateExists(Long id) {
        if (partFindDiffDifferenceMapper.selectById(id) == null) {
            throw exception(PART_FIND_DIFF_DIFFERENCE_NOT_EXISTS);
        }
    }

    @Override
    public PartFindDiffDifferenceDO getPartFindDiffDifference(Long id) {
        return partFindDiffDifferenceMapper.selectById(id);
    }

    @Override
    public PageResult<PartFindDiffDifferenceDO> getPartFindDiffDifferencePage(PartFindDiffDifferencePageReqVO pageReqVO) {
        return partFindDiffDifferenceMapper.selectPage(pageReqVO);
    }

}
