package cn.kugua.module.english.controller.admin.practiceAnswer;

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

import cn.kugua.module.english.controller.admin.practiceAnswer.vo.*;
import cn.kugua.module.english.dal.dataobject.practiceAnswer.PracticeAnswerDO;
import cn.kugua.module.english.service.practiceAnswer.PracticeAnswerService;

@Tag(name = "管理后台 - 练习单题作答")
@RestController
@RequestMapping("/english/practice-answer")
@Validated
public class PracticeAnswerController {

    @Resource
    private PracticeAnswerService practiceAnswerService;

    @PostMapping("/create")
    @Operation(summary = "创建练习单题作答")
    @PreAuthorize("@ss.hasPermission('english:practice-answer:create')")
    public CommonResult<Long> createPracticeAnswer(@Valid @RequestBody PracticeAnswerSaveReqVO createReqVO) {
        return success(practiceAnswerService.createPracticeAnswer(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新练习单题作答")
    @PreAuthorize("@ss.hasPermission('english:practice-answer:update')")
    public CommonResult<Boolean> updatePracticeAnswer(@Valid @RequestBody PracticeAnswerSaveReqVO updateReqVO) {
        practiceAnswerService.updatePracticeAnswer(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除练习单题作答")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:practice-answer:delete')")
    public CommonResult<Boolean> deletePracticeAnswer(@RequestParam("id") Long id) {
        practiceAnswerService.deletePracticeAnswer(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除练习单题作答")
                @PreAuthorize("@ss.hasPermission('english:practice-answer:delete')")
    public CommonResult<Boolean> deletePracticeAnswerList(@RequestParam("ids") List<Long> ids) {
        practiceAnswerService.deletePracticeAnswerListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得练习单题作答")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:practice-answer:query')")
    public CommonResult<PracticeAnswerRespVO> getPracticeAnswer(@RequestParam("id") Long id) {
        PracticeAnswerDO practiceAnswer = practiceAnswerService.getPracticeAnswer(id);
        return success(BeanUtils.toBean(practiceAnswer, PracticeAnswerRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得练习单题作答分页")
    @PreAuthorize("@ss.hasPermission('english:practice-answer:query')")
    public CommonResult<PageResult<PracticeAnswerRespVO>> getPracticeAnswerPage(@Valid PracticeAnswerPageReqVO pageReqVO) {
        PageResult<PracticeAnswerDO> pageResult = practiceAnswerService.getPracticeAnswerPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PracticeAnswerRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出练习单题作答 Excel")
    @PreAuthorize("@ss.hasPermission('english:practice-answer:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPracticeAnswerExcel(@Valid PracticeAnswerPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PracticeAnswerDO> list = practiceAnswerService.getPracticeAnswerPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "练习单题作答.xls", "数据", PracticeAnswerRespVO.class,
                        BeanUtils.toBean(list, PracticeAnswerRespVO.class));
    }

}