package cn.kugua.module.english.service.partInfoExchangeCard;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.partInfoExchangeCard.vo.*;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeCard.PartInfoExchangeCardDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 信息互换 - 卡片 Service 接口
 *
 * @author 苦瓜
 */
public interface PartInfoExchangeCardService {

    /**
     * 创建信息互换 - 卡片
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPartInfoExchangeCard(@Valid PartInfoExchangeCardSaveReqVO createReqVO);

    /**
     * 更新信息互换 - 卡片
     *
     * @param updateReqVO 更新信息
     */
    void updatePartInfoExchangeCard(@Valid PartInfoExchangeCardSaveReqVO updateReqVO);

    /**
     * 删除信息互换 - 卡片
     *
     * @param id 编号
     */
    void deletePartInfoExchangeCard(Long id);

    /**
    * 批量删除信息互换 - 卡片
    *
    * @param ids 编号
    */
    void deletePartInfoExchangeCardListByIds(List<Long> ids);

    /**
     * 获得信息互换 - 卡片
     *
     * @param id 编号
     * @return 信息互换 - 卡片
     */
    PartInfoExchangeCardDO getPartInfoExchangeCard(Long id);

    /**
     * 获得信息互换 - 卡片分页
     *
     * @param pageReqVO 分页查询
     * @return 信息互换 - 卡片分页
     */
    PageResult<PartInfoExchangeCardDO> getPartInfoExchangeCardPage(PartInfoExchangeCardPageReqVO pageReqVO);

}