package cn.kugua.module.english.service.practiceAnswer;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.practiceAnswer.vo.*;
import cn.kugua.module.english.dal.dataobject.practiceAnswer.PracticeAnswerDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 练习单题作答 Service 接口
 *
 * @author 苦瓜
 */
public interface PracticeAnswerService {

    /**
     * 创建练习单题作答
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPracticeAnswer(@Valid PracticeAnswerSaveReqVO createReqVO);

    /**
     * 更新练习单题作答
     *
     * @param updateReqVO 更新信息
     */
    void updatePracticeAnswer(@Valid PracticeAnswerSaveReqVO updateReqVO);

    /**
     * 删除练习单题作答
     *
     * @param id 编号
     */
    void deletePracticeAnswer(Long id);

    /**
    * 批量删除练习单题作答
    *
    * @param ids 编号
    */
    void deletePracticeAnswerListByIds(List<Long> ids);

    /**
     * 获得练习单题作答
     *
     * @param id 编号
     * @return 练习单题作答
     */
    PracticeAnswerDO getPracticeAnswer(Long id);

    /**
     * 获得练习单题作答分页
     *
     * @param pageReqVO 分页查询
     * @return 练习单题作答分页
     */
    PageResult<PracticeAnswerDO> getPracticeAnswerPage(PracticeAnswerPageReqVO pageReqVO);

}