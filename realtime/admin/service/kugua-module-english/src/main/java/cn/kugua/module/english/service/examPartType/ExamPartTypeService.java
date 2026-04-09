package cn.kugua.module.english.service.examPartType;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.examPartType.vo.*;
import cn.kugua.module.english.dal.dataobject.examPartType.ExamPartTypeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 题型字典 Service 接口
 *
 * @author 苦瓜
 */
public interface ExamPartTypeService {

    /**
     * 创建题型字典
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExamPartType(@Valid ExamPartTypeSaveReqVO createReqVO);

    /**
     * 更新题型字典
     *
     * @param updateReqVO 更新信息
     */
    void updateExamPartType(@Valid ExamPartTypeSaveReqVO updateReqVO);

    /**
     * 删除题型字典
     *
     * @param id 编号
     */
    void deleteExamPartType(Long id);

    /**
    * 批量删除题型字典
    *
    * @param ids 编号
    */
    void deleteExamPartTypeListByIds(List<Long> ids);

    /**
     * 获得题型字典
     *
     * @param id 编号
     * @return 题型字典
     */
    ExamPartTypeDO getExamPartType(Long id);

    /**
     * 获得题型字典分页
     *
     * @param pageReqVO 分页查询
     * @return 题型字典分页
     */
    PageResult<ExamPartTypeDO> getExamPartTypePage(ExamPartTypePageReqVO pageReqVO);

}