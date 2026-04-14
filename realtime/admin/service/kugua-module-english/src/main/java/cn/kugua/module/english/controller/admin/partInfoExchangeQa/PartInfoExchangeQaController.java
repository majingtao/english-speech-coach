package cn.kugua.module.english.controller.admin.partInfoExchangeQa;

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

import cn.kugua.module.english.controller.admin.partInfoExchangeQa.vo.*;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeQa.PartInfoExchangeQaDO;
import cn.kugua.module.english.service.partInfoExchangeQa.PartInfoExchangeQaService;

@Tag(name = "管理后台 - 信息互换 - 问答条目")
@RestController
@RequestMapping("/english/part-info-exchange-qa")
@Validated
public class PartInfoExchangeQaController {

    @Resource
    private PartInfoExchangeQaService partInfoExchangeQaService;

    @PostMapping("/create")
    @Operation(summary = "创建信息互换 - 问答条目")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-qa:create')")
    public CommonResult<Long> createPartInfoExchangeQa(@Valid @RequestBody PartInfoExchangeQaSaveReqVO createReqVO) {
        return success(partInfoExchangeQaService.createPartInfoExchangeQa(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新信息互换 - 问答条目")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-qa:update')")
    public CommonResult<Boolean> updatePartInfoExchangeQa(@Valid @RequestBody PartInfoExchangeQaSaveReqVO updateReqVO) {
        partInfoExchangeQaService.updatePartInfoExchangeQa(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除信息互换 - 问答条目")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-qa:delete')")
    public CommonResult<Boolean> deletePartInfoExchangeQa(@RequestParam("id") Long id) {
        partInfoExchangeQaService.deletePartInfoExchangeQa(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除信息互换 - 问答条目")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-qa:delete')")
    public CommonResult<Boolean> deletePartInfoExchangeQaList(@RequestParam("ids") List<Long> ids) {
        partInfoExchangeQaService.deletePartInfoExchangeQaListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得信息互换 - 问答条目")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-qa:query')")
    public CommonResult<PartInfoExchangeQaRespVO> getPartInfoExchangeQa(@RequestParam("id") Long id) {
        PartInfoExchangeQaDO obj = partInfoExchangeQaService.getPartInfoExchangeQa(id);
        return success(BeanUtils.toBean(obj, PartInfoExchangeQaRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得信息互换 - 问答条目分页")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-qa:query')")
    public CommonResult<PageResult<PartInfoExchangeQaRespVO>> getPartInfoExchangeQaPage(@Valid PartInfoExchangeQaPageReqVO pageReqVO) {
        PageResult<PartInfoExchangeQaDO> pageResult = partInfoExchangeQaService.getPartInfoExchangeQaPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartInfoExchangeQaRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出信息互换 - 问答条目 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-qa:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartInfoExchangeQaExcel(@Valid PartInfoExchangeQaPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartInfoExchangeQaDO> list = partInfoExchangeQaService.getPartInfoExchangeQaPage(pageReqVO).getList();
        ExcelUtils.write(response, "信息互换 - 问答条目.xls", "数据", PartInfoExchangeQaRespVO.class,
                        BeanUtils.toBean(list, PartInfoExchangeQaRespVO.class));
    }

}
