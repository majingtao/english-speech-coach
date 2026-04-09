package cn.kugua.module.english.controller.admin.practiceSession;

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

import cn.kugua.module.english.controller.admin.practiceSession.vo.*;
import cn.kugua.module.english.dal.dataobject.practiceSession.PracticeSessionDO;
import cn.kugua.module.english.service.practiceSession.PracticeSessionService;

@Tag(name = "管理后台 - 练习会话")
@RestController
@RequestMapping("/english/practice-session")
@Validated
public class PracticeSessionController {

    @Resource
    private PracticeSessionService practiceSessionService;

    @PostMapping("/create")
    @Operation(summary = "创建练习会话")
    @PreAuthorize("@ss.hasPermission('english:practice-session:create')")
    public CommonResult<Long> createPracticeSession(@Valid @RequestBody PracticeSessionSaveReqVO createReqVO) {
        return success(practiceSessionService.createPracticeSession(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新练习会话")
    @PreAuthorize("@ss.hasPermission('english:practice-session:update')")
    public CommonResult<Boolean> updatePracticeSession(@Valid @RequestBody PracticeSessionSaveReqVO updateReqVO) {
        practiceSessionService.updatePracticeSession(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除练习会话")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:practice-session:delete')")
    public CommonResult<Boolean> deletePracticeSession(@RequestParam("id") Long id) {
        practiceSessionService.deletePracticeSession(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除练习会话")
                @PreAuthorize("@ss.hasPermission('english:practice-session:delete')")
    public CommonResult<Boolean> deletePracticeSessionList(@RequestParam("ids") List<Long> ids) {
        practiceSessionService.deletePracticeSessionListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得练习会话")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:practice-session:query')")
    public CommonResult<PracticeSessionRespVO> getPracticeSession(@RequestParam("id") Long id) {
        PracticeSessionDO practiceSession = practiceSessionService.getPracticeSession(id);
        return success(BeanUtils.toBean(practiceSession, PracticeSessionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得练习会话分页")
    @PreAuthorize("@ss.hasPermission('english:practice-session:query')")
    public CommonResult<PageResult<PracticeSessionRespVO>> getPracticeSessionPage(@Valid PracticeSessionPageReqVO pageReqVO) {
        PageResult<PracticeSessionDO> pageResult = practiceSessionService.getPracticeSessionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PracticeSessionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出练习会话 Excel")
    @PreAuthorize("@ss.hasPermission('english:practice-session:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPracticeSessionExcel(@Valid PracticeSessionPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PracticeSessionDO> list = practiceSessionService.getPracticeSessionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "练习会话.xls", "数据", PracticeSessionRespVO.class,
                        BeanUtils.toBean(list, PracticeSessionRespVO.class));
    }

}