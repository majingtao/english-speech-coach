package cn.kugua.module.english.controller.admin.expression;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionItemPageReqVO;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionItemSaveReqVO;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionThemeSaveReqVO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionItemDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionThemeDO;
import cn.kugua.module.english.service.expression.ExpressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 表达练习")
@RestController
@RequestMapping("/english/expression")
public class ExpressionController {

    @Resource
    private ExpressionService expressionService;

    @PostMapping("/theme/create")
    @Operation(summary = "创建表达主题")
    @PreAuthorize("@ss.hasPermission('english:expression:create')")
    public CommonResult<Long> createTheme(@Valid @RequestBody ExpressionThemeSaveReqVO reqVO) {
        return success(expressionService.createTheme(reqVO));
    }

    @PutMapping("/theme/update")
    @Operation(summary = "更新表达主题")
    @PreAuthorize("@ss.hasPermission('english:expression:update')")
    public CommonResult<Boolean> updateTheme(@Valid @RequestBody ExpressionThemeSaveReqVO reqVO) {
        expressionService.updateTheme(reqVO);
        return success(true);
    }

    @DeleteMapping("/theme/delete")
    @Operation(summary = "删除表达主题")
    @PreAuthorize("@ss.hasPermission('english:expression:delete')")
    public CommonResult<Boolean> deleteTheme(@RequestParam("id") Long id) {
        expressionService.deleteTheme(id);
        return success(true);
    }

    @GetMapping("/theme/list")
    @Operation(summary = "表达主题列表")
    @PreAuthorize("@ss.hasPermission('english:expression:query')")
    public CommonResult<List<ExpressionThemeDO>> themeList(
            @RequestParam(value = "level", required = false) String level) {
        return success(expressionService.getAllThemes(level));
    }

    @PostMapping("/item/create")
    @Operation(summary = "创建表达练习项")
    @PreAuthorize("@ss.hasPermission('english:expression:create')")
    public CommonResult<Long> createItem(@Valid @RequestBody ExpressionItemSaveReqVO reqVO) {
        return success(expressionService.createItem(reqVO));
    }

    @PutMapping("/item/update")
    @Operation(summary = "更新表达练习项")
    @PreAuthorize("@ss.hasPermission('english:expression:update')")
    public CommonResult<Boolean> updateItem(@Valid @RequestBody ExpressionItemSaveReqVO reqVO) {
        expressionService.updateItem(reqVO);
        return success(true);
    }

    @DeleteMapping("/item/delete")
    @Operation(summary = "删除表达练习项")
    @PreAuthorize("@ss.hasPermission('english:expression:delete')")
    public CommonResult<Boolean> deleteItem(@RequestParam("id") Long id) {
        expressionService.deleteItem(id);
        return success(true);
    }

    @GetMapping("/item/get")
    @Operation(summary = "获取表达练习项")
    @PreAuthorize("@ss.hasPermission('english:expression:query')")
    public CommonResult<ExpressionItemDO> getItem(@RequestParam("id") Long id) {
        return success(expressionService.getItem(id));
    }

    @GetMapping("/item/page")
    @Operation(summary = "分页查询表达练习项")
    @PreAuthorize("@ss.hasPermission('english:expression:query')")
    public CommonResult<PageResult<ExpressionItemDO>> itemPage(@Valid ExpressionItemPageReqVO reqVO) {
        return success(expressionService.getItemPage(reqVO));
    }
}
