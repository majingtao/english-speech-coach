package cn.kugua.module.english.service.partTellStoryFrame;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.partTellStoryFrame.vo.*;
import cn.kugua.module.english.dal.dataobject.partTellStoryFrame.PartTellStoryFrameDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 讲故事 - 单帧 Service 接口
 *
 * @author 苦瓜科技
 */
public interface PartTellStoryFrameService {

    /**
     * 创建讲故事 - 单帧
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPartTellStoryFrame(@Valid PartTellStoryFrameSaveReqVO createReqVO);

    /**
     * 更新讲故事 - 单帧
     *
     * @param updateReqVO 更新信息
     */
    void updatePartTellStoryFrame(@Valid PartTellStoryFrameSaveReqVO updateReqVO);

    /**
     * 删除讲故事 - 单帧
     *
     * @param id 编号
     */
    void deletePartTellStoryFrame(Long id);

    /**
    * 批量删除讲故事 - 单帧
    *
    * @param ids 编号
    */
    void deletePartTellStoryFrameListByIds(List<Long> ids);

    /**
     * 获得讲故事 - 单帧
     *
     * @param id 编号
     * @return 讲故事 - 单帧
     */
    PartTellStoryFrameDO getPartTellStoryFrame(Long id);

    /**
     * 获得讲故事 - 单帧分页
     *
     * @param pageReqVO 分页查询
     * @return 讲故事 - 单帧分页
     */
    PageResult<PartTellStoryFrameDO> getPartTellStoryFramePage(PartTellStoryFramePageReqVO pageReqVO);

}