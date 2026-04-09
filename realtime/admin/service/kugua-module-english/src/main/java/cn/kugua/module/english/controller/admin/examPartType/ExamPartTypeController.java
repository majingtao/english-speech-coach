package cn.kugua.module.english.controller.admin.examPartType;

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

import cn.kugua.module.english.controller.admin.examPartType.vo.*;
import cn.kugua.module.english.dal.dataobject.examPartType.ExamPartTypeDO;
import cn.kugua.module.english.service.examPartType.ExamPartTypeService;

@Tag(name = "管理后台 - 题型字典")
@RestController
@RequestMapping("/english/exam-part-type")
@Validated
public class ExamPartTypeController {

    @Resource
    private ExamPartTypeService examPartTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建题型字典")
    @PreAuthorize("@ss.hasPermission('english:exam-part-type:create')")
    public CommonResult<Long> createExamPartType(@Valid @RequestBody ExamPartTypeSaveReqVO createReqVO) {
        return success(examPartTypeService.createExamPartType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新题型字典")
    @PreAuthorize("@ss.hasPermission('english:exam-part-type:update')")
    public CommonResult<Boolean> updateExamPartType(@Valid @RequestBody ExamPartTypeSaveReqVO updateReqVO) {
        examPartTypeService.updateExamPartType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除题型字典")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:exam-part-type:delete')")
    public CommonResult<Boolean> deleteExamPartType(@RequestParam("id") Long id) {
        examPartTypeService.deleteExamPartType(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除题型字典")
                @PreAuthorize("@ss.hasPermission('english:exam-part-type:delete')")
    public CommonResult<Boolean> deleteExamPartTypeList(@RequestParam("ids") List<Long> ids) {
        examPartTypeService.deleteExamPartTypeListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得题型字典")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:exam-part-type:query')")
    public CommonResult<ExamPartTypeRespVO> getExamPartType(@RequestParam("id") Long id) {
        ExamPartTypeDO examPartType = examPartTypeService.getExamPartType(id);
        return success(BeanUtils.toBean(examPartType, ExamPartTypeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得题型字典分页")
    @PreAuthorize("@ss.hasPermission('english:exam-part-type:query')")
    public CommonResult<PageResult<ExamPartTypeRespVO>> getExamPartTypePage(@Valid ExamPartTypePageReqVO pageReqVO) {
        PageResult<ExamPartTypeDO> pageResult = examPartTypeService.getExamPartTypePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ExamPartTypeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出题型字典 Excel")
    @PreAuthorize("@ss.hasPermission('english:exam-part-type:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExamPartTypeExcel(@Valid ExamPartTypePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExamPartTypeDO> list = examPartTypeService.getExamPartTypePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "题型字典.xls", "数据", ExamPartTypeRespVO.class,
                        BeanUtils.toBean(list, ExamPartTypeRespVO.class));
    }

}