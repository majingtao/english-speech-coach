package cn.kugua.module.english.controller.admin.synonym;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.kugua.module.english.controller.admin.synonym.vo.*;
import cn.kugua.module.english.dal.dataobject.synonym.SynonymPointDO;
import cn.kugua.module.english.service.synonym.SynonymService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 同义替换题库")
@RestController
@RequestMapping("/english/synonym")
public class SynonymController {
    @Resource private SynonymService service;

    @GetMapping("/point/page") @PreAuthorize("@ss.hasPermission('english:synonym:query')")
    public CommonResult<PageResult<SynonymPointDO>> page(@Valid SynonymPointPageReqVO req) {
        return success(service.getPointPage(req));
    }

    @GetMapping("/point/get") @PreAuthorize("@ss.hasPermission('english:synonym:query')")
    public CommonResult<SynonymPointDO> get(@RequestParam Long id) {
        return success(service.getPoint(id));
    }

    @PostMapping("/point/create") @PreAuthorize("@ss.hasPermission('english:synonym:create')")
    public CommonResult<Long> create(@Valid @RequestBody SynonymPointSaveReqVO req) {
        return success(service.createPoint(req));
    }

    @PutMapping("/point/update") @PreAuthorize("@ss.hasPermission('english:synonym:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody SynonymPointSaveReqVO req) {
        service.updatePoint(req);
        return success(true);
    }

    @DeleteMapping("/point/delete") @PreAuthorize("@ss.hasPermission('english:synonym:delete')")
    public CommonResult<Boolean> delete(@RequestParam Long id) {
        service.deletePoint(id);
        return success(true);
    }

    @PostMapping("/point/import") @PreAuthorize("@ss.hasPermission('english:synonym:create')")
    public CommonResult<Integer> importPoints(@Valid @RequestBody SynonymBatchImportReqVO req) {
        return success(service.importPoints(req));
    }
}
