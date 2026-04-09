package cn.kugua.module.english.service.examLevel;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.examLevel.vo.*;
import cn.kugua.module.english.dal.dataobject.examLevel.ExamLevelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 考试级别字典 Service 接口
 *
 * @author 苦瓜
 */
public interface ExamLevelService {

    /**
     * 创建考试级别字典
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExamLevel(@Valid ExamLevelSaveReqVO createReqVO);

    /**
     * 更新考试级别字典
     *
     * @param updateReqVO 更新信息
     */
    void updateExamLevel(@Valid ExamLevelSaveReqVO updateReqVO);

    /**
     * 删除考试级别字典
     *
     * @param id 编号
     */
    void deleteExamLevel(Long id);

    /**
    * 批量删除考试级别字典
    *
    * @param ids 编号
    */
    void deleteExamLevelListByIds(List<Long> ids);

    /**
     * 获得考试级别字典
     *
     * @param id 编号
     * @return 考试级别字典
     */
    ExamLevelDO getExamLevel(Long id);

    /**
     * 获得考试级别字典分页
     *
     * @param pageReqVO 分页查询
     * @return 考试级别字典分页
     */
    PageResult<ExamLevelDO> getExamLevelPage(ExamLevelPageReqVO pageReqVO);

}