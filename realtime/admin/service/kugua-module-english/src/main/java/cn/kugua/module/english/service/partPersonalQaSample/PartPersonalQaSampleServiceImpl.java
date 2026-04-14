package cn.kugua.module.english.service.partPersonalQaSample;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.kugua.module.english.controller.admin.partPersonalQaSample.vo.*;
import cn.kugua.module.english.dal.dataobject.partPersonalQaSample.PartPersonalQaSampleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.partPersonalQaSample.PartPersonalQaSampleMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 个人问答 - 示例答案 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartPersonalQaSampleServiceImpl implements PartPersonalQaSampleService {

    @Resource
    private PartPersonalQaSampleMapper partPersonalQaSampleMapper;

    @Override
    public Long createPartPersonalQaSample(PartPersonalQaSampleSaveReqVO createReqVO) {
        PartPersonalQaSampleDO obj = BeanUtils.toBean(createReqVO, PartPersonalQaSampleDO.class);
        partPersonalQaSampleMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updatePartPersonalQaSample(PartPersonalQaSampleSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        PartPersonalQaSampleDO updateObj = BeanUtils.toBean(updateReqVO, PartPersonalQaSampleDO.class);
        partPersonalQaSampleMapper.updateById(updateObj);
    }

    @Override
    public void deletePartPersonalQaSample(Long id) {
        validateExists(id);
        partPersonalQaSampleMapper.deleteById(id);
    }

    @Override
    public void deletePartPersonalQaSampleListByIds(List<Long> ids) {
        partPersonalQaSampleMapper.deleteByIds(ids);
    }

    private void validateExists(Long id) {
        if (partPersonalQaSampleMapper.selectById(id) == null) {
            throw exception(PART_PERSONAL_QA_SAMPLE_NOT_EXISTS);
        }
    }

    @Override
    public PartPersonalQaSampleDO getPartPersonalQaSample(Long id) {
        return partPersonalQaSampleMapper.selectById(id);
    }

    @Override
    public PageResult<PartPersonalQaSampleDO> getPartPersonalQaSamplePage(PartPersonalQaSamplePageReqVO pageReqVO) {
        return partPersonalQaSampleMapper.selectPage(pageReqVO);
    }

}
