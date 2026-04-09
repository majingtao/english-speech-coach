package cn.kugua.module.english.controller.admin.aiCallLog;

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

import cn.kugua.module.english.controller.admin.aiCallLog.vo.*;
import cn.kugua.module.english.dal.dataobject.aiCallLog.AiCallLogDO;
import cn.kugua.module.english.service.aiCallLog.AiCallLogService;

@Tag(name = "管理后台 - AI 调用日志")
@RestController
@RequestMapping("/english/ai-call-log")
@Validated
public class AiCallLogController {

    @Resource
    private AiCallLogService aiCallLogService;

    @PostMapping("/create")
    @Operation(summary = "创建AI 调用日志")
    @PreAuthorize("@ss.hasPermission('english:ai-call-log:create')")
    public CommonResult<Long> createAiCallLog(@Valid @RequestBody AiCallLogSaveReqVO createReqVO) {
        return success(aiCallLogService.createAiCallLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新AI 调用日志")
    @PreAuthorize("@ss.hasPermission('english:ai-call-log:update')")
    public CommonResult<Boolean> updateAiCallLog(@Valid @RequestBody AiCallLogSaveReqVO updateReqVO) {
        aiCallLogService.updateAiCallLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除AI 调用日志")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:ai-call-log:delete')")
    public CommonResult<Boolean> deleteAiCallLog(@RequestParam("id") Long id) {
        aiCallLogService.deleteAiCallLog(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除AI 调用日志")
                @PreAuthorize("@ss.hasPermission('english:ai-call-log:delete')")
    public CommonResult<Boolean> deleteAiCallLogList(@RequestParam("ids") List<Long> ids) {
        aiCallLogService.deleteAiCallLogListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得AI 调用日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:ai-call-log:query')")
    public CommonResult<AiCallLogRespVO> getAiCallLog(@RequestParam("id") Long id) {
        AiCallLogDO aiCallLog = aiCallLogService.getAiCallLog(id);
        return success(BeanUtils.toBean(aiCallLog, AiCallLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得AI 调用日志分页")
    @PreAuthorize("@ss.hasPermission('english:ai-call-log:query')")
    public CommonResult<PageResult<AiCallLogRespVO>> getAiCallLogPage(@Valid AiCallLogPageReqVO pageReqVO) {
        PageResult<AiCallLogDO> pageResult = aiCallLogService.getAiCallLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiCallLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出AI 调用日志 Excel")
    @PreAuthorize("@ss.hasPermission('english:ai-call-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAiCallLogExcel(@Valid AiCallLogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AiCallLogDO> list = aiCallLogService.getAiCallLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "AI 调用日志.xls", "数据", AiCallLogRespVO.class,
                        BeanUtils.toBean(list, AiCallLogRespVO.class));
    }

}