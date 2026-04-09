package cn.kugua.module.english.controller.admin.examLevel;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
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

import cn.kugua.module.english.controller.admin.examLevel.vo.*;
import cn.kugua.module.english.dal.dataobject.examLevel.ExamLevelDO;
import cn.kugua.module.english.service.examLevel.ExamLevelService;

@Tag(name = "管理后台 - 考试级别字典")
@RestController
@RequestMapping("/english/exam-level")
@Validated
public class ExamLevelController {

    @Resource
    private ExamLevelService examLevelService;

    @PostMapping("/create")
    @Operation(summary = "创建考试级别字典")
    @PreAuthorize("@ss.hasPermission('english:exam-level:create')")
    public CommonResult<Long> createExamLevel(@Valid @RequestBody ExamLevelSaveReqVO createReqVO) {
        return success(examLevelService.createExamLevel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新考试级别字典")
    @PreAuthorize("@ss.hasPermission('english:exam-level:update')")
    public CommonResult<Boolean> updateExamLevel(@Valid @RequestBody ExamLevelSaveReqVO updateReqVO) {
        examLevelService.updateExamLevel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除考试级别字典")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:exam-level:delete')")
    public CommonResult<Boolean> deleteExamLevel(@RequestParam("id") Long id) {
        examLevelService.deleteExamLevel(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除考试级别字典")
                @PreAuthorize("@ss.hasPermission('english:exam-level:delete')")
    public CommonResult<Boolean> deleteExamLevelList(@RequestParam("ids") List<Long> ids) {
        examLevelService.deleteExamLevelListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得考试级别字典")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:exam-level:query')")
    public CommonResult<ExamLevelRespVO> getExamLevel(@RequestParam("id") Long id) {
        ExamLevelDO examLevel = examLevelService.getExamLevel(id);
        return success(BeanUtils.toBean(examLevel, ExamLevelRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得考试级别字典分页")
    @PreAuthorize("@ss.hasPermission('english:exam-level:query')")
    public CommonResult<PageResult<ExamLevelRespVO>> getExamLevelPage(@Valid ExamLevelPageReqVO pageReqVO) {
        PageResult<ExamLevelDO> pageResult = examLevelService.getExamLevelPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ExamLevelRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出考试级别字典 Excel")
    @PreAuthorize("@ss.hasPermission('english:exam-level:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExamLevelExcel(@Valid ExamLevelPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExamLevelDO> list = examLevelService.getExamLevelPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "考试级别字典.xls", "数据", ExamLevelRespVO.class,
                        BeanUtils.toBean(list, ExamLevelRespVO.class));
    }

}