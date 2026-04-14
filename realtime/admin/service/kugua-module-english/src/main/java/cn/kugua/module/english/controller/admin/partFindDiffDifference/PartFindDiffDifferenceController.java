package cn.kugua.module.english.controller.admin.partFindDiffDifference;

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

import cn.kugua.module.english.controller.admin.partFindDiffDifference.vo.*;
import cn.kugua.module.english.dal.dataobject.partFindDiffDifference.PartFindDiffDifferenceDO;
import cn.kugua.module.english.service.partFindDiffDifference.PartFindDiffDifferenceService;

@Tag(name = "管理后台 - 找不同 - 差异点")
@RestController
@RequestMapping("/english/part-find-diff-difference")
@Validated
public class PartFindDiffDifferenceController {

    @Resource
    private PartFindDiffDifferenceService partFindDiffDifferenceService;

    @PostMapping("/create")
    @Operation(summary = "创建找不同 - 差异点")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-difference:create')")
    public CommonResult<Long> createPartFindDiffDifference(@Valid @RequestBody PartFindDiffDifferenceSaveReqVO createReqVO) {
        return success(partFindDiffDifferenceService.createPartFindDiffDifference(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新找不同 - 差异点")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-difference:update')")
    public CommonResult<Boolean> updatePartFindDiffDifference(@Valid @RequestBody PartFindDiffDifferenceSaveReqVO updateReqVO) {
        partFindDiffDifferenceService.updatePartFindDiffDifference(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除找不同 - 差异点")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-difference:delete')")
    public CommonResult<Boolean> deletePartFindDiffDifference(@RequestParam("id") Long id) {
        partFindDiffDifferenceService.deletePartFindDiffDifference(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除找不同 - 差异点")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-difference:delete')")
    public CommonResult<Boolean> deletePartFindDiffDifferenceList(@RequestParam("ids") List<Long> ids) {
        partFindDiffDifferenceService.deletePartFindDiffDifferenceListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得找不同 - 差异点")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-difference:query')")
    public CommonResult<PartFindDiffDifferenceRespVO> getPartFindDiffDifference(@RequestParam("id") Long id) {
        PartFindDiffDifferenceDO obj = partFindDiffDifferenceService.getPartFindDiffDifference(id);
        return success(BeanUtils.toBean(obj, PartFindDiffDifferenceRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得找不同 - 差异点分页")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-difference:query')")
    public CommonResult<PageResult<PartFindDiffDifferenceRespVO>> getPartFindDiffDifferencePage(@Valid PartFindDiffDifferencePageReqVO pageReqVO) {
        PageResult<PartFindDiffDifferenceDO> pageResult = partFindDiffDifferenceService.getPartFindDiffDifferencePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartFindDiffDifferenceRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出找不同 - 差异点 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-difference:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartFindDiffDifferenceExcel(@Valid PartFindDiffDifferencePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartFindDiffDifferenceDO> list = partFindDiffDifferenceService.getPartFindDiffDifferencePage(pageReqVO).getList();
        ExcelUtils.write(response, "找不同 - 差异点.xls", "数据", PartFindDiffDifferenceRespVO.class,
                        BeanUtils.toBean(list, PartFindDiffDifferenceRespVO.class));
    }

}
