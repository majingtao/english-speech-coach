package cn.kugua.module.english.controller.admin.partInfoExchangeCard;

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

import cn.kugua.module.english.controller.admin.partInfoExchangeCard.vo.*;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeCard.PartInfoExchangeCardDO;
import cn.kugua.module.english.service.partInfoExchangeCard.PartInfoExchangeCardService;

@Tag(name = "管理后台 - 信息互换 - 卡片")
@RestController
@RequestMapping("/english/part-info-exchange-card")
@Validated
public class PartInfoExchangeCardController {

    @Resource
    private PartInfoExchangeCardService partInfoExchangeCardService;

    @PostMapping("/create")
    @Operation(summary = "创建信息互换 - 卡片")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-card:create')")
    public CommonResult<Long> createPartInfoExchangeCard(@Valid @RequestBody PartInfoExchangeCardSaveReqVO createReqVO) {
        return success(partInfoExchangeCardService.createPartInfoExchangeCard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新信息互换 - 卡片")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-card:update')")
    public CommonResult<Boolean> updatePartInfoExchangeCard(@Valid @RequestBody PartInfoExchangeCardSaveReqVO updateReqVO) {
        partInfoExchangeCardService.updatePartInfoExchangeCard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除信息互换 - 卡片")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-card:delete')")
    public CommonResult<Boolean> deletePartInfoExchangeCard(@RequestParam("id") Long id) {
        partInfoExchangeCardService.deletePartInfoExchangeCard(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除信息互换 - 卡片")
                @PreAuthorize("@ss.hasPermission('english:part-info-exchange-card:delete')")
    public CommonResult<Boolean> deletePartInfoExchangeCardList(@RequestParam("ids") List<Long> ids) {
        partInfoExchangeCardService.deletePartInfoExchangeCardListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得信息互换 - 卡片")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-card:query')")
    public CommonResult<PartInfoExchangeCardRespVO> getPartInfoExchangeCard(@RequestParam("id") Long id) {
        PartInfoExchangeCardDO partInfoExchangeCard = partInfoExchangeCardService.getPartInfoExchangeCard(id);
        return success(BeanUtils.toBean(partInfoExchangeCard, PartInfoExchangeCardRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得信息互换 - 卡片分页")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-card:query')")
    public CommonResult<PageResult<PartInfoExchangeCardRespVO>> getPartInfoExchangeCardPage(@Valid PartInfoExchangeCardPageReqVO pageReqVO) {
        PageResult<PartInfoExchangeCardDO> pageResult = partInfoExchangeCardService.getPartInfoExchangeCardPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartInfoExchangeCardRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出信息互换 - 卡片 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-info-exchange-card:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartInfoExchangeCardExcel(@Valid PartInfoExchangeCardPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartInfoExchangeCardDO> list = partInfoExchangeCardService.getPartInfoExchangeCardPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "信息互换 - 卡片.xls", "数据", PartInfoExchangeCardRespVO.class,
                        BeanUtils.toBean(list, PartInfoExchangeCardRespVO.class));
    }

}