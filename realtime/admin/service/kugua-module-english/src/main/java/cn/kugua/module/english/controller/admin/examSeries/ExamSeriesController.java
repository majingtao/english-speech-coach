package cn.kugua.module.english.controller.admin.examSeries;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.kugua.module.english.controller.admin.examSeries.vo.*;
import cn.kugua.module.english.dal.dataobject.examSeries.ExamSeriesDO;
import cn.kugua.module.english.service.examSeries.ExamSeriesService;

@Tag(name = "管理后台 - 考试系列字典")
@RestController
@RequestMapping("/english/exam-series")
@Validated
public class ExamSeriesController {

    @Resource
    private ExamSeriesService examSeriesService;

    @PostMapping("/create")
    @Operation(summary = "创建考试系列字典")
    @PreAuthorize("@ss.hasPermission('english:exam-series:create')")
    public CommonResult<Long> createExamSeries(@Valid @RequestBody ExamSeriesSaveReqVO createReqVO) {
        return success(examSeriesService.createExamSeries(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新考试系列字典")
    @PreAuthorize("@ss.hasPermission('english:exam-series:update')")
    public CommonResult<Boolean> updateExamSeries(@Valid @RequestBody ExamSeriesSaveReqVO updateReqVO) {
        examSeriesService.updateExamSeries(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除考试系列字典")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:exam-series:delete')")
    public CommonResult<Boolean> deleteExamSeries(@RequestParam("id") Long id) {
        examSeriesService.deleteExamSeries(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除考试系列字典")
    @PreAuthorize("@ss.hasPermission('english:exam-series:delete')")
    public CommonResult<Boolean> deleteExamSeriesList(@RequestParam("ids") List<Long> ids) {
        examSeriesService.deleteExamSeriesListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得考试系列字典")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:exam-series:query')")
    public CommonResult<ExamSeriesRespVO> getExamSeries(@RequestParam("id") Long id) {
        ExamSeriesDO examSeries = examSeriesService.getExamSeries(id);
        return success(BeanUtils.toBean(examSeries, ExamSeriesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得考试系列字典分页")
    @PreAuthorize("@ss.hasPermission('english:exam-series:query')")
    public CommonResult<PageResult<ExamSeriesRespVO>> getExamSeriesPage(@Valid ExamSeriesPageReqVO pageReqVO) {
        PageResult<ExamSeriesDO> pageResult = examSeriesService.getExamSeriesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ExamSeriesRespVO.class));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得全部启用的考试系列（不分页，供下拉/级联使用）")
    public CommonResult<List<ExamSeriesRespVO>> listAllSimple() {
        List<ExamSeriesDO> list = examSeriesService.listEnabled();
        return success(BeanUtils.toBean(list, ExamSeriesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出考试系列字典 Excel")
    @PreAuthorize("@ss.hasPermission('english:exam-series:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExamSeriesExcel(@Valid ExamSeriesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExamSeriesDO> list = examSeriesService.getExamSeriesPage(pageReqVO).getList();
        ExcelUtils.write(response, "考试系列字典.xls", "数据", ExamSeriesRespVO.class,
                BeanUtils.toBean(list, ExamSeriesRespVO.class));
    }

}
