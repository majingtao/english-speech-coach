package cn.kugua.module.english.controller.admin.partPersonalQaSample;

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

import cn.kugua.module.english.controller.admin.partPersonalQaSample.vo.*;
import cn.kugua.module.english.dal.dataobject.partPersonalQaSample.PartPersonalQaSampleDO;
import cn.kugua.module.english.service.partPersonalQaSample.PartPersonalQaSampleService;

@Tag(name = "管理后台 - 个人问答 - 示例答案")
@RestController
@RequestMapping("/english/part-personal-qa-sample")
@Validated
public class PartPersonalQaSampleController {

    @Resource
    private PartPersonalQaSampleService partPersonalQaSampleService;

    @PostMapping("/create")
    @Operation(summary = "创建个人问答 - 示例答案")
    @PreAuthorize("@ss.hasPermission('english:part-personal-qa-sample:create')")
    public CommonResult<Long> createPartPersonalQaSample(@Valid @RequestBody PartPersonalQaSampleSaveReqVO createReqVO) {
        return success(partPersonalQaSampleService.createPartPersonalQaSample(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新个人问答 - 示例答案")
    @PreAuthorize("@ss.hasPermission('english:part-personal-qa-sample:update')")
    public CommonResult<Boolean> updatePartPersonalQaSample(@Valid @RequestBody PartPersonalQaSampleSaveReqVO updateReqVO) {
        partPersonalQaSampleService.updatePartPersonalQaSample(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除个人问答 - 示例答案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-personal-qa-sample:delete')")
    public CommonResult<Boolean> deletePartPersonalQaSample(@RequestParam("id") Long id) {
        partPersonalQaSampleService.deletePartPersonalQaSample(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除个人问答 - 示例答案")
    @PreAuthorize("@ss.hasPermission('english:part-personal-qa-sample:delete')")
    public CommonResult<Boolean> deletePartPersonalQaSampleList(@RequestParam("ids") List<Long> ids) {
        partPersonalQaSampleService.deletePartPersonalQaSampleListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得个人问答 - 示例答案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-personal-qa-sample:query')")
    public CommonResult<PartPersonalQaSampleRespVO> getPartPersonalQaSample(@RequestParam("id") Long id) {
        PartPersonalQaSampleDO obj = partPersonalQaSampleService.getPartPersonalQaSample(id);
        return success(BeanUtils.toBean(obj, PartPersonalQaSampleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得个人问答 - 示例答案分页")
    @PreAuthorize("@ss.hasPermission('english:part-personal-qa-sample:query')")
    public CommonResult<PageResult<PartPersonalQaSampleRespVO>> getPartPersonalQaSamplePage(@Valid PartPersonalQaSamplePageReqVO pageReqVO) {
        PageResult<PartPersonalQaSampleDO> pageResult = partPersonalQaSampleService.getPartPersonalQaSamplePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartPersonalQaSampleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出个人问答 - 示例答案 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-personal-qa-sample:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartPersonalQaSampleExcel(@Valid PartPersonalQaSamplePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartPersonalQaSampleDO> list = partPersonalQaSampleService.getPartPersonalQaSamplePage(pageReqVO).getList();
        ExcelUtils.write(response, "个人问答 - 示例答案.xls", "数据", PartPersonalQaSampleRespVO.class,
                        BeanUtils.toBean(list, PartPersonalQaSampleRespVO.class));
    }

}
