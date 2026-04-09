package cn.iocoder.yudao.module.english.service.partLongTurnPhoto;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.english.controller.admin.partLongTurnPhoto.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partLongTurnPhoto.PartLongTurnPhotoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 独立长答 - 图片描述 Service 接口
 *
 * @author 苦瓜
 */
public interface PartLongTurnPhotoService {

    /**
     * 创建独立长答 - 图片描述
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPartLongTurnPhoto(@Valid PartLongTurnPhotoSaveReqVO createReqVO);

    /**
     * 更新独立长答 - 图片描述
     *
     * @param updateReqVO 更新信息
     */
    void updatePartLongTurnPhoto(@Valid PartLongTurnPhotoSaveReqVO updateReqVO);

    /**
     * 删除独立长答 - 图片描述
     *
     * @param id 编号
     */
    void deletePartLongTurnPhoto(Long id);

    /**
    * 批量删除独立长答 - 图片描述
    *
    * @param ids 编号
    */
    void deletePartLongTurnPhotoListByIds(List<Long> ids);

    /**
     * 获得独立长答 - 图片描述
     *
     * @param id 编号
     * @return 独立长答 - 图片描述
     */
    PartLongTurnPhotoDO getPartLongTurnPhoto(Long id);

    /**
     * 获得独立长答 - 图片描述分页
     *
     * @param pageReqVO 分页查询
     * @return 独立长答 - 图片描述分页
     */
    PageResult<PartLongTurnPhotoDO> getPartLongTurnPhotoPage(PartLongTurnPhotoPageReqVO pageReqVO);

}