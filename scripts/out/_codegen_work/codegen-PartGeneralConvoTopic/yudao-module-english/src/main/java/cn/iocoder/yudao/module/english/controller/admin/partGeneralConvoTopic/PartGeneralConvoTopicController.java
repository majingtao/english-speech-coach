package cn.iocoder.yudao.module.english.controller.admin.partGeneralConvoTopic;

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

import cn.iocoder.yudao.module.english.controller.admin.partGeneralConvoTopic.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partGeneralConvoTopic.PartGeneralConvoTopicDO;
import cn.iocoder.yudao.module.english.service.partGeneralConvoTopic.PartGeneralConvoTopicService;

@Tag(name = "管理后台 - 一般对话 - 主题")
@RestController
@RequestMapping("/english/part-general-convo-topic")
@Validated
public class PartGeneralConvoTopicController {

    @Resource
    private PartGeneralConvoTopicService partGeneralConvoTopicService;

    @PostMapping("/create")
    @Operation(summary = "创建一般对话 - 主题")
    @PreAuthorize("@ss.hasPermission('english:part-general-convo-topic:create')")
    public CommonResult<Long> createPartGeneralConvoTopic(@Valid @RequestBody PartGeneralConvoTopicSaveReqVO createReqVO) {
        return success(partGeneralConvoTopicService.createPartGeneralConvoTopic(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新一般对话 - 主题")
    @PreAuthorize("@ss.hasPermission('english:part-general-convo-topic:update')")
    public CommonResult<Boolean> updatePartGeneralConvoTopic(@Valid @RequestBody PartGeneralConvoTopicSaveReqVO updateReqVO) {
        partGeneralConvoTopicService.updatePartGeneralConvoTopic(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除一般对话 - 主题")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-general-convo-topic:delete')")
    public CommonResult<Boolean> deletePartGeneralConvoTopic(@RequestParam("id") Long id) {
        partGeneralConvoTopicService.deletePartGeneralConvoTopic(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除一般对话 - 主题")
                @PreAuthorize("@ss.hasPermission('english:part-general-convo-topic:delete')")
    public CommonResult<Boolean> deletePartGeneralConvoTopicList(@RequestParam("ids") List<Long> ids) {
        partGeneralConvoTopicService.deletePartGeneralConvoTopicListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得一般对话 - 主题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-general-convo-topic:query')")
    public CommonResult<PartGeneralConvoTopicRespVO> getPartGeneralConvoTopic(@RequestParam("id") Long id) {
        PartGeneralConvoTopicDO partGeneralConvoTopic = partGeneralConvoTopicService.getPartGeneralConvoTopic(id);
        return success(BeanUtils.toBean(partGeneralConvoTopic, PartGeneralConvoTopicRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得一般对话 - 主题分页")
    @PreAuthorize("@ss.hasPermission('english:part-general-convo-topic:query')")
    public CommonResult<PageResult<PartGeneralConvoTopicRespVO>> getPartGeneralConvoTopicPage(@Valid PartGeneralConvoTopicPageReqVO pageReqVO) {
        PageResult<PartGeneralConvoTopicDO> pageResult = partGeneralConvoTopicService.getPartGeneralConvoTopicPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartGeneralConvoTopicRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出一般对话 - 主题 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-general-convo-topic:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartGeneralConvoTopicExcel(@Valid PartGeneralConvoTopicPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartGeneralConvoTopicDO> list = partGeneralConvoTopicService.getPartGeneralConvoTopicPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "一般对话 - 主题.xls", "数据", PartGeneralConvoTopicRespVO.class,
                        BeanUtils.toBean(list, PartGeneralConvoTopicRespVO.class));
    }

}