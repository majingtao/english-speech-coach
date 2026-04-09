package cn.kugua.module.english.service.partInfoExchangeCard;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.partInfoExchangeCard.vo.*;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeCard.PartInfoExchangeCardDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.partInfoExchangeCard.PartInfoExchangeCardMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 信息互换 - 卡片 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartInfoExchangeCardServiceImpl implements PartInfoExchangeCardService {

    @Resource
    private PartInfoExchangeCardMapper partInfoExchangeCardMapper;

    @Override
    public Long createPartInfoExchangeCard(PartInfoExchangeCardSaveReqVO createReqVO) {
        // 插入
        PartInfoExchangeCardDO partInfoExchangeCard = BeanUtils.toBean(createReqVO, PartInfoExchangeCardDO.class);
        partInfoExchangeCardMapper.insert(partInfoExchangeCard);

        // 返回
        return partInfoExchangeCard.getId();
    }

    @Override
    public void updatePartInfoExchangeCard(PartInfoExchangeCardSaveReqVO updateReqVO) {
        // 校验存在
        validatePartInfoExchangeCardExists(updateReqVO.getId());
        // 更新
        PartInfoExchangeCardDO updateObj = BeanUtils.toBean(updateReqVO, PartInfoExchangeCardDO.class);
        partInfoExchangeCardMapper.updateById(updateObj);
    }

    @Override
    public void deletePartInfoExchangeCard(Long id) {
        // 校验存在
        validatePartInfoExchangeCardExists(id);
        // 删除
        partInfoExchangeCardMapper.deleteById(id);
    }

    @Override
        public void deletePartInfoExchangeCardListByIds(List<Long> ids) {
        // 删除
        partInfoExchangeCardMapper.deleteByIds(ids);
        }


    private void validatePartInfoExchangeCardExists(Long id) {
        if (partInfoExchangeCardMapper.selectById(id) == null) {
            throw exception(PART_INFO_EXCHANGE_CARD_NOT_EXISTS);
        }
    }

    @Override
    public PartInfoExchangeCardDO getPartInfoExchangeCard(Long id) {
        return partInfoExchangeCardMapper.selectById(id);
    }

    @Override
    public PageResult<PartInfoExchangeCardDO> getPartInfoExchangeCardPage(PartInfoExchangeCardPageReqVO pageReqVO) {
        return partInfoExchangeCardMapper.selectPage(pageReqVO);
    }

}