package cn.kugua.module.english.controller.admin.examPart;

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

import cn.kugua.module.english.controller.admin.examPart.vo.*;
import cn.kugua.module.english.dal.dataobject.examPart.ExamPartDO;
import cn.kugua.module.english.service.examPart.ExamPartService;

@Tag(name = "管理后台 - 试卷 Part 多态头")
@RestController
@RequestMapping("/english/exam-part")
@Validated
public class ExamPartController {

    @Resource
    private ExamPartService examPartService;

    @PostMapping("/create")
    @Operation(summary = "创建试卷 Part 多态头")
    @PreAuthorize("@ss.hasPermission('english:exam-part:create')")
    public CommonResult<Long> createExamPart(@Valid @RequestBody ExamPartSaveReqVO createReqVO) {
        return success(examPartService.createExamPart(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新试卷 Part 多态头")
    @PreAuthorize("@ss.hasPermission('english:exam-part:update')")
    public CommonResult<Boolean> updateExamPart(@Valid @RequestBody ExamPartSaveReqVO updateReqVO) {
        examPartService.updateExamPart(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试卷 Part 多态头")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:exam-part:delete')")
    public CommonResult<Boolean> deleteExamPart(@RequestParam("id") Long id) {
        examPartService.deleteExamPart(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除试卷 Part 多态头")
                @PreAuthorize("@ss.hasPermission('english:exam-part:delete')")
    public CommonResult<Boolean> deleteExamPartList(@RequestParam("ids") List<Long> ids) {
        examPartService.deleteExamPartListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得试卷 Part 多态头")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:exam-part:query')")
    public CommonResult<ExamPartRespVO> getExamPart(@RequestParam("id") Long id) {
        ExamPartDO examPart = examPartService.getExamPart(id);
        return success(BeanUtils.toBean(examPart, ExamPartRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得试卷 Part 多态头分页")
    @PreAuthorize("@ss.hasPermission('english:exam-part:query')")
    public CommonResult<PageResult<ExamPartRespVO>> getExamPartPage(@Valid ExamPartPageReqVO pageReqVO) {
        PageResult<ExamPartDO> pageResult = examPartService.getExamPartPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ExamPartRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出试卷 Part 多态头 Excel")
    @PreAuthorize("@ss.hasPermission('english:exam-part:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExamPartExcel(@Valid ExamPartPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExamPartDO> list = examPartService.getExamPartPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "试卷 Part 多态头.xls", "数据", ExamPartRespVO.class,
                        BeanUtils.toBean(list, ExamPartRespVO.class));
    }

}