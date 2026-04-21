package cn.kugua.module.english.controller.app.examSeries;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.examSeries.vo.ExamSeriesRespVO;
import cn.kugua.module.english.dal.dataobject.examSeries.ExamSeriesDO;
import cn.kugua.module.english.service.examSeries.ExamSeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * H5 考试系列字典查询接口（仅返回启用项，按 level → sort 排序）
 */
@Tag(name = "H5 - 考试系列字典")
@RestController
@RequestMapping("/english/exam-series")
@Validated
public class AppExamSeriesController {

    @Resource
    private ExamSeriesService examSeriesService;

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得全部启用的考试系列")
    public CommonResult<List<ExamSeriesRespVO>> listAllSimple() {
        List<ExamSeriesDO> list = examSeriesService.listEnabled();
        return success(BeanUtils.toBean(list, ExamSeriesRespVO.class));
    }

}
