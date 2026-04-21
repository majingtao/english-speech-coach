package cn.kugua.module.english.service.examSeries;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.kugua.module.english.controller.admin.examSeries.vo.*;
import cn.kugua.module.english.dal.dataobject.examSeries.ExamSeriesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.examSeries.ExamSeriesMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 考试系列字典 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class ExamSeriesServiceImpl implements ExamSeriesService {

    @Resource
    private ExamSeriesMapper examSeriesMapper;

    @Override
    public Long createExamSeries(ExamSeriesSaveReqVO createReqVO) {
        ExamSeriesDO examSeries = BeanUtils.toBean(createReqVO, ExamSeriesDO.class);
        examSeriesMapper.insert(examSeries);
        return examSeries.getId();
    }

    @Override
    public void updateExamSeries(ExamSeriesSaveReqVO updateReqVO) {
        validateExamSeriesExists(updateReqVO.getId());
        ExamSeriesDO updateObj = BeanUtils.toBean(updateReqVO, ExamSeriesDO.class);
        examSeriesMapper.updateById(updateObj);
    }

    @Override
    public void deleteExamSeries(Long id) {
        validateExamSeriesExists(id);
        examSeriesMapper.deleteById(id);
    }

    @Override
    public void deleteExamSeriesListByIds(List<Long> ids) {
        examSeriesMapper.deleteByIds(ids);
    }

    private void validateExamSeriesExists(Long id) {
        if (examSeriesMapper.selectById(id) == null) {
            throw exception(EXAM_SERIES_NOT_EXISTS);
        }
    }

    @Override
    public ExamSeriesDO getExamSeries(Long id) {
        return examSeriesMapper.selectById(id);
    }

    @Override
    public PageResult<ExamSeriesDO> getExamSeriesPage(ExamSeriesPageReqVO pageReqVO) {
        return examSeriesMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ExamSeriesDO> listEnabled() {
        return examSeriesMapper.selectList(Wrappers.<ExamSeriesDO>lambdaQuery()
                .eq(ExamSeriesDO::getStatus, 0)
                .orderByAsc(ExamSeriesDO::getLevelCode)
                .orderByAsc(ExamSeriesDO::getSort));
    }

}
