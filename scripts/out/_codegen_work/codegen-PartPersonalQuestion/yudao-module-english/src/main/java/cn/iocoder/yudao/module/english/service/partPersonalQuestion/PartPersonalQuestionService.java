package cn.iocoder.yudao.module.english.service.partPersonalQuestion;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.english.controller.admin.partPersonalQuestion.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partPersonalQuestion.PartPersonalQuestionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 个人问答 - 问题 Service 接口
 *
 * @author 苦瓜科技
 */
public interface PartPersonalQuestionService {

    /**
     * 创建个人问答 - 问题
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPartPersonalQuestion(@Valid PartPersonalQuestionSaveReqVO createReqVO);

    /**
     * 更新个人问答 - 问题
     *
     * @param updateReqVO 更新信息
     */
    void updatePartPersonalQuestion(@Valid PartPersonalQuestionSaveReqVO updateReqVO);

    /**
     * 删除个人问答 - 问题
     *
     * @param id 编号
     */
    void deletePartPersonalQuestion(Long id);

    /**
    * 批量删除个人问答 - 问题
    *
    * @param ids 编号
    */
    void deletePartPersonalQuestionListByIds(List<Long> ids);

    /**
     * 获得个人问答 - 问题
     *
     * @param id 编号
     * @return 个人问答 - 问题
     */
    PartPersonalQuestionDO getPartPersonalQuestion(Long id);

    /**
     * 获得个人问答 - 问题分页
     *
     * @param pageReqVO 分页查询
     * @return 个人问答 - 问题分页
     */
    PageResult<PartPersonalQuestionDO> getPartPersonalQuestionPage(PartPersonalQuestionPageReqVO pageReqVO);

}