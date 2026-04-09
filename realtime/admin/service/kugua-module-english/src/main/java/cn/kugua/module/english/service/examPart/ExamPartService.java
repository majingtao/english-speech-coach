package cn.kugua.module.english.service.examPart;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.examPart.vo.*;
import cn.kugua.module.english.dal.dataobject.examPart.ExamPartDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 试卷 Part 多态头 Service 接口
 *
 * @author 苦瓜
 */
public interface ExamPartService {

    /**
     * 创建试卷 Part 多态头
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExamPart(@Valid ExamPartSaveReqVO createReqVO);

    /**
     * 更新试卷 Part 多态头
     *
     * @param updateReqVO 更新信息
     */
    void updateExamPart(@Valid ExamPartSaveReqVO updateReqVO);

    /**
     * 删除试卷 Part 多态头
     *
     * @param id 编号
     */
    void deleteExamPart(Long id);

    /**
    * 批量删除试卷 Part 多态头
    *
    * @param ids 编号
    */
    void deleteExamPartListByIds(List<Long> ids);

    /**
     * 获得试卷 Part 多态头
     *
     * @param id 编号
     * @return 试卷 Part 多态头
     */
    ExamPartDO getExamPart(Long id);

    /**
     * 获得试卷 Part 多态头分页
     *
     * @param pageReqVO 分页查询
     * @return 试卷 Part 多态头分页
     */
    PageResult<ExamPartDO> getExamPartPage(ExamPartPageReqVO pageReqVO);

}