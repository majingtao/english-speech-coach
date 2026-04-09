package cn.iocoder.yudao.module.english.controller.admin.partLongTurnPhoto;

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

import cn.iocoder.yudao.module.english.controller.admin.partLongTurnPhoto.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partLongTurnPhoto.PartLongTurnPhotoDO;
import cn.iocoder.yudao.module.english.service.partLongTurnPhoto.PartLongTurnPhotoService;

@Tag(name = "管理后台 - 独立长答 - 图片描述")
@RestController
@RequestMapping("/english/part-long-turn-photo")
@Validated
public class PartLongTurnPhotoController {

    @Resource
    private PartLongTurnPhotoService partLongTurnPhotoService;

    @PostMapping("/create")
    @Operation(summary = "创建独立长答 - 图片描述")
    @PreAuthorize("@ss.hasPermission('english:part-long-turn-photo:create')")
    public CommonResult<Long> createPartLongTurnPhoto(@Valid @RequestBody PartLongTurnPhotoSaveReqVO createReqVO) {
        return success(partLongTurnPhotoService.createPartLongTurnPhoto(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新独立长答 - 图片描述")
    @PreAuthorize("@ss.hasPermission('english:part-long-turn-photo:update')")
    public CommonResult<Boolean> updatePartLongTurnPhoto(@Valid @RequestBody PartLongTurnPhotoSaveReqVO updateReqVO) {
        partLongTurnPhotoService.updatePartLongTurnPhoto(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除独立长答 - 图片描述")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:part-long-turn-photo:delete')")
    public CommonResult<Boolean> deletePartLongTurnPhoto(@RequestParam("id") Long id) {
        partLongTurnPhotoService.deletePartLongTurnPhoto(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除独立长答 - 图片描述")
                @PreAuthorize("@ss.hasPermission('english:part-long-turn-photo:delete')")
    public CommonResult<Boolean> deletePartLongTurnPhotoList(@RequestParam("ids") List<Long> ids) {
        partLongTurnPhotoService.deletePartLongTurnPhotoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得独立长答 - 图片描述")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:part-long-turn-photo:query')")
    public CommonResult<PartLongTurnPhotoRespVO> getPartLongTurnPhoto(@RequestParam("id") Long id) {
        PartLongTurnPhotoDO partLongTurnPhoto = partLongTurnPhotoService.getPartLongTurnPhoto(id);
        return success(BeanUtils.toBean(partLongTurnPhoto, PartLongTurnPhotoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得独立长答 - 图片描述分页")
    @PreAuthorize("@ss.hasPermission('english:part-long-turn-photo:query')")
    public CommonResult<PageResult<PartLongTurnPhotoRespVO>> getPartLongTurnPhotoPage(@Valid PartLongTurnPhotoPageReqVO pageReqVO) {
        PageResult<PartLongTurnPhotoDO> pageResult = partLongTurnPhotoService.getPartLongTurnPhotoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PartLongTurnPhotoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出独立长答 - 图片描述 Excel")
    @PreAuthorize("@ss.hasPermission('english:part-long-turn-photo:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPartLongTurnPhotoExcel(@Valid PartLongTurnPhotoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PartLongTurnPhotoDO> list = partLongTurnPhotoService.getPartLongTurnPhotoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "独立长答 - 图片描述.xls", "数据", PartLongTurnPhotoRespVO.class,
                        BeanUtils.toBean(list, PartLongTurnPhotoRespVO.class));
    }

}