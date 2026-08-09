package cn.kugua.module.english.controller.admin.vocab;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemePageReqVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemeRespVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemeSaveReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeDO;
import cn.kugua.module.english.service.vocab.VocabThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 词汇主题")
@RestController
@RequestMapping("/english/vocab-theme")
public class VocabThemeController {

    @Resource
    private VocabThemeService themeService;

    @PostMapping("/create")
    @Operation(summary = "创建主题")
    @PreAuthorize("@ss.hasPermission('english:vocab-theme:create')")
    public CommonResult<Long> create(@Valid @RequestBody VocabThemeSaveReqVO reqVO) {
        return success(themeService.createTheme(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新主题")
    @PreAuthorize("@ss.hasPermission('english:vocab-theme:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody VocabThemeSaveReqVO reqVO) {
        themeService.updateTheme(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除主题")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('english:vocab-theme:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        themeService.deleteTheme(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取主题")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('english:vocab-theme:query')")
    public CommonResult<VocabThemeRespVO> get(@RequestParam("id") Long id) {
        VocabThemeDO theme = themeService.getTheme(id);
        return success(BeanUtils.toBean(theme, VocabThemeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询主题")
    @PreAuthorize("@ss.hasPermission('english:vocab-theme:query')")
    public CommonResult<PageResult<VocabThemeRespVO>> page(@Valid VocabThemePageReqVO reqVO) {
        PageResult<VocabThemeDO> page = themeService.getThemePage(reqVO);
        return success(BeanUtils.toBean(page, VocabThemeRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "按级别查询已启用主题（前端下拉用）")
    @PreAuthorize("@ss.hasPermission('english:vocab-theme:query')")
    public CommonResult<List<VocabThemeRespVO>> listByLevel(@RequestParam(value = "levelCode", required = false) String levelCode) {
        return success(BeanUtils.toBean(themeService.getThemeListByLevel(levelCode), VocabThemeRespVO.class));
    }

}
