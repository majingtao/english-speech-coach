package cn.kugua.module.english.service.practiceSession;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.practiceSession.vo.*;
import cn.kugua.module.english.dal.dataobject.practiceSession.PracticeSessionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 练习会话 Service 接口
 *
 * @author 苦瓜
 */
public interface PracticeSessionService {

    /**
     * 创建练习会话
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPracticeSession(@Valid PracticeSessionSaveReqVO createReqVO);

    /**
     * 更新练习会话
     *
     * @param updateReqVO 更新信息
     */
    void updatePracticeSession(@Valid PracticeSessionSaveReqVO updateReqVO);

    /**
     * 删除练习会话
     *
     * @param id 编号
     */
    void deletePracticeSession(Long id);

    /**
    * 批量删除练习会话
    *
    * @param ids 编号
    */
    void deletePracticeSessionListByIds(List<Long> ids);

    /**
     * 获得练习会话
     *
     * @param id 编号
     * @return 练习会话
     */
    PracticeSessionDO getPracticeSession(Long id);

    /**
     * 获得练习会话分页
     *
     * @param pageReqVO 分页查询
     * @return 练习会话分页
     */
    PageResult<PracticeSessionDO> getPracticeSessionPage(PracticeSessionPageReqVO pageReqVO);

}