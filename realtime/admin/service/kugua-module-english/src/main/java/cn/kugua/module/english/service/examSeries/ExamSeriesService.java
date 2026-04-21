package cn.kugua.module.english.service.examSeries;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.examSeries.vo.*;
import cn.kugua.module.english.dal.dataobject.examSeries.ExamSeriesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 考试系列字典 Service 接口
 *
 * @author 苦瓜
 */
public interface ExamSeriesService {

    Long createExamSeries(@Valid ExamSeriesSaveReqVO createReqVO);

    void updateExamSeries(@Valid ExamSeriesSaveReqVO updateReqVO);

    void deleteExamSeries(Long id);

    void deleteExamSeriesListByIds(List<Long> ids);

    ExamSeriesDO getExamSeries(Long id);

    PageResult<ExamSeriesDO> getExamSeriesPage(ExamSeriesPageReqVO pageReqVO);

    /**
     * 列出全部启用的系列（按 level → sort 排序），供下拉/级联使用
     */
    List<ExamSeriesDO> listEnabled();

}
