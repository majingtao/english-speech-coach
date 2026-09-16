package cn.kugua.module.english.controller.admin.personalpractice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.personalpractice.vo.PersonalPracticePageReqVO;
import cn.kugua.module.english.controller.admin.personalpractice.vo.PersonalPracticeSaveReqVO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeDO;
import cn.kugua.module.english.service.personalpractice.PersonalPracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 专属练习")
@RestController
@RequestMapping("/english/personal-practice")
public class PersonalPracticeController {

    @Resource
    private PersonalPracticeService personalPracticeService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('english:personal-practice:create')")
    @Operation(summary = "创建专属练习")
    public CommonResult<Long> create(@Valid @RequestBody PersonalPracticeSaveReqVO reqVO) {
        return success(personalPracticeService.create(reqVO));
    }

    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('english:personal-practice:update')")
    @Operation(summary = "更新专属练习")
    public CommonResult<Boolean> update(@Valid @RequestBody PersonalPracticeSaveReqVO reqVO) {
        personalPracticeService.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermission('english:personal-practice:delete')")
    @Operation(summary = "删除专属练习")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        personalPracticeService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('english:personal-practice:query')")
    @Operation(summary = "获取专属练习")
    public CommonResult<PersonalPracticeDO> get(@RequestParam("id") Long id) {
        return success(personalPracticeService.get(id));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('english:personal-practice:query')")
    @Operation(summary = "分页查询专属练习")
    public CommonResult<PageResult<PersonalPracticeDO>> page(@Valid PersonalPracticePageReqVO reqVO) {
        return success(personalPracticeService.getPage(reqVO));
    }
}
