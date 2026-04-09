package cn.iocoder.yudao.module.english.service.partGeneralConvoTopic;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.english.controller.admin.partGeneralConvoTopic.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partGeneralConvoTopic.PartGeneralConvoTopicDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 一般对话 - 主题 Service 接口
 *
 * @author 苦瓜
 */
public interface PartGeneralConvoTopicService {

    /**
     * 创建一般对话 - 主题
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPartGeneralConvoTopic(@Valid PartGeneralConvoTopicSaveReqVO createReqVO);

    /**
     * 更新一般对话 - 主题
     *
     * @param updateReqVO 更新信息
     */
    void updatePartGeneralConvoTopic(@Valid PartGeneralConvoTopicSaveReqVO updateReqVO);

    /**
     * 删除一般对话 - 主题
     *
     * @param id 编号
     */
    void deletePartGeneralConvoTopic(Long id);

    /**
    * 批量删除一般对话 - 主题
    *
    * @param ids 编号
    */
    void deletePartGeneralConvoTopicListByIds(List<Long> ids);

    /**
     * 获得一般对话 - 主题
     *
     * @param id 编号
     * @return 一般对话 - 主题
     */
    PartGeneralConvoTopicDO getPartGeneralConvoTopic(Long id);

    /**
     * 获得一般对话 - 主题分页
     *
     * @param pageReqVO 分页查询
     * @return 一般对话 - 主题分页
     */
    PageResult<PartGeneralConvoTopicDO> getPartGeneralConvoTopicPage(PartGeneralConvoTopicPageReqVO pageReqVO);

}