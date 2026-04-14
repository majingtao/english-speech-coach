package cn.kugua.module.english.service.partFindDiffDifference;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.partFindDiffDifference.vo.*;
import cn.kugua.module.english.dal.dataobject.partFindDiffDifference.PartFindDiffDifferenceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 找不同 - 差异点 Service 接口
 *
 * @author 苦瓜
 */
public interface PartFindDiffDifferenceService {

    Long createPartFindDiffDifference(@Valid PartFindDiffDifferenceSaveReqVO createReqVO);

    void updatePartFindDiffDifference(@Valid PartFindDiffDifferenceSaveReqVO updateReqVO);

    void deletePartFindDiffDifference(Long id);

    void deletePartFindDiffDifferenceListByIds(List<Long> ids);

    PartFindDiffDifferenceDO getPartFindDiffDifference(Long id);

    PageResult<PartFindDiffDifferenceDO> getPartFindDiffDifferencePage(PartFindDiffDifferencePageReqVO pageReqVO);

}
