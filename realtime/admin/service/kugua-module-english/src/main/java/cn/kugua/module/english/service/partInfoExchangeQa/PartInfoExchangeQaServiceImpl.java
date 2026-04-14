package cn.kugua.module.english.service.partInfoExchangeQa;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.kugua.module.english.controller.admin.partInfoExchangeQa.vo.*;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeQa.PartInfoExchangeQaDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.partInfoExchangeQa.PartInfoExchangeQaMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 信息互换 - 问答条目 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartInfoExchangeQaServiceImpl implements PartInfoExchangeQaService {

    @Resource
    private PartInfoExchangeQaMapper partInfoExchangeQaMapper;

    @Override
    public Long createPartInfoExchangeQa(PartInfoExchangeQaSaveReqVO createReqVO) {
        PartInfoExchangeQaDO obj = BeanUtils.toBean(createReqVO, PartInfoExchangeQaDO.class);
        partInfoExchangeQaMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updatePartInfoExchangeQa(PartInfoExchangeQaSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        PartInfoExchangeQaDO updateObj = BeanUtils.toBean(updateReqVO, PartInfoExchangeQaDO.class);
        partInfoExchangeQaMapper.updateById(updateObj);
    }

    @Override
    public void deletePartInfoExchangeQa(Long id) {
        validateExists(id);
        partInfoExchangeQaMapper.deleteById(id);
    }

    @Override
    public void deletePartInfoExchangeQaListByIds(List<Long> ids) {
        partInfoExchangeQaMapper.deleteByIds(ids);
    }

    private void validateExists(Long id) {
        if (partInfoExchangeQaMapper.selectById(id) == null) {
            throw exception(PART_INFO_EXCHANGE_QA_NOT_EXISTS);
        }
    }

    @Override
    public PartInfoExchangeQaDO getPartInfoExchangeQa(Long id) {
        return partInfoExchangeQaMapper.selectById(id);
    }

    @Override
    public PageResult<PartInfoExchangeQaDO> getPartInfoExchangeQaPage(PartInfoExchangeQaPageReqVO pageReqVO) {
        return partInfoExchangeQaMapper.selectPage(pageReqVO);
    }

}
