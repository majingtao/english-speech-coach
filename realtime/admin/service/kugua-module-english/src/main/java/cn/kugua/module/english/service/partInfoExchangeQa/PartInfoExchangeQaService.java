package cn.kugua.module.english.service.partInfoExchangeQa;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.partInfoExchangeQa.vo.*;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeQa.PartInfoExchangeQaDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 信息互换 - 问答条目 Service 接口
 *
 * @author 苦瓜
 */
public interface PartInfoExchangeQaService {

    Long createPartInfoExchangeQa(@Valid PartInfoExchangeQaSaveReqVO createReqVO);

    void updatePartInfoExchangeQa(@Valid PartInfoExchangeQaSaveReqVO updateReqVO);

    void deletePartInfoExchangeQa(Long id);

    void deletePartInfoExchangeQaListByIds(List<Long> ids);

    PartInfoExchangeQaDO getPartInfoExchangeQa(Long id);

    PageResult<PartInfoExchangeQaDO> getPartInfoExchangeQaPage(PartInfoExchangeQaPageReqVO pageReqVO);

}
