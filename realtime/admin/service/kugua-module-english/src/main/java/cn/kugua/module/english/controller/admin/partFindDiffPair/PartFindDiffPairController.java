package cn.kugua.module.english.controller.admin.partFindDiffPair;

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

import cn.kugua.module.english.controller.admin.partFindDiffPair.vo.*;
import cn.kugua.module.english.dal.dataobject.partFindDiffPair.PartFindDiffPairDO;
import cn.kugua.module.english.service.partFindDiffPair.PartFindDiffPairService;

@Tag(name = "管理后台 - 找不同 - 图对")
@RestController
@RequestMapping("/english/part-find-diff-pair")
@Validated
public class PartFindDiffPairController {

    @Resource
    private PartFindDiffPairService partFindDiffPairService;

    @PostMapping("/create")
    @Operation(summary = "创建找不同 - 图对")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-pair:create')")
    public CommonResult<Long> createPartFindDiffPair(@Valid @RequestBody PartFindDiffPairSaveReqVO createReqVO) {
        return success(partFindDiffPairService.createPartFindDiffPair(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新找不同 - 图对")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-pair:update')")
    public CommonResult<Boolean> updatePartFindDiffPair(@Valid @RequestBody PartFindDiffPairSaveReqVO updateReqVO) {
        partFindDiffPairService.updatePartFindDiffPair(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除找不同 - 图对")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-pair:delete')")
    public CommonResult<Boolean> deletePartFindDiffPair(@RequestParam("id") Long id) {
        partFindDiffPairService.deletePartFindDiffPair(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除找不同 - 图对")
                @PreAuthorize("@ss.hasPermission('english:part-find-diff-pair:delete')")
    public CommonResult<Boolean> deletePartFindDiffPairList(@RequestParam("ids") List<Long> ids) {
        partFindDiffPairService.deletePartFindDiffPairListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得找不同 - 图对")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-pair:query')")
    public CommonResult<PartFindDiffPairRespVO> getPartFindDiffPair(@RequestParam("id") Long id) {
        PartFindDiffPairDO partFindDiffPair = partFindDiffPairService.getPartFindDiffPair(id);
        return success(BeanUtils.toBean(partFindDiffPair, PartFindDiffPairRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得找不同 - 图对分页")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-pair:query')")
    public CommonResult<PageResult<PartFindDiffPairRespVO>> getPartFindDiffPairPage(@Valid PartFindDiffPairPageReqVO pageReqVO) {
        PageResult<PartFindDiffPairDO> pageResult = partFindDiffPairService.getPartFindDiffPairPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartFindDiffPairRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出找不同 - 图对 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-find-diff-pair:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartFindDiffPairExcel(@Valid PartFindDiffPairPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartFindDiffPairDO> list = partFindDiffPairService.getPartFindDiffPairPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "找不同 - 图对.xls", "数据", PartFindDiffPairRespVO.class,
                        BeanUtils.toBean(list, PartFindDiffPairRespVO.class));
    }

}