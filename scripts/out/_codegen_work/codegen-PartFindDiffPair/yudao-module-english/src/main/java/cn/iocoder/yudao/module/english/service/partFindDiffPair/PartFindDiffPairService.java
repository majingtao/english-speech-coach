package cn.iocoder.yudao.module.english.service.partFindDiffPair;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.english.controller.admin.partFindDiffPair.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partFindDiffPair.PartFindDiffPairDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 找不同 - 图对 Service 接口
 *
 * @author 苦瓜
 */
public interface PartFindDiffPairService {

    /**
     * 创建找不同 - 图对
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPartFindDiffPair(@Valid PartFindDiffPairSaveReqVO createReqVO);

    /**
     * 更新找不同 - 图对
     *
     * @param updateReqVO 更新信息
     */
    void updatePartFindDiffPair(@Valid PartFindDiffPairSaveReqVO updateReqVO);

    /**
     * 删除找不同 - 图对
     *
     * @param id 编号
     */
    void deletePartFindDiffPair(Long id);

    /**
    * 批量删除找不同 - 图对
    *
    * @param ids 编号
    */
    void deletePartFindDiffPairListByIds(List<Long> ids);

    /**
     * 获得找不同 - 图对
     *
     * @param id 编号
     * @return 找不同 - 图对
     */
    PartFindDiffPairDO getPartFindDiffPair(Long id);

    /**
     * 获得找不同 - 图对分页
     *
     * @param pageReqVO 分页查询
     * @return 找不同 - 图对分页
     */
    PageResult<PartFindDiffPairDO> getPartFindDiffPairPage(PartFindDiffPairPageReqVO pageReqVO);

}