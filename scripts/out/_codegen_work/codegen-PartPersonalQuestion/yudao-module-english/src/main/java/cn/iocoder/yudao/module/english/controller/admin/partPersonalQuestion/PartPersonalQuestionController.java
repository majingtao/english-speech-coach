package cn.iocoder.yudao.module.english.controller.admin.partPersonalQuestion;

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

import cn.iocoder.yudao.module.english.controller.admin.partPersonalQuestion.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partPersonalQuestion.PartPersonalQuestionDO;
import cn.iocoder.yudao.module.english.service.partPersonalQuestion.PartPersonalQuestionService;

@Tag(name = "管理后台 - 个人问答 - 问题")
@RestController
@RequestMapping("/english/part-personal-question")
@Validated
public class PartPersonalQuestionController {

    @Resource
    private PartPersonalQuestionService partPersonalQuestionService;

    @PostMapping("/create")
    @Operation(summary = "创建个人问答 - 问题")
    @PreAuthorize("@ss.hasPermission('english:part-personal-question:create')")
    public CommonResult<Long> createPartPersonalQuestion(@Valid @RequestBody PartPersonalQuestionSaveReqVO createReqVO) {
        return success(partPersonalQuestionService.createPartPersonalQuestion(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新个人问答 - 问题")
    @PreAuthorize("@ss.hasPermission('english:part-personal-question:update')")
    public CommonResult<Boolean> updatePartPersonalQuestion(@Valid @RequestBody PartPersonalQuestionSaveReqVO updateReqVO) {
        partPersonalQuestionService.updatePartPersonalQuestion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除个人问答 - 问题")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-personal-question:delete')")
    public CommonResult<Boolean> deletePartPersonalQuestion(@RequestParam("id") Long id) {
        partPersonalQuestionService.deletePartPersonalQuestion(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除个人问答 - 问题")
                @PreAuthorize("@ss.hasPermission('english:part-personal-question:delete')")
    public CommonResult<Boolean> deletePartPersonalQuestionList(@RequestParam("ids") List<Long> ids) {
        partPersonalQuestionService.deletePartPersonalQuestionListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得个人问答 - 问题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-personal-question:query')")
    public CommonResult<PartPersonalQuestionRespVO> getPartPersonalQuestion(@RequestParam("id") Long id) {
        PartPersonalQuestionDO partPersonalQuestion = partPersonalQuestionService.getPartPersonalQuestion(id);
        return success(BeanUtils.toBean(partPersonalQuestion, PartPersonalQuestionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得个人问答 - 问题分页")
    @PreAuthorize("@ss.hasPermission('english:part-personal-question:query')")
    public CommonResult<PageResult<PartPersonalQuestionRespVO>> getPartPersonalQuestionPage(@Valid PartPersonalQuestionPageReqVO pageReqVO) {
        PageResult<PartPersonalQuestionDO> pageResult = partPersonalQuestionService.getPartPersonalQuestionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartPersonalQuestionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出个人问答 - 问题 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-personal-question:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartPersonalQuestionExcel(@Valid PartPersonalQuestionPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartPersonalQuestionDO> list = partPersonalQuestionService.getPartPersonalQuestionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "个人问答 - 问题.xls", "数据", PartPersonalQuestionRespVO.class,
                        BeanUtils.toBean(list, PartPersonalQuestionRespVO.class));
    }

}