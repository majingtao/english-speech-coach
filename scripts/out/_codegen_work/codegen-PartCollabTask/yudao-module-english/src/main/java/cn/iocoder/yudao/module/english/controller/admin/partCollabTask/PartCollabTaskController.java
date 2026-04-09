package cn.iocoder.yudao.module.english.controller.admin.partCollabTask;

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

import cn.iocoder.yudao.module.english.controller.admin.partCollabTask.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partCollabTask.PartCollabTaskDO;
import cn.iocoder.yudao.module.english.service.partCollabTask.PartCollabTaskService;

@Tag(name = "管理后台 - 协作任务 - 主体")
@RestController
@RequestMapping("/english/part-collab-task")
@Validated
public class PartCollabTaskController {

    @Resource
    private PartCollabTaskService partCollabTaskService;

    @PostMapping("/create")
    @Operation(summary = "创建协作任务 - 主体")
    @PreAuthorize("@ss.hasPermission('english:part-collab-task:create')")
    public CommonResult<Long> createPartCollabTask(@Valid @RequestBody PartCollabTaskSaveReqVO createReqVO) {
        return success(partCollabTaskService.createPartCollabTask(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新协作任务 - 主体")
    @PreAuthorize("@ss.hasPermission('english:part-collab-task:update')")
    public CommonResult<Boolean> updatePartCollabTask(@Valid @RequestBody PartCollabTaskSaveReqVO updateReqVO) {
        partCollabTaskService.updatePartCollabTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除协作任务 - 主体")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-collab-task:delete')")
    public CommonResult<Boolean> deletePartCollabTask(@RequestParam("id") Long id) {
        partCollabTaskService.deletePartCollabTask(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除协作任务 - 主体")
                @PreAuthorize("@ss.hasPermission('english:part-collab-task:delete')")
    public CommonResult<Boolean> deletePartCollabTaskList(@RequestParam("ids") List<Long> ids) {
        partCollabTaskService.deletePartCollabTaskListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得协作任务 - 主体")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-collab-task:query')")
    public CommonResult<PartCollabTaskRespVO> getPartCollabTask(@RequestParam("id") Long id) {
        PartCollabTaskDO partCollabTask = partCollabTaskService.getPartCollabTask(id);
        return success(BeanUtils.toBean(partCollabTask, PartCollabTaskRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得协作任务 - 主体分页")
    @PreAuthorize("@ss.hasPermission('english:part-collab-task:query')")
    public CommonResult<PageResult<PartCollabTaskRespVO>> getPartCollabTaskPage(@Valid PartCollabTaskPageReqVO pageReqVO) {
        PageResult<PartCollabTaskDO> pageResult = partCollabTaskService.getPartCollabTaskPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartCollabTaskRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出协作任务 - 主体 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-collab-task:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartCollabTaskExcel(@Valid PartCollabTaskPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartCollabTaskDO> list = partCollabTaskService.getPartCollabTaskPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "协作任务 - 主体.xls", "数据", PartCollabTaskRespVO.class,
                        BeanUtils.toBean(list, PartCollabTaskRespVO.class));
    }

}