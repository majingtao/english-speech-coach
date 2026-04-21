package cn.kugua.module.english.controller.admin.aiModel;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelPageReqVO;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelRespVO;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelSaveReqVO;
import cn.kugua.module.english.dal.dataobject.aiModel.EscAiModelDO;
import cn.kugua.module.english.service.aiModel.EscAiModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI模型配置")
@RestController
@RequestMapping("/english/ai-model")
@Validated
public class EscAiModelController {

    @Resource
    private EscAiModelService aiModelService;

    @PostMapping("/create")
    @Operation(summary = "创建AI模型配置")
    @PreAuthorize("@ss.hasPermission('english:ai-model:create')")
    public CommonResult<Long> createAiModel(@Valid @RequestBody EscAiModelSaveReqVO createReqVO) {
        return success(aiModelService.createAiModel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新AI模型配置")
    @PreAuthorize("@ss.hasPermission('english:ai-model:update')")
    public CommonResult<Boolean> updateAiModel(@Valid @RequestBody EscAiModelSaveReqVO updateReqVO) {
        aiModelService.updateAiModel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除AI模型配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:ai-model:delete')")
    public CommonResult<Boolean> deleteAiModel(@RequestParam("id") Long id) {
        aiModelService.deleteAiModel(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除AI模型配置")
    @Parameter(name = "ids", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:ai-model:delete')")
    public CommonResult<Boolean> deleteAiModelList(@RequestParam("ids") List<Long> ids) {
        aiModelService.deleteAiModelListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得AI模型配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:ai-model:query')")
    public CommonResult<EscAiModelRespVO> getAiModel(@RequestParam("id") Long id) {
        EscAiModelDO aiModel = aiModelService.getAiModel(id);
        return success(BeanUtils.toBean(aiModel, EscAiModelRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得AI模型配置分页")
    @PreAuthorize("@ss.hasPermission('english:ai-model:query')")
    public CommonResult<PageResult<EscAiModelRespVO>> getAiModelPage(@Valid EscAiModelPageReqVO pageReqVO) {
        PageResult<EscAiModelDO> pageResult = aiModelService.getAiModelPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EscAiModelRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出AI模型配置 Excel")
    @PreAuthorize("@ss.hasPermission('english:ai-model:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAiModelExcel(@Valid EscAiModelPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EscAiModelDO> list = aiModelService.getAiModelPage(pageReqVO).getList();
        ExcelUtils.write(response, "AI模型配置.xls", "数据", EscAiModelRespVO.class,
                BeanUtils.toBean(list, EscAiModelRespVO.class));
    }
}
