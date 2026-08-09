package cn.kugua.module.english.controller.admin.readingmaterial;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.readingmaterial.vo.ReadingMaterialPageReqVO;
import cn.kugua.module.english.controller.admin.readingmaterial.vo.ReadingMaterialSaveReqVO;
import cn.kugua.module.english.dal.dataobject.readingmaterial.ReadingMaterialDO;
import cn.kugua.module.english.service.readingmaterial.ReadingMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 自由跟读素材")
@RestController
@RequestMapping("/english/reading-material")
public class ReadingMaterialController {

    @Resource
    private ReadingMaterialService readingMaterialService;

    @PostMapping("/create")
    @Operation(summary = "创建自由跟读素材")
    @PreAuthorize("@ss.hasPermission('english:reading-material:create')")
    public CommonResult<Long> createMaterial(@Valid @RequestBody ReadingMaterialSaveReqVO reqVO) {
        return success(readingMaterialService.createMaterial(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新自由跟读素材")
    @PreAuthorize("@ss.hasPermission('english:reading-material:update')")
    public CommonResult<Boolean> updateMaterial(@Valid @RequestBody ReadingMaterialSaveReqVO reqVO) {
        readingMaterialService.updateMaterial(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除自由跟读素材")
    @PreAuthorize("@ss.hasPermission('english:reading-material:delete')")
    public CommonResult<Boolean> deleteMaterial(@RequestParam("id") Long id) {
        readingMaterialService.deleteMaterial(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取自由跟读素材")
    @PreAuthorize("@ss.hasPermission('english:reading-material:query')")
    public CommonResult<ReadingMaterialDO> getMaterial(@RequestParam("id") Long id) {
        return success(readingMaterialService.getMaterial(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询自由跟读素材")
    @PreAuthorize("@ss.hasPermission('english:reading-material:query')")
    public CommonResult<PageResult<ReadingMaterialDO>> getMaterialPage(@Valid ReadingMaterialPageReqVO reqVO) {
        return success(readingMaterialService.getMaterialPage(reqVO));
    }
}
