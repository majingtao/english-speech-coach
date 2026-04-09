package cn.kugua.module.english.controller.admin.partTellStoryFrame;

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

import cn.kugua.module.english.controller.admin.partTellStoryFrame.vo.*;
import cn.kugua.module.english.dal.dataobject.partTellStoryFrame.PartTellStoryFrameDO;
import cn.kugua.module.english.service.partTellStoryFrame.PartTellStoryFrameService;

@Tag(name = "管理后台 - 讲故事 - 单帧")
@RestController
@RequestMapping("/english/part-tell-story-frame")
@Validated
public class PartTellStoryFrameController {

    @Resource
    private PartTellStoryFrameService partTellStoryFrameService;

    @PostMapping("/create")
    @Operation(summary = "创建讲故事 - 单帧")
    @PreAuthorize("@ss.hasPermission('english:part-tell-story-frame:create')")
    public CommonResult<Long> createPartTellStoryFrame(@Valid @RequestBody PartTellStoryFrameSaveReqVO createReqVO) {
        return success(partTellStoryFrameService.createPartTellStoryFrame(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新讲故事 - 单帧")
    @PreAuthorize("@ss.hasPermission('english:part-tell-story-frame:update')")
    public CommonResult<Boolean> updatePartTellStoryFrame(@Valid @RequestBody PartTellStoryFrameSaveReqVO updateReqVO) {
        partTellStoryFrameService.updatePartTellStoryFrame(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除讲故事 - 单帧")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-tell-story-frame:delete')")
    public CommonResult<Boolean> deletePartTellStoryFrame(@RequestParam("id") Long id) {
        partTellStoryFrameService.deletePartTellStoryFrame(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除讲故事 - 单帧")
                @PreAuthorize("@ss.hasPermission('english:part-tell-story-frame:delete')")
    public CommonResult<Boolean> deletePartTellStoryFrameList(@RequestParam("ids") List<Long> ids) {
        partTellStoryFrameService.deletePartTellStoryFrameListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得讲故事 - 单帧")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-tell-story-frame:query')")
    public CommonResult<PartTellStoryFrameRespVO> getPartTellStoryFrame(@RequestParam("id") Long id) {
        PartTellStoryFrameDO partTellStoryFrame = partTellStoryFrameService.getPartTellStoryFrame(id);
        return success(BeanUtils.toBean(partTellStoryFrame, PartTellStoryFrameRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得讲故事 - 单帧分页")
    @PreAuthorize("@ss.hasPermission('english:part-tell-story-frame:query')")
    public CommonResult<PageResult<PartTellStoryFrameRespVO>> getPartTellStoryFramePage(@Valid PartTellStoryFramePageReqVO pageReqVO) {
        PageResult<PartTellStoryFrameDO> pageResult = partTellStoryFrameService.getPartTellStoryFramePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartTellStoryFrameRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出讲故事 - 单帧 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-tell-story-frame:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartTellStoryFrameExcel(@Valid PartTellStoryFramePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartTellStoryFrameDO> list = partTellStoryFrameService.getPartTellStoryFramePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "讲故事 - 单帧.xls", "数据", PartTellStoryFrameRespVO.class,
                        BeanUtils.toBean(list, PartTellStoryFrameRespVO.class));
    }

}